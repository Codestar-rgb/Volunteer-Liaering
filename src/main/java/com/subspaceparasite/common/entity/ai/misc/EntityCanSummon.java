package com.subspaceparasite.common.entity.ai.misc;

import java.util.List;

/**
 * Interface for entities that can summon minions with point budget system.
 */
public interface EntityCanSummon {

    /**
     * Add an entity ID with point cost to the summon budget.
     */
    void addID(int id, int points);

    /**
     * Check if the summon list is available/initialized.
     */
    boolean IDable();

    /**
     * Check if there are valid IDs available for summoning.
     */
    boolean checkID();

    /**
     * Get the list of available entity IDs for summoning.
     */
    List<Integer> getIDList();

    /**
     * Get the list of point costs for each ID.
     */
    List<Integer> getPointList();

    /**
     * Get the total point budget for summoning.
     */
    int getTotalParasites();

    /**
     * Get the current number of active summoned parasites.
     */
    int getActualParasites();

    /**
     * Set the current number of active summoned parasites.
     */
    void setActualParasites(int count);
}
