# SRP 1.20.1 Port - 架构优化与技术规范 v2.0

## 核心设计原则

### 1. 零容忍错误处理 (Zero-Tolerance Error Handling)
```java
// ✅ 强制使用 Result 模式处理可能失败的操作
public sealed class EntitySpawnResult {
    public record Success(EntityParasiteBase entity) implements EntitySpawnResult {}
    public record Failure(SpawnFailureReason reason, String message) implements EntitySpawnResult {}
}

// ✅ 使用 Optional 明确表示可能为空的返回值
public Optional<EntityParasiteBase> tryGetOwner() {
    return Optional.ofNullable(this.owner);
}
```

### 2. 编译时安全 (Compile-Time Safety)
- 使用 Java 17+ 特性：`sealed`, `record`, `pattern matching`
- 所有状态机使用 `enum` + `sealed interface` 确保穷尽性检查
- 使用注解处理器进行编译时验证

### 3. 性能优先架构 (Performance-First Architecture)

#### 3.1 对象池化系统
```java
// 预分配 + 对象池，零 GC 压力
public final class ParticlePool {
    private static final ObjectPool<ParticleData> POOL = 
        ObjectPool.bounded(1000, ParticleData::new, ParticleData::reset);
    
    public static ParticleData acquire() { return POOL.acquire(); }
    public static void release(ParticleData data) { POOL.release(data); }
}
```

#### 3.2 缓存友好数据结构
```java
// 使用数组而非 HashMap 存储高频访问数据
private final float[] geneFloats = new float[GeneType.floatGeneCount()]; // 直接索引 O(1)
private final boolean[] abilities = new boolean[AbilityType.COUNT];     // 位图压缩
```

#### 3.3 SIMD 友好计算
```java
// 批量处理时使用 Vector API (Java 19+)
var species = FloatVector.SPECIES_256;
FloatVector healthVec = FloatVector.fromArray(species, healthArray, 0);
FloatVector damageVec = FloatVector.fromArray(species, damageArray, 0);
healthVec.sub(damageVec).intoArray(resultArray, 0);
```

### 4. 并发安全设计 (Concurrency-Safe Design)

#### 4.1 无共享架构
```java
// 每线程本地数据，避免锁竞争
private static final ThreadLocal<EvolutionContext> EVOLUTION_CONTEXT = 
    ThreadLocal.withInitial(EvolutionContext::new);
```

#### 4.2 不可变数据传输
```java
// DTO 使用 record 保证不可变性
public record EvolutionSnapshot(
    EvoPhase phase,
    int killCount,
    float infectionLevel,
    long timestamp
) implements Serializable {}
```

#### 4.3 响应式事件总线
```java
// 使用 Loom 虚拟线程处理异步事件
@EventSubscriber(async = true, scheduler = Scheduler.VIRTUAL_THREAD)
public void onEntityHurt(EntityHurtEvent event) {
    // 非阻塞处理
}
```

### 5. 可观测性设计 (Observability by Design)

#### 5.1 结构化日志
```java
// 使用 Key-Value 日志便于机器解析
LOGGER.info(Map.of(
    "event", "entity_evolution",
    "entity_id", entity.getId(),
    "old_phase", oldPhase.name(),
    "new_phase", newPhase.name(),
    "kill_count", entity.getKillCount()
));
```

#### 5.2 指标收集
```java
// Micrometer 指标集成
private final Timer evolutionTimer = Metrics.timer("parasite.evolution.duration");
private final Counter spawnCounter = Metrics.counter("parasite.spawn.total");

public void evolve() {
    evolutionTimer.record(() -> {
        // evolution logic
    });
}
```

#### 5.3 分布式追踪
```java
// OpenTelemetry 追踪跨度
try (Span span = Tracer.startSpan("parasite.tick")) {
    span.setAttribute("entity.type", this.getType().toString());
    // tick logic
}
```

---

## 目录结构标准

