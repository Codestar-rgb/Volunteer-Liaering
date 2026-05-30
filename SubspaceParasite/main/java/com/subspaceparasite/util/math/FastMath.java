package com.subspaceparasite.util.math;

/**
 * 快速数学工具类，提供高性能的三角函数和常用数学运算。
 * <p>
 * 特性：
 * - 查找表加速三角函数计算
 * - 位运算优化的整数操作
 * - SIMD 友好的批量处理
 * 
 * @author SubspaceParasite Team
 * @version 2.0
 */
public final class FastMath {
    
    private static final int SIN_MASK = 0xFFF; // 4096 个条目
    private static final float[] SIN_TABLE = new float[SIN_MASK + 1];
    private static final float[] COS_TABLE = new float[SIN_MASK + 1];
    
    static {
        // 预计算正弦和余弦查找表
        for (int i = 0; i <= SIN_MASK; i++) {
            double angle = 2.0 * Math.PI * i / (SIN_MASK + 1);
            SIN_TABLE[i] = (float) Math.sin(angle);
            COS_TABLE[i] = (float) Math.cos(angle);
        }
    }
    
    private FastMath() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    
    /**
     * 快速正弦计算（使用查找表）。
     * 
     * @param radians 弧度值
     * @return 正弦值
     */
    public static float sin(float radians) {
        int index = (int) (radians * (SIN_MASK + 1) / (2.0f * Math.PI)) & SIN_MASK;
        return SIN_TABLE[index];
    }
    
    /**
     * 快速余弦计算（使用查找表）。
     * 
     * @param radians 弧度值
     * @return 余弦值
     */
    public static float cos(float radians) {
        int index = (int) (radians * (SIN_MASK + 1) / (2.0f * Math.PI)) & SIN_MASK;
        return COS_TABLE[index];
    }
    
    /**
     * 快速正切计算。
     * 
     * @param radians 弧度值
     * @return 正切值
     */
    public static float tan(float radians) {
        float c = cos(radians);
        if (Math.abs(c) < 1e-6f) {
            return Float.POSITIVE_INFINITY;
        }
        return sin(radians) / c;
    }
    
    /**
     * 快速反正弦计算（使用泰勒展开近似）。
     * 
     * @param x 输入值 [-1, 1]
     * @return 反正弦值（弧度）
     */
    public static float asin(float x) {
        // 边界检查
        if (x >= 1.0f) return (float) Math.PI / 2.0f;
        if (x <= -1.0f) return -(float) Math.PI / 2.0f;
        
        // 泰勒展开近似：asin(x) ≈ x + x³/6 + 3x⁵/40
        float x2 = x * x;
        return x + x * x2 / 6.0f + 3.0f * x * x2 * x2 / 40.0f;
    }
    
    /**
     * 快速平方根倒数（雷神之锤 III 算法）。
     * 
     * @param x 输入值
     * @return 1/√x
     */
    public static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1); // 魔法数字
        x = Float.intBitsToFloat(i);
        x = x * (1.5f - xhalf * x * x); // 牛顿迭代一次
        return x;
    }
    
    /**
     * 快速平方根。
     * 
     * @param x 输入值
     * @return √x
     */
    public static float sqrt(float x) {
        return x * invSqrt(x);
    }
    
    /**
     * 快速归一化角度到 [0, 2π) 范围。
     * 
     * @param angle 角度（弧度）
     * @return 归一化后的角度
     */
    public static float normalizeAngle(float angle) {
        angle %= (2.0f * Math.PI);
        if (angle < 0) {
            angle += 2.0f * Math.PI;
        }
        return angle;
    }
    
    /**
     * 快速线性插值。
     * 
     * @param a 起始值
     * @param b 目标值
     * @param t 插值因子 [0, 1]
     * @return 插值结果
     */
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
    
    /**
     * 快速双线性插值。
     * 
     * @param v00 左上角值
     * @param v01 右上角值
     * @param v10 左下角值
     * @param v11 右下角值
     * @param x X 方向插值因子
     * @param y Y 方向插值因子
     * @return 插值结果
     */
    public static float bilerp(float v00, float v01, float v10, float v11, float x, float y) {
        return lerp(lerp(v00, v01, x), lerp(v10, v11, x), y);
    }
    
    /**
     * 平滑步进函数（Smoothstep）。
     * 
     * @param edge0 下边界
     * @param edge1 上边界
     * @param x 输入值
     * @return 平滑插值结果 [0, 1]
     */
    public static float smoothstep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
    
    /**
     * 更平滑的步进函数（Smootherstep）。
     * 
     * @param edge0 下边界
     * @param edge1 上边界
     * @param x 输入值
     * @return 平滑插值结果 [0, 1]
     */
    public static float smootherstep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
    }
    
    /**
     * 快速钳制函数。
     * 
     * @param value 输入值
     * @param min 最小值
     * @param max 最大值
     * @return 钳制后的值
     */
    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    
    /**
     * 快速整数钳制函数。
     * 
     * @param value 输入值
     * @param min 最小值
     * @param max 最大值
     * @return 钳制后的值
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    
    /**
     * 快速绝对值（使用位运算）。
     * 
     * @param x 输入值
     * @return 绝对值
     */
    public static int abs(int x) {
        int mask = x >> 31;
        return (x ^ mask) - mask;
    }
    
    /**
     * 快速最大值。
     * 
     * @param a 第一个值
     * @param b 第二个值
     * @return 较大值
     */
    public static float max(float a, float b) {
        return a > b ? a : b;
    }
    
    /**
     * 快速最小值。
     * 
     * @param a 第一个值
     * @param b 第二个值
     * @return 较小值
     */
    public static float min(float a, float b) {
        return a < b ? a : b;
    }
    
    /**
     * 判断浮点数是否接近零。
     * 
     * @param value 输入值
     * @param epsilon 容差
     * @return 如果 |value| < epsilon 则返回 true
     */
    public static boolean isNearZero(float value, float epsilon) {
        return Math.abs(value) < epsilon;
    }
    
    /**
     * 快速距离平方计算（避免开方）。
     * 
     * @param x1 点 1 的 X 坐标
     * @param z1 点 1 的 Z 坐标
     * @param x2 点 2 的 X 坐标
     * @param z2 点 2 的 Z 坐标
     * @return 距离的平方
     */
    public static float distSq(float x1, float z1, float x2, float z2) {
        float dx = x2 - x1;
        float dz = z2 - z1;
        return dx * dx + dz * dz;
    }
    
    /**
     * 快速三维距离平方计算。
     * 
     * @param x1 点 1 的 X 坐标
     * @param y1 点 1 的 Y 坐标
     * @param z1 点 1 的 Z 坐标
     * @param x2 点 2 的 X 坐标
     * @param y2 点 2 的 Y 坐标
     * @param z2 点 2 的 Z 坐标
     * @return 距离的平方
     */
    public static float distSq3D(float x1, float y1, float z1, float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        return dx * dx + dy * dy + dz * dz;
    }
}
