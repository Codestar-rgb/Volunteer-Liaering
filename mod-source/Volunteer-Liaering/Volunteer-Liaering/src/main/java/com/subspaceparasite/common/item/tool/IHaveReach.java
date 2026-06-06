package com.subspaceparasite.common.item.tool;

/**
 * Interface for items that have custom reach distance.
 * Ported from original SRP IHaveReach.
 */
public interface IHaveReach {
    /**
     * @return The additional reach distance for this weapon.
     */
    float getReach();
}
