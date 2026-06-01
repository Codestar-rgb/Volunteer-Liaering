package com.subspaceparasite.util.cache;

/**
 * Interface for objects that can be pooled in an {@link ObjectPool}.
 * <p>
 * Implementations must provide reset logic for object reuse and
 * optionally handle lifecycle events like destruction and age tracking.
 *
 * @param <T> the implementing type
 * @author SubspaceParasite Team
 */
public interface Poolable<T extends Poolable<T>> {
    /**
     * Reset object state for reuse.
     * Called when the object is acquired from the pool.
     */
    void reset();

    /**
     * Cleanup logic when the object is destroyed.
     * Called when the pool is cleared or the object expires.
     */
    default void onDestroy() {}

    /**
     * Mark this object as released back to the pool.
     */
    default void markAsReleased() {}

    /**
     * Set the timestamp when this object was acquired.
     *
     * @param timeNanos nanosecond timestamp
     */
    default void setAcquiredTime(long timeNanos) {}

    /**
     * Get the age of this object in ticks.
     *
     * @return the age, or -1 if not tracked
     */
    default int getAge() { return -1; }
}
