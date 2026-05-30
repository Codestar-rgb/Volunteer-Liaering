# SRP 1.20.1 Port - 即时开发行动计划

## 当前状态评估 (2024)

### 已完成的工作
- ✅ 基础项目结构建立
- ✅ 实体基类 `EntityParasiteBase` 实现（两个版本）
- ✅ API 接口定义 (`IParasiteEntity`, `ICombatComponent`, 等)
- ✅ Tier 1 实体实现 (`EntityMovingFlesh`, `EntityWorker`)
- ✅ 部分感染生物实现 (`EntityInfectedHuman`, `EntityInfectedCow`, `EntityInfectedSheep`)
- ✅ 原始寄生虫实现（多种 Bano 变体）
- ✅ GeckoLib 模型 ZIP 资源已提供

### 待完成的核心系统

#### 🔴 P0 - 立即执行（本周）

1. **统一架构决策**
   - 问题：存在两套代码库 (`src/` vs `srp_framework/`)
   - 解决方案：合并为单一架构，采用 `srp_framework/` 的组件模式
   - 行动：将 `srp_framework/` 作为主代码库，迁移 `src/` 中的实体实现

2. **Capability 系统注册**
   - 位置：`src/main/java/com/subspaceparasite/common/capability/`
   - 需要创建：
     - `ModCapabilities.java` - Capability 注册
     - `PlayerInfectionCapability.java` - 玩家感染能力
     - `CapabilityAttachmentEvents.java` - 附加事件处理器

3. **殖民地系统核心**
   - 位置：`src/main/java/com/subspaceparasite/common/colony/`
   - 需要创建：
     - `Colony.java` - 殖民地数据结构
     - `ColonyManager.java` - 每维度管理器
     - `ColonyDataSavedEvent.java` - 世界数据保存

4. **网络同步系统**
   - 位置：`src/main/java/com/subspaceparasite/network/`
   - 需要创建：
     - `ModNetworkChannel.java` - 网络通道
     - `InfectionSyncPacket.java` - 感染数据同步
     - `EvolutionSyncPacket.java` - 进化数据同步

#### 🟡 P1 - 高优先级（下周）

5. **进化调度器系统**
   - 位置：`src/main/java/com/subspaceparasite/common/entity/dispatcher/`
   - 需要创建：
     - `EvolutionDispatcher.java` - 进化事件调度
     - `EvolutionCondition.java` - 条件接口
     - 具体条件实现（击杀数、阶段、群系等）

6. **感染传播系统**
   - 位置：`src/main/java/com/subspaceparasite/common/infection/`
   - 需要创建：
     - `InfectionSpreadTask.java` - 异步传播计算
     - `COTHChunkData.java` - 区块 COTH 存储
     - `InfectionOverlayRenderer.java` - 客户端覆盖层

7. **配置系统完善**
   - 位置：`src/main/java/com/subspaceparasite/config/`
   - 需要创建：
     - `ModConfigSystems.java` - Forge Config API 集成
     - `EntityStatsConfig.java` - 实体数值配置
     - `EvolutionConfig.java` - 进化参数配置

#### 🟢 P2 - 中优先级（本月）

8. **Tier 1 实体补完**
   - 需要实现：
     - `EntityCarrierLight` - 轻型运输单位
     - `EntityCarrierHeavy` - 重型运输单位
     - `EntityHost` - 殖民地方块实体

9. **世界生成系统**
   - 位置：`src/main/java/com/subspaceparasite/common/world/`
   - 需要创建：
     - `ModWorldData.java` - 世界级数据持久化
     - `BiomeCorruptionHandler.java` - 群系腐化
     - `InfestationStructure.java` - 感染结构生成

10. **血月事件系统**
    - 位置：`src/main/java/com/subspaceparasite/common/event/`
    - 需要创建：
      - `BloodMoonEvent.java` - 事件触发器
      - `SpawnWaveManager.java` - 波次管理

---

## 技术规范强制执行

### 代码质量检查清单

每个 PR 必须通过以下检查：

