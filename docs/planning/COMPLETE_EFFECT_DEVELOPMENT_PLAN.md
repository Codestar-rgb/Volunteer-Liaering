# SRP 1.20.1 完整效果系统开发计划

## 🎯 目标
实现原版SRP所有36种BUFF/DEBUFF效果的完整逻辑，确保：
- 代码健壮性最高
- 技术力最强
- 适用于未来开发任务
- 高效开发、调试和可维护性
- 绝对稳定性和超强性能
- 支撑未来移植重写任务基础

---

## 📋 效果分类与实现优先级

### P0 - 核心感染系统 (立即实现)

#### 1. 感染效果链 (5个)
| 效果名称 | 当前状态 | 需要实现的功能 |
|---------|---------|---------------|
| COTH | ✅ 已实现 | 需要增强与Capability的联动 |
| INFECTION_II | ❌ 空实现 | 进阶感染，加速转化 |
| INFECTION_III | ❌ 空实现 | 终极感染，即时转化威胁 |
| VIRULENCE | ❌ 空实现 | 增加感染传播速率 |
| COAGULATION | ❌ 空实现 | 降低治疗效果 |

#### 2. 净化效果链 (3个)
| 效果名称 | 当前状态 | 需要实现的功能 |
|---------|---------|---------------|
| PURGE | ✅ 已实现 | 需要完善免疫机制 |
| IMMUNITY | ❌ 空实现 | 临时完全免疫 |
| CLEANSING | ❌ 空实现 | 持续净化 + 恢复 |

### P1 - 寄生虫增益系统 (高优先级)

#### 3. 进化效果链 (4个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| EVOLUTION | 进化点数获取加速 |
| ADAPTATION | 伤害抗性逐步提升 |
| SENTIENCE | 感知范围扩展 + AI增强 |
| DERIVATION | 特殊形态转换准备 |

#### 4. 寄生虫专属增益 (5个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| PARASITE_VITALITY | ✅ 已实现 |
| PARASITE_RESISTANCE | 百分比伤害减免 |
| PARASITE_STRENGTH | 攻击力叠加 |
| PARASITE_SPEED | 移动速度 + 攻击速度 |
| PARASITE_REGENERATION | 超速再生 |

### P2 - 腐化与减益系统 (中优先级)

#### 5. 腐化效果链 (4个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| CORRUPTION | 持续伤害 + 最大生命值降低 |
| DECAY | 护甲值永久降低 |
| DECOMPOSITION | 饥饿度快速消耗 |
| PUTREFACTION | 中毒 + 缓慢复合效果 |

#### 6. 战斗减益 (4个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| FEAR | 强制逃跑 + 颤抖效果 |
| SLOWNESS_PARASITE | 超级缓慢 |
| WEAKNESS_PARASITE | 超级虚弱 |
| WITHER_PARASITE | 超级凋零 |

### P3 - 特殊机制系统 (低优先级)

#### 7. 繁殖与孵化 (2个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| GESTATION | 寄生卵发育进度 |
| INCUBATION | 孵化倒计时 |

#### 8. 特殊攻击效果 (9个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| ASSIMILATION | 同化进度追踪 |
| MUTAGENIC | 基因突变触发 |
| CORROSION | ✅ 已实现 |
| BLEED | ✅ 已实现 |
| VIRAL | 治疗反转 (治疗变伤害) |
| SPORE | 孢子云生成 + 窒息 |
| NOVISION | 完全致盲 + 听觉补偿 |
| VOMIT | 反胃 + 随机掉落物品 |
| LEECH | 生命偷取链接 |

#### 9. Nexus系统 (2个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| NEXUS_LINK | 蜂巢意识连接 |
| NEXUS_COMMAND | 殖民地指挥权 |

#### 10. 其他效果 (2个)
| 效果名称 | 需要实现的功能 |
|---------|---------------|
| PARASITE_HUNGER | 寄生虫专属饥饿 (吞噬) |
| IMMUNITY | 完全免疫 (已在P0) |

---

## 🏗️ 技术架构设计

### 1. 分层效果基类体系

```
BaseSRPEffect (抽象基类)
├── InfectionEffectBase (感染系基类)
│   ├── CothEffect ✅
│   ├── InfectionStageEffect (INFECTION_II/III)
│   ├── VirulenceEffect
│   └── CoagulationEffect
├── PurificationEffectBase (净化系基类)
│   ├── PurgeEffect ✅
│   ├── ImmunityEffect
│   └── CleansingEffect
├── ParasiteBuffEffectBase (寄生虫增益基类)
│   ├── ParasiteVitalityEffect ✅
│   ├── ParasiteResistanceEffect
│   ├── ParasiteStrengthEffect
│   ├── ParasiteSpeedEffect
│   └── ParasiteRegenerationEffect
├── CorruptionEffectBase (腐化系基类)
│   ├── CorruptionEffect
│   ├── DecayEffect
│   ├── DecompositionEffect
│   └── PutrefactionEffect
├── DebuffEffectBase (减益基类)
│   ├── FearEffect
│   └── StatReductionEffect (Slowness/Weakness/Wither)
└── SpecialEffectBase (特殊基类)
    ├── GestationEffect
    ├── ViralEffect
    ├── SporeEffect
    ├── NovisionEffect
    └── LeechEffect
```

### 2. 效果数据组件系统

