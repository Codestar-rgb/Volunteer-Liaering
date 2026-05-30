# SRP 1.20.1 Port - 开发优先级与技术规范

## 核心目标
- **代码健壮性最高**: 完善的错误处理、空值安全、边界检查
- **技术力最强**: 采用现代 Java 特性、设计模式、性能优化技术
- **可维护性最佳**: 清晰的代码结构、完整的文档、统一的编码规范
- **绝对稳定性**: 充分的测试覆盖、防御性编程、容错机制
- **超强性能**: 零冗余分配、缓存优化、并发友好设计

---

## Phase 1: 核心系统完善 (立即执行)

### 1.1 能力系统 (Capability System) 🔴 高优先级
**位置**: `src/main/java/com/subspaceparasite/common/capability/`

**需要实现**:
- [ ] `IPlayerInfectionCapability` - 玩家感染状态追踪
- [ ] `PlayerInfectionCapability` - 能力实现
- [ ] `CapabilityAttachmentEvents` - 能力附加事件
- [ ] `InfectionData` - 感染数据结构 (NBT 序列化)
- [ ] `InfectionSyncPacket` - 网络同步包

**技术规范**:
```java
// 使用 AutoCapability 注解简化注册
@AutoCapability(value = "subspaceparasite:player_infection")
public interface IPlayerInfectionCapability {
    int getInfectionLevel();
    void setInfectionLevel(int level);
    float getResistance();
    void addResistance(float amount);
    boolean hasSymptoms();
    List<Symptom> getActiveSymptoms();
}
```

### 1.2 进化调度器系统 (Evolution Dispatcher) 🔴 高优先级
**位置**: `src/main/java/com/subspaceparasite/common/entity/dispatcher/`

**需要实现**:
- [ ] `EvolutionDispatcher` - 进化事件调度中心
- [ ] `EvolutionCondition` - 进化条件接口
- [ ] `KillCountCondition` - 击杀数条件
- [ ] `PhaseCondition` - 阶段条件
- [ ] `BiomeCondition` - 生物群系条件
- [ ] `TimeCondition` - 时间条件
- [ ] `EvolutionResult` - 进化结果封装

**设计模式**: 策略模式 + 责任链模式

### 1.3 殖民地系统 (Colony System) 🟡 中优先级
**位置**: `src/main/java/com/subspaceparasite/common/colony/`

**需要实现**:
- [ ] `Colony` - 殖民地数据类
- [ ] `ColonyManager` - 殖民地管理器 (每维度单例)
- [ ] `ColonyHierarchy` - 等级制度枚举
- [ ] `ColonySpawnHandler` - 殖民地生成处理器
- [ ] `HostBlockEntity` - Host 方块实体

---

## Phase 2: Tier 1 实体完成 (第一可玩版本)

### 2.1 必需实体清单
| 实体 | 状态 | 优先级 | 备注 |
|------|------|--------|------|
| InfectedHuman | ✅ | - | 已完成 |
| InfectedSheep | ✅ | - | 已完成 |
| InfectedCow | ✅ | - | 已完成 |
| Bano (Primitive) | ✅ | - | 已完成 |
| MovingFlesh | ❌ | 🔴 | 资源单位核心 |
| Worker | ❌ | 🔴 | 殖民地建造者 |
| CarrierLight | ❌ | 🟡 | 轻型运输 |
| CarrierHeavy | ❌ | 🟡 | 重型运输 |

### 2.2 EntityMovingFlesh 实现模板
```java
public class EntityMovingFlesh extends EntityParasiteBase {
    // 核心功能:
    // 1. 被其他寄生虫拾取作为资源
    // 2. 低血量、高移动速度
    // 3. 无攻击能力，纯被动单位
    // 4. 死亡时掉落生物质
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ParasiteFleeFromEntityGoal<>(this, Player.class, 8.0));
        this.goalSelector.addGoal(3, new ParasiteWanderGoal(this, 1.2));
    }
    
    // 性能优化: 重写 AABB 计算减少 GC
    private static final AABB STATIC_BB = new AABB(-0.3, 0, -0.3, 0.3, 0.4, 0.3);
    @Override
    public AABB getBoundingBoxForCulling() {
        return STATIC_BB.move(this.position());
    }
}
```

---

## Phase 3: 系统级功能

### 3.1 感染传播系统
- [ ] `InfectionSpreadTask` - 异步感染传播计算
- [ ] `COTHChunkData` - 每区块 COTH 值存储
- [ ] `InfectionOverlayRenderer` - 客户端感染覆盖层渲染
- [ ] `BiomeCorruptionHandler` - 生物群系腐化处理

### 3.2 基因表达系统
- [ ] `GenePool` - 基因池管理
- [ ] `GeneExpressionCalculator` - 基因表达计算器
- [ ] `PhenotypeGenerator` - 表型生成器
- [ ] `GeneMutationHandler` - 基因突变处理器

### 3.3 血月事件系统
- [ ] `BloodMoonEvent` - 血月事件触发器
- [ ] `SpawnWaveManager` - 生成波次管理器
- [ ] `DifficultyScaler` - 难度缩放器

---

## 技术规范与最佳实践

### 代码质量要求

