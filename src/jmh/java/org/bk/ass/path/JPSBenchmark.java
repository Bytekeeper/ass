package org.bk.ass.path;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import javax.imageio.ImageIO;
import org.bk.ass.path.JPS.Map;
import org.bk.ass.path.JPS.Position;
import org.bk.ass.path.JPS.Result;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@Measurement(iterations = 3, time = 5)
@Fork(3)
public class JPSBenchmark {

  @State(Scope.Thread)
  public static class MyState {

    List<Position[]> positions;
    Map map;
    JPS jps;

    @Setup
    public void setup() throws IOException {
      ImageIO.setUseCache(false);
      BufferedImage image = ImageIO.read(JPSTest.class.getResourceAsStream("/dungeon_map.bmp"));
      boolean[][] data = new boolean[image.getWidth()][image.getHeight()];
      for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
          data[y][x] = image.getRGB(x, y) == -1;
        }
      }
      map = Map.fromBooleanArray(data);
      jps = new JPS(map);

      SplittableRandom rnd = new SplittableRandom(98765);
      positions = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        Position start;
        do {
          start = new Position(rnd.nextInt(image.getWidth()), rnd.nextInt(image.getHeight()));
        } while (map.isWalkable(start.x, start.y));
        Position end;
        do {
          end = new Position(rnd.nextInt(image.getWidth()), rnd.nextInt(image.getHeight()));
        } while (map.isWalkable(end.x, end.y));
        positions.add(new Position[]{start, end});
      }
    }
  }

  @Benchmark
  public List<Result> path100RandomStartToEnd(MyState state) {
    List<Result> results = new ArrayList<>();
    for (Position[] p : state.positions) {
      results.add(state.jps.findPath(p[0], p[1]));
    }
    return results;
  }
}