- [ ] **空值安全**: 所有可空返回值标注 `@Nullable`
- [ ] **日志分级**: 使用 `LOGGER.debug/info/warn/error` 适当级别
- [ ] **性能优化**: tick 方法中无对象分配
- [ ] **并发安全**: 共享数据使用 `ConcurrentHashMap` 或原子类
- [ ] **文档完整**: 所有公共 API 有 Javadoc
- [ ] **测试覆盖**: 核心逻辑有单元测试

### 性能基准目标

| 指标 | 目标值 | 测量工具 |
|------|--------|----------|
| 实体 Tick 时间 | < 0.05ms/实体 | Spark Profiler |
| 内存占用 | < 50MB (空闲) | VisualVM |
| GC 频率 | < 1 次/分钟 | GC 日志 |
| 网络带宽 | < 10KB/s/玩家 | Wireshark |

---

## 架构合并策略

### 第一阶段：代码库整合

```bash
# 1. 备份当前 src/
mv src src_backup

# 2. 复制 srp_framework 为主代码库
cp -r srp_framework/SubspaceParasite/src src

# 3. 从 src_backup 迁移新增的实体类
cp src_backup/java/com/subspaceparasite/common/entity/monster/crude/EntityMovingFlesh.java \
   src/main/java/com/subspaceparasite/common/entity/monster/crude/
cp src_backup/java/com/subspaceparasite/common/entity/monster/crude/EntityWorker.java \
   src/main/java/com/subspaceparasite/common/entity/monster/crude/
```

### 第二阶段：API 统一

- 保留 `srp_framework` 的接口设计（`IParasite`, `IEvolvable`, `IInfectable`, `IHitboxedEntity`, `ICanAbility`）
- 使用 `srp_framework` 的 `EvoPhase`（ZERO 到 TEN）
- 使用 `srp_framework` 的 `GeneType` 系统
- 弃用 `src/` 的简化版枚举

### 第三阶段：实体迁移

将 `EntityMovingFlesh` 和 `EntityWorker` 重构为继承自 `srp_framework` 的 `EntityParasiteBase`：

```java
public class EntityMovingFlesh extends EntityParasiteBase implements IParasite {
    // 使用组件系统而不是直接实现所有逻辑
    @Override
    protected CombatComponent createCombatComponent() {
        return new MovingFleshCombatComponent(this);
    }
}
```

---

## 立即可执行的任务列表

### 今日任务

1. **[CODE]** 创建 Capability 系统骨架
   - 文件：`src/main/java/com/subspaceparasite/api/capability/ModCapabilities.java`
   - 内容：注册 `PLAYER_INFECTION` Capability

2. **[CODE]** 创建玩家感染能力实现
   - 文件：`src/main/java/com/subspaceparasite/common/capability/PlayerInfectionCapability.java`
   - 实现：`IPlayerInfectionCapability` 接口

3. **[CODE]** 创建能力附加事件处理器
   - 文件：`src/main/java/com/subspaceparasite/handler/CapabilityAttachmentEvents.java`
   - 注册：`AttachCapabilitiesEvent<Player>`

4. **[CONFIG]** 设置 Gradle 构建环境
   - 确保 `build.gradle` 配置正确
   - 添加 Parchment 映射支持

5. **[DOC]** 更新 DEVELOPMENT_PRIORITY.md
   - 标记已完成的任务
   - 添加新的时间节点

### 本周里程碑

- [x] Capability 系统工作
- [ ] 殖民地系统基础框架
- [ ] 网络同步通道建立
- [ ] EntityMovingFlesh 和 EntityWorker 完全集成
- [ ] 至少一个实体可在游戏中生成并正常工作

---

## 参考资源

- 原版 SRP 源码：https://github.com/Codestar-rgb/Qom-Inseac
- Forge 1.20.1 文档：https://mcforge.readthedocs.io/en/1.20.x/
- Parchment 映射：https://parchmentmc.org/docs/getting-started
- GeckoLib 文档：https://geckolib3.wikibooks.dev/

---

*最后更新：即时*
*下次审查：24 小时后*