#### 1. 空值安全
```java
// ✅ 正确做法
@Nullable
public EntityParasiteBase getOwner() {
    return this.owner;
}

public EntityParasiteBase getOwnerNonNull() {
    return Objects.requireNonNull(this.owner, "Owner cannot be null");
}

// ✅ 使用 Optional
public Optional<EntityParasiteBase> tryGetOwner() {
    return Optional.ofNullable(this.owner);
}
```

#### 2. 日志规范
```java
// ✅ 分级日志
if (Config.DEBUG.get()) {
    LOGGER.debug("Debug info: {}", data);
}

LOGGER.info("Entity {} evolved to phase {}", entity.getId(), newPhase);

LOGGER.warn("Unusual state detected: {}", state);

LOGGER.error("Critical failure in evolution system", exception);
```

#### 3. 性能优化
```java
// ✅ 对象池重用
private static final ObjectPool<ParticleData> PARTICLE_POOL = 
    ObjectPool.create(ParticleData::new, ParticleData::reset);

// ✅ 缓存计算结果
private final Lazy<Float> cachedHealthMultiplier = Lazy.of(this::calculateHealthMultiplier);

// ✅ 避免在 tick 中分配
// ❌ 错误: 每帧创建新对象
this.level().addParticle(new ParticleData(x, y, z));

// ✅ 正确: 重用静态实例或使用池
this.level().addParticle(ParticleTypes.PORTAL, x, y, z, 0, 0.05, 0);
```

#### 4. 并发安全
```java
// ✅ 使用线程安全的数据结构
private final ConcurrentHashMap<UUID, ColonyData> colonies = new ConcurrentHashMap<>();

// ✅ 原子操作
private final AtomicInteger evolutionCounter = new AtomicInteger(0);

// ✅ 读写锁
private final ReadWriteLock lock = new ReentrantReadWriteLock();
```

### 文件组织标准

```
src/main/java/com/subspaceparasite/
├── api/                          # 公共 API (稳定接口)
│   ├── capability/               # 能力接口
│   ├── event/                    # 事件接口
│   └── parasite/                 # 枚举类型
├── client/                       # 客户端专用
│   ├── model/                    # GeckoLib 模型
│   ├── renderer/                 # 实体渲染器
│   ├── overlay/                  # HUD 覆盖层
│   └── particle/                 # 粒子渲染
├── common/                       # 通用逻辑
│   ├── entity/
│   │   ├── base/                 # 基类和组件
│   │   ├── ai/                   # AI 目标
│   │   ├── dispatcher/           # 进化调度器
│   │   └── monster/              # 具体实体
│   ├── colony/                   # 殖民地系统
│   ├── infection/                # 感染系统
│   ├── world/                    # 世界生成和管理
│   └── network/                  # 网络包
├── config/                       # 配置系统
├── core/                         # 注册对象
├── handler/                      # 事件处理器
├── util/                         # 工具类
│   ├── cache/                    # 缓存工具
│   ├── pool/                     # 对象池
│   └── math/                     # 数学工具
└── SubspaceParasite.java         # 主类
```

### 测试策略

#### 单元测试 (JUnit 5)
```java
@Test
@DisplayName("进化组件应正确计算所需点数")
void evolutionComponent_shouldCalculateRequiredPoints() {
    EvolutionComponent component = new EvolutionComponent(mockEntity);
    assertEquals(100, component.getPointsNeededForNextPhase(EvoPhase.ZERO));
}
```

#### 游戏内测试
```java
// 使用 GameTest 框架
@GameTest(template = "evolution_test")
public void test_entity_evolution(TestContext context) {
    EntityParasiteBase entity = (EntityParasiteBase) context.spawn(...);
    entity.setKillCount(100);
    context.runAfterTicks(() -> {
        assert entity.getPhaseCreated() == EvoPhase.ONE;
        context.succeed();
    }, 20);
}
```

---

## 性能基准目标

| 指标 | 目标值 | 测量方法 |
|------|--------|----------|
| 实体 Tick 时间 | < 0.05ms/实体 | Spark Profiler |
| 内存占用 | < 50MB (空闲) | VisualVM |
| GC 频率 | < 1 次/分钟 | GC 日志 |
| 网络带宽 | < 10KB/s/玩家 | Wireshark |
| 渲染批次 | < 100 (百实体) | F3 调试屏 |

---

## 下一步行动项

### 本周目标
1. ✅ 完成 Capability 系统实现
2. ✅ 实现 EvolutionDispatcher 基础框架
3. ✅ 添加 EntityMovingFlesh
4. ✅ 添加 EntityWorker
5. ✅ 编写单元测试覆盖核心逻辑

### 本月目标
1. 所有 Tier 1 实体完成
2. 殖民地系统基础可用
3. 感染传播系统工作
4. 配置系统完整
5. 性能基准测试通过

---

## 参考资源

- 原版 SRP 源码: https://github.com/Codestar-rgb/Qom-Inseac
- Forge 文档: https://mcforge.readthedocs.io/en/1.20.x/
- Parchment 映射: https://parchmentmc.org/docs/getting-started
- GeckoLib 文档: https://geckolib3.wikibooks.dev/

---

*最后更新: 开发进行中*
*目标版本: Minecraft 1.20.1 Forge 47.3.0*
