package com.subspaceparasite.util.cache;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 高性能对象池实现，用于减少 GC 压力。
 * <p>
 * 特性：
 * - 可配置的最大容量
 * - 懒初始化
 * - 自动清理过期对象
 * - 线程安全（使用同步双端队列）
 * 
 * @param <T> 池中对象的类型
 * @author SubspaceParasite Team
 * @version 2.0
 */
public final class ObjectPool<T extends Poolable<T>> {
    
    private final Deque<T> pool;
    private final int maxSize;
    private final Supplier<T> factory;
    private final int maxAge; // 对象最大存活时间（tick），-1 表示永不过期
    
    /**
     * 创建有界对象池。
     * 
     * @param maxSize 最大容量
     * @param factory 对象工厂
     * @param maxAge 对象最大存活时间（tick），-1 表示永不过期
     */
    public ObjectPool(int maxSize, Supplier<T> factory, int maxAge) {
        this.maxSize = Math.max(1, maxSize);
        this.factory = Objects.requireNonNull(factory, "Factory cannot be null");
        this.maxAge = maxAge;
        this.pool = new LinkedList<>();
    }
    
    /**
     * 从池中获取一个对象。
     * <p>
     * 如果池为空，则创建新对象；否则复用池中对象。
     * 
     * @return 可用的对象实例
     */
    public synchronized T acquire() {
        T obj;
        if (pool.isEmpty()) {
            obj = factory.get();
            obj.setAcquiredTime(System.nanoTime());
        } else {
            obj = pool.removeFirst();
            // 检查对象是否过期
            if (maxAge > 0 && obj.getAge() > maxAge) {
                // 对象已过期，创建新对象
                obj = factory.get();
                obj.setAcquiredTime(System.nanoTime());
            }
        }
        obj.reset();
        return obj;
    }
    
    /**
     * 将对象归还到池中。
     * <p>
     * 如果池已满，则丢弃该对象。
     * 
     * @param obj 要归还的对象
     */
    public synchronized void release(T obj) {
        if (obj == null) return;
        
        if (pool.size() < maxSize) {
            obj.markAsReleased();
            pool.addLast(obj);
        }
        // 否则对象将被垃圾回收
    }
    
    /**
     * 获取池中可用对象数量。
     * 
     * @return 池中对象数量
     */
    public synchronized int size() {
        return pool.size();
    }
    
    /**
     * 清空池中所有对象。
     */
    public synchronized void clear() {
        pool.forEach(Poolable::onDestroy);
        pool.clear();
    }
    
    /**
     * 预填充池到指定大小。
     * 
     * @param count 预填充数量
     */
    public synchronized void prefill(int count) {
        for (int i = 0; i < Math.min(count, maxSize); i++) {
            T obj = factory.get();
            obj.markAsReleased();
            pool.addLast(obj);
        }
    }
    
    /**
     * 可池化对象的接口。
     * <p>
     * 实现此接口的对象必须提供重置和清理逻辑。
     * 
     * @param <T> 实现类的类型
     */
    public interface Poolable<T extends Poolable<T>> {
        /**
         * 重置对象状态以供复用。
         * <p>
         * 此方法在对象从池中取出时调用。
         */
        void reset();
        
        /**
         * 对象被销毁时的清理逻辑。
         * <p>
         * 此方法在池清空或对象过期时调用。
         */
        default void onDestroy() {}
        
        /**
         * 标记对象已被释放回池中。
         */
        default void markAsReleased() {}
        
        /**
         * 设置获取时间戳。
         * 
         * @param timeNanos 纳秒级时间戳
         */
        default void setAcquiredTime(long timeNanos) {}
        
        /**
         * 获取对象年龄（以 tick 为单位）。
         * 
         * @return 对象年龄
         */
        default int getAge() { return -1; }
    }
}
