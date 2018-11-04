package org.bk.ass;

import static org.bk.ass.AgentUtil.distanceSquared;
import static org.bk.ass.AgentUtil.moveToward;

public class HealerSimulator {

  // Retrieved from OpenBW
  public static final int MEDICS_HEAL_RANGE_SQUARED = 30 * 30;

  public boolean simUnit(Agent agent, UnorderedCollection<Agent> allies) {
    if (agent.energyShifted < 256) {
      return true;
    }
    Agent selectedAlly = null;
    int selectedDistanceSquared = Integer.MAX_VALUE;
    for (int i = 0; i < allies.size(); i++) {
      Agent ally = allies.get(i);
      if (ally.isOrganic
          && ally.healthShifted < ally.maxHealthShifted
          && !agent.healedThisFrame
          && ally != agent) {

        int distance = distanceSquared(agent, ally);
        if (distance < selectedDistanceSquared) {
          selectedDistanceSquared = distance;
          selectedAlly = ally;

          // If we can heal it this frame, we're done searching
          if (selectedDistanceSquared <= MEDICS_HEAL_RANGE_SQUARED) {
            break;
          }
        }
      }
    }

    if (selectedAlly == null) {
      return false;
    }

    moveToward(agent, selectedAlly, selectedDistanceSquared);
    if (selectedDistanceSquared > MEDICS_HEAL_RANGE_SQUARED) {
      return true;
    }
    agent.energyShifted -= 256;
    selectedAlly.healedThisFrame = true;
    selectedAlly.healthShifted += 150;
    if (selectedAlly.healthShifted > selectedAlly.maxHealthShifted) {
      selectedAlly.healthShifted = selectedAlly.maxHealthShifted;
    }

    return true;
  }
}