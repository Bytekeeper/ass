package org.bk.ass;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openbw.bwapi4j.test.BWDataProvider;
import org.openbw.bwapi4j.type.UnitType;

class SimulatorTest {

  private Simulator simulator = new Simulator();
  private BWAPI4JAgentFactory factory = new BWAPI4JAgentFactory(null);

  @BeforeAll
  static void setup() throws Exception {
    BWDataProvider.injectValues();
  }

  @Test
  void stimmedVsUnstimmed() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine).setCanStim(true));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine).setCanStim(true));
    simulator.addAgentB(factory.of(UnitType.Terran_Medic));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void MMVsSunkens() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Medic));
    simulator.addAgentA(factory.of(UnitType.Terran_Medic));
    simulator.addAgentA(factory.of(UnitType.Terran_Medic));
    simulator.addAgentB(factory.of(UnitType.Zerg_Sunken_Colony));
    simulator.addAgentB(factory.of(UnitType.Zerg_Sunken_Colony));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).size().isLessThanOrEqualTo(5);
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(1);
  }

  @Test
  void MMvsMM() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Medic));
    simulator.addAgentA(factory.of(UnitType.Terran_Medic));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Medic));
    simulator.addAgentB(factory.of(UnitType.Terran_Medic));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).size().isLessThanOrEqualTo(2);
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(2);
  }

  @Test
  void vultureVs4Zergling() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Vulture).setX(20).setY(50));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(2);
  }

  @Test
  void twoMarinesVsOneToTheDeath() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void marineVsValkyrieSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Terran_Valkyrie));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsB())
        .element(0)
        .hasFieldOrPropertyWithValue("health", (UnitType.Terran_Valkyrie.maxHitPoints() - 4));
  }

  @Test
  void lurkerVsTwoOpposingMarinesSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(0));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(60));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker).setX(30).setBurrowed(true));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).extracting("health").contains(20, 40);
  }

  @Test
  void lurkerVsTwoMarinesSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(20));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(40));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker).setBurrowed(true));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).extracting("health").containsOnly(20);
  }

  @Test
  void lurkerVsOneMarineAndOneInSplashRangeSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(20));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(200));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker).setBurrowed(true));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).extracting("health").containsOnly(20);
  }

  @Test
  void lurkerVsOneMarineAndOneOutOfSplashRangeSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(20));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(192 + 20 + 1));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker).setBurrowed(true));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).extracting("health").containsOnly(40, 20);
  }

  @Test
  void fireBatVs2Lings() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Firebat).setX(20));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void GoonCloseToSiegedTank() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Protoss_Dragoon));
    simulator.addAgentB(factory.of(UnitType.Terran_Siege_Tank_Siege_Mode));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void mutaVs3GhostsSingleFrame() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk).setX(20));
    simulator.addAgentB(factory.of(UnitType.Terran_Ghost));
    simulator.addAgentB(factory.of(UnitType.Terran_Ghost));
    simulator.addAgentB(factory.of(UnitType.Terran_Ghost));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsB()).extracting("health").containsOnly(45 - 9, 45 - 3, 45 - 1);
  }

  @Test
  void GoonAwayFromSiegedTank() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Protoss_Dragoon).setX(1000));
    simulator.addAgentB(factory.of(UnitType.Terran_Siege_Tank_Siege_Mode));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _3ZerglingAwayFromSiegedTankAndMarine() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1000).setY(20));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1000));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1000).setY(40));
    simulator.addAgentB(factory.of(UnitType.Terran_Siege_Tank_Siege_Mode));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _6ZerglingAwayFromSiegedTankAndMarine() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1200));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(2000));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1200).setY(600));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(2000).setY(600));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(1200).setY(300));
    simulator.addAgentA(factory.of(UnitType.Zerg_Zergling).setX(2000).setY(300));

    simulator.addAgentB(factory.of(UnitType.Terran_Siege_Tank_Siege_Mode).setX(1600).setY(300));
    simulator.addAgentB(factory.of(UnitType.Terran_Marine).setX(1600).setY(300));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void GoonVsTank() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Protoss_Dragoon));
    simulator.addAgentB(factory.of(UnitType.Terran_Siege_Tank_Tank_Mode));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void MarineVsLurker() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void MarineVsBurrowedLurker() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Marine));
    simulator.addAgentB(factory.of(UnitType.Zerg_Lurker).setBurrowed(true));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _2LurkersVs10Marines() {
    // GIVEN
    simulator
        .addAgentA(factory.of(UnitType.Zerg_Lurker).setBurrowed(true).setX(200).setY(200))
        .addAgentA(factory.of(UnitType.Zerg_Lurker).setBurrowed(true).setX(210).setY(200));

    for (int i = 0; i < 10; i++) {
      simulator.addAgentB(factory.of(UnitType.Terran_Marine).setX(10 * i).setY(20));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void _2LurkersVs12Marines() {
    // GIVEN
    simulator
        .addAgentA(factory.of(UnitType.Zerg_Lurker).setBurrowed(true))
        .addAgentA(factory.of(UnitType.Zerg_Lurker).setBurrowed(true));

    for (int i = 0; i < 12; i++) {
      simulator.addAgentB(factory.of(UnitType.Terran_Marine).setX(10 * i).setY(20));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _7MutaVs1BunkerAndSCV() {
    // GIVEN
    for (int i = 0; i < 7; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    }
    simulator.addAgentB(factory.of(UnitType.Terran_Bunker));
    simulator.addAgentB(factory.of(UnitType.Terran_SCV));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void _7MutaVs1BunkerAnd4SCVs() {
    // GIVEN
    for (int i = 0; i < 7; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    }
    simulator.addAgentB(factory.of(UnitType.Terran_Bunker));
    for (int i = 0; i < 4; i++) {
      simulator.addAgentB(factory.of(UnitType.Terran_SCV));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _5MutaVs1Bunker() {
    // GIVEN
    for (int i = 0; i < 5; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    }
    simulator.addAgentB(factory.of(UnitType.Terran_Bunker));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _7MutasVs9Hydras() {
    // GIVEN
    for (int i = 0; i < 7; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    }
    for (int i = 0; i < 9; i++) {
      simulator.addAgentB(factory.of(UnitType.Zerg_Hydralisk));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).size().isLessThanOrEqualTo(3);
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(1);
  }

  @Test
  void _13DragoonsVs10Hydras() {
    // GIVEN
    for (int i = 0; i < 13; i++) {
      simulator.addAgentA(factory.of(UnitType.Protoss_Dragoon).setX(1000 + i * 8).setY(1000));
    }
    for (int i = 0; i < 10; i++) {
      simulator.addAgentB(
          factory
              .of(UnitType.Zerg_Hydralisk, 0, 0, 0, 0, false, false)
              .setX(1000 + i * 8)
              .setY(1200));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).size().isLessThanOrEqualTo(2);
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void _13DragoonsVs10UpgradedHydras() {
    // GIVEN
    for (int i = 0; i < 13; i++) {
      simulator.addAgentA(factory.of(UnitType.Protoss_Dragoon).setX(400).setY(400));
    }
    for (int i = 0; i < 10; i++) {
      simulator.addAgentB(factory.of(UnitType.Zerg_Hydralisk, 0, 0, 32, 32, true, false).setX(200));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(3);
  }

  @Test
  void _13MarinesVs10Hydras() {
    // GIVEN
    for (int i = 0; i < 13; i++) {
      simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(200 + i * 8).setY(400));
    }
    for (int i = 0; i < 10; i++) {
      simulator.addAgentB(factory.of(UnitType.Zerg_Hydralisk).setX(200 + i * 8).setY(500));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).size().isLessThanOrEqualTo(3);
  }

  @Test
  void MutaVsVulture() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    simulator.addAgentB(factory.of(UnitType.Terran_Vulture));

    // WHEN
    simulator.simulate(200);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void LargeArmiesTest() {
    // GIVEN
    for (int i = 0; i < 1000; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
      simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isNotEmpty();
    assertThat(simulator.getAgentsB()).isEmpty();
  }

  @Test
  void _10HydrasVsDT() {
    // GIVEN
    for (int i = 0; i < 10; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Hydralisk).setX(1000 + i * 8).setY(1000));
    }
    simulator.addAgentB(
        factory.of(UnitType.Protoss_Dark_Templar).setDetected(false).setX(1000).setY(1100));

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void _7MutasVs14Marines() {
    // GIVEN
    for (int i = 0; i < 7; i++) {
      simulator.addAgentA(factory.of(UnitType.Zerg_Mutalisk));
    }
    for (int i = 0; i < 14; i++) {
      simulator.addAgentB(factory.of(UnitType.Terran_Marine));
    }

    // WHEN
    simulator.simulate(-1);

    // THEN
    assertThat(simulator.getAgentsA()).isEmpty();
    assertThat(simulator.getAgentsB()).isNotEmpty();
  }

  @Test
  void tankSplashShouldAffectOwnUnits() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Terran_Siege_Tank_Siege_Mode));
    simulator.addAgentA(factory.of(UnitType.Terran_Marine).setX(100));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling).setX(100));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).hasSize(1);
  }

  @Test
  void archonSplashShouldNotAffectOwnUnits() {
    // GIVEN
    simulator.addAgentA(factory.of(UnitType.Protoss_Archon));
    simulator.addAgentA(factory.of(UnitType.Protoss_Zealot).setX(48));
    simulator.addAgentB(factory.of(UnitType.Zerg_Zergling).setX(48));

    // WHEN
    simulator.simulate(1);

    // THEN
    assertThat(simulator.getAgentsA()).element(1)
        .hasFieldOrPropertyWithValue("shieldsShifted", 14087);
  }
}