```
src/main/java/com/subspaceparasite/
├── SubspaceParasite.java              # 主入口
├── api/                               # 公共 API（稳定接口）
│   ├── capability/
│   │   ├── IParasiteCapability.java
│   │   ├── IEvolutionCapability.java
│   │   └── IInfectionCapability.java
│   ├── event/                         # 事件接口
│   │   ├── ParasiteEvent.java
│   │   ├── EvolutionEvent.java
│   │   └── InfectionEvent.java
│   └── parasite/                      # 枚举类型
│       ├── ParasiteType.java
│       ├── EvoPhase.java
│       ├── EvolutionPath.java
│       ├── GeneType.java
│       └── DislodgmentCode.java
│
├── client/                            # 客户端专用（@OnlyIn(Dist.CLIENT)）
│   ├── model/                         # GeckoLib 模型
│   │   ├── ModelParasiteBase.java
│   │   └── entity/                    # 具体模型
│   ├── renderer/                      # 渲染器
│   │   ├── RendererParasiteBase.java
│   │   └── layer/                     # 渲染层
│   ├── overlay/                       # HUD 覆盖层
│   │   ├── InfectionOverlay.java
│   │   └── EvolutionOverlay.java
│   └── particle/                      # 粒子渲染
│       ├── ModParticleRenderers.java
│       └── types/
│
├── common/                            # 通用逻辑（服务端 + 客户端）
│   ├── block/
│   │   ├── ModBlocks.java
│   │   ├── entity/                    # 方块实体
│   │   └── behavior/                  # 方块行为
│   ├── entity/
│   │   ├── base/
│   │   │   ├── EntityParasiteBase.java      # 基类
│   │   │   ├── Component.java               # 组件基类
│   │   │   ├── CombatComponent.java
│   │   │   ├── EvolutionComponent.java
│   │   │   ├── InfectionComponent.java
│   │   │   └── ColonyComponent.java
│   │   ├── ai/
│   │   │   ├── goals/                     # AI 目标
│   │   │   └── sensors/                   # AI 传感器
│   │   ├── dispatcher/                    # 进化调度器
│   │   │   ├── EvolutionDispatcher.java
│   │   │   ├── Condition.java
│   │   │   └── conditions/
│   │   └── monster/                       # 具体实体
│   │       ├── infected/
│   │       ├── primitive/
│   │       ├── crude/
│   │       ├── adapted/
│   │       └── ancient/
│   ├── colony/                        # 殖民地系统
│   │   ├── Colony.java
│   │   ├── ColonyManager.java
│   │   ├── Hierarchy.java
│   │   └── tasks/
│   ├── infection/                     # 感染系统
│   │   ├── InfectionManager.java
│   │   ├── COTHSpreadTask.java
│   │   └── BiomeCorruption.java
│   ├── world/
│   │   ├── ModWorldData.java
│   │   ├── CelestialManager.java
│   │   └── biome/
│   ├── network/                       # 网络包
│   │   ├── ModNetwork.java
│   │   ├── Packet.java
│   │   ├── s2c/
│   │   └── c2s/
│   └── item/
│
├── config/                            # 配置系统
│   ├── ModConfigSystems.java
│   ├── EntityStatsConfig.java
│   ├── EvolutionConfig.java
│   └── Serialization.java
│
├── core/                              # 注册对象
│   ├── ModBlocks.java
│   ├── ModItems.java
│   ├── ModEntities.java
│   ├── ModEffects.java
│   ├── ModSounds.java
│   └── ModFeatures.java
│
├── handler/                           # 事件处理器
│   ├── LifecycleHandler.java
│   ├── EventHandler.java
│   └── CapabilityHandler.java
│
├── mixin/                             # Mixin（可选高级功能）
│   ├── MixinLivingEntity.java
│   └── MixinServerLevel.java
│
├── platform/                          # 平台抽象层
│   ├── Services.java
│   └── IPlatformHelper.java
│
└── util/                              # 工具类
    ├── cache/                         # 缓存工具
    │   ├── LRUCache.java
    │   └── ObjectPool.java
    ├── math/                          # 数学工具
    │   ├── FastMath.java
    │   └── SpatialHash.java
    ├── collection/                    # 集合工具
    │   ├── PrimitiveCollections.java
    │   └── BitFlags.java
    └── logging/                       # 日志工具
        ├── StructuredLogger.java
        └── MetricsCollector.java
```

---

## 编码规范强制执行