创建 `EffectDataComponent` 用于：
- 追踪效果来源实体
- 记录效果叠加层数
- 管理效果连锁反应
- 网络同步效果状态

### 3. 效果事件总线

使用Forge事件系统统一管理：
```java
@SubscribeEvent
public void onEffectAdded(MobEffectEvent.Added event)
@SubscribeEvent  
public void onEffectRemoved(MobEffectEvent.Removed event)
@SubscribeEvent
public void onEffectTick(MobEffectEvent.Tick event)
```

### 4. 配置驱动平衡

所有效果参数通过 `ModConfigSystems` 配置：
- 基础强度
- 持续时间
- 叠加层数上限
- 免疫条件
- 群体影响范围

---

## 📝 实现清单

### 阶段一：核心感染系统 (Day 1-2)

- [ ] 创建 `InfectionEffectBase` 基类
- [ ] 实现 `InfectionStageEffect` (INFECTION_II/III通用逻辑)
- [ ] 实现 `VirulenceEffect`
- [ ] 实现 `CoagulationEffect`
- [ ] 实现 `ImmunityEffect`
- [ ] 实现 `CleansingEffect`
- [ ] 增强 `CothEffect` 与 `ParasiteCapability` 联动
- [ ] 增强 `PurgeEffect` 免疫机制

### 阶段二：寄生虫增益系统 (Day 3-4)

- [ ] 创建 `ParasiteBuffEffectBase` 基类
- [ ] 实现 `EvolutionEffect`
- [ ] 实现 `AdaptationEffect`
- [ ] 实现 `SentienceEffect`
- [ ] 实现 `DerivationEffect`
- [ ] 实现 `ParasiteResistanceEffect`
- [ ] 实现 `ParasiteStrengthEffect`
- [ ] 实现 `ParasiteSpeedEffect`
- [ ] 实现 `ParasiteRegenerationEffect`

### 阶段三：腐化与减益系统 (Day 5-6)

- [ ] 创建 `CorruptionEffectBase` 基类
- [ ] 实现 `CorruptionEffect`
- [ ] 实现 `DecayEffect`
- [ ] 实现 `DecompositionEffect`
- [ ] 实现 `PutrefactionEffect`
- [ ] 实现 `FearEffect`
- [ ] 实现 `StatDebuffEffect` (Slowness/Weakness/Wither统一处理)

### 阶段四：特殊机制系统 (Day 7-10)

- [ ] 创建 `SpecialEffectBase` 基类
- [ ] 实现 `GestationEffect`
- [ ] 实现 `IncubationEffect`
- [ ] 实现 `AssimilationEffect`
- [ ] 实现 `MutagenicEffect`
- [ ] 实现 `ViralEffect`
- [ ] 实现 `SporeEffect`
- [ ] 实现 `NovisionEffect`
- [ ] 实现 `VomitEffect`
- [ ] 实现 `LeechEffect`
- [ ] 实现 `NexusLinkEffect`
- [ ] 实现 `NexusCommandEffect`
- [ ] 实现 `ParasiteHungerEffect`

### 阶段五：系统集成与优化 (Day 11-14)

- [ ] 创建 `EffectDataComponent`
- [ ] 实现效果事件总线
- [ ] 添加效果来源追踪
- [ ] 实现效果冲突检测
- [ ] 实现效果连锁反应
- [ ] 网络同步优化
- [ ] 性能分析与优化
- [ ] 配置文件完善
- [ ] 文档编写

---

## 🔧 代码质量标准

### 1. 健壮性要求
- 所有公开方法必须有 `@NotNull` / `@Nullable` 注解
- 所有外部输入必须验证
- 所有异常必须捕获并记录日志
- 所有NBT读写必须有默认值处理

### 2. 性能要求
- 效果tick必须有合理的间隔 (`tickInterval >= 5`)
- 粒子生成必须限制在客户端
- 实体搜索必须使用优化的空间查询
- 网络包必须合并发送

### 3. 可维护性要求
- 每个效果类不超过500行
- 复杂逻辑必须拆分为私有方法
- 所有魔法数字必须定义为常量
- 所有公共API必须有JavaDoc

### 4. 可扩展性要求
- 使用继承而非硬编码
- 效果参数必须可配置
- 预留钩子方法供子类覆盖
- 遵循开闭原则

---

## 📊 验收标准

### 功能完整性
- [ ] 36种效果全部有实际逻辑
- [ ] 所有效果可正常施加/移除
- [ ] 效果叠加正确工作
- [ ] 效果冲突正确处理

### 稳定性
- [ ] 无内存泄漏
- [ ] 无崩溃风险
- [ ] 服务端/客户端同步正确
- [ ] 存档加载/保存正确

### 性能
- [ ] 单个效果tick耗时 < 0.01ms
- [ ] 50个效果同时存在FPS下降 < 5%
- [ ] 网络带宽占用合理

### 还原度
- [ ] 效果行为与原版SRP一致
- [ ] 数值平衡与原版SRP一致
- [ ] 视觉效果风格一致
- [ ] 音效反馈完整

---

## 🚀 启动命令

完成此计划后，执行以下命令验证：

```bash
# 编译检查
./gradlew build

# 运行测试
./gradlew test

# 代码质量检查
./gradlew checkstyleMain

# 生成开发报告
./gradlew generateDevelopmentReport
```

---

**预计总工时**: 14天  
**代码行数预估**: +8000行  
**测试覆盖率目标**: >85%