### 1. 命名约定
```java
// 类名：PascalCase
public final class EvolutionDispatcher {}

// 接口名：I 前缀 + PascalCase
public interface IParasite {}

// 方法名：camelCase，动词开头
public void applyDamage(float amount) {}

// 字段名：camelCase，描述性名称
private final AtomicInteger evolutionCounter;

// 常量：UPPER_SNAKE_CASE
private static final int MAX_EVOLUTION_STAGE = 4;

// 泛型参数：单字母，语义化
public class Repository<T extends Entity> {}
public class EvolutionState<S extends EvolutionStage> {}
```

### 2. 文档规范
```java
/**
 * 处理寄生虫实体的进化逻辑。
 * <p>
 * 此组件负责：
 * <ul>
 *   <li>跟踪击杀数和进化点数</li>
 *   <li>评估进化条件</li>
 *   <li>应用阶段转换</li>
 * </ul>
 * 
 * @param <T> 宿主实体类型，必须是 {@link EntityParasiteBase} 的子类
 * @author SubspaceParasite Team
 * @version 2.0
 * @see EvolutionDispatcher
 * @see EvoPhase
 */
public final class EvolutionComponent<T extends EntityParasiteBase> {
    /**
     * 进化到下一阶段所需的点数。
     * <p>
     * 公式：{@code basePoints * (currentPhase + 1)^1.5}
     */
    private final int pointsForNextPhase;
}
```

### 3. 测试规范
```java
@DisplayName("进化组件应正确计算所需点数")
@Test
void evolutionComponent_shouldCalculateRequiredPoints() {
    // Given
    EvolutionComponent component = new EvolutionComponent(mockEntity);
    component.setPoints(50);
    
    // When
    boolean evolved = component.tryEvolve(EvoPhase.ONE);
    
    // Then
    assertFalse(evolved);
    assertEquals(50, component.getPoints());
}

@GameTest(template = "evolution_test")
public void test_entity_evolution(TestContext context) {
    EntityParasiteBase entity = spawnEntity(context);
    entity.setKillCount(100);
    
    context.runAfterTicks(() -> {
        assert entity.getPhaseCreated() == EvoPhase.ONE;
        context.succeed();
    }, 20);
}
```

---

## 性能基准目标

| 指标 | 目标值 | 测量工具 | 警报阈值 |
|------|--------|----------|----------|
| 实体 Tick 时间 | < 0.03ms | Spark Profiler | > 0.1ms |
| 内存占用（空闲） | < 30MB | VisualVM | > 80MB |
| GC 频率 | < 0.5 次/分钟 | GC 日志 | > 2 次/分钟 |
| 网络带宽 | < 5KB/s/玩家 | Wireshark | > 20KB/s |
| 渲染批次（百实体） | < 50 | F3 调试屏 | > 200 |
| 加载时间 | < 2s | Startup Logger | > 5s |

---

## 质量保证流程

### 1. 静态分析
```bash
# SpotBugs - 查找 bug 模式
./gradlew spotbugsMain

# PMD - 代码质量检查
./gradlew pmdMain

# Checkstyle - 编码规范
./gradlew checkstyleMain

# SonarQube - 综合质量门禁
./gradlew sonarqube
```

### 2. 性能测试
```bash
# JMH 基准测试
./gradlew jmh

# 游戏内性能分析
/profiler start duration=60s output=spark
```

### 3. 兼容性测试
- Minecraft 1.20.1 Forge 47.3.0
- Java 17 LTS（最低），Java 21（推荐）
- 与其他模组的兼容性矩阵测试

---

## 持续集成/持续部署 (CI/CD)

```yaml
# .github/workflows/build.yml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build with Gradle
        run: ./gradlew build
      - name: Run Tests
        run: ./gradlew test
      - name: Upload Artifacts
        uses: actions/upload-artifact@v3
        with:
          name: mod-jar
          path: build/libs/*.jar
```

---

## 技术债务管理

### 已知限制与未来改进
1. **当前使用占位符实体** → 逐步替换为完整实现
2. **配置文件未完全迁移** → 使用 ConfigLib 统一配置
3. **网络同步未优化** → 实现增量同步和压缩
4. **模型/动画待集成** → GeckoLib 集成计划

### 重构计划
- Phase 1: 完成核心系统（Capability, Colony, Evolution）
- Phase 2: 实现 Tier 1 实体完整功能
- Phase 3: 性能优化和内存管理
- Phase 4: 高级特性（Mixin, 数据包支持）

---

*版本：2.0*  
*最后更新：2024*  
*维护者：SubspaceParasite Development Team*
