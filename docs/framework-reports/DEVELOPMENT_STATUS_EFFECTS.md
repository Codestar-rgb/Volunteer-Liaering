# SRP 1.20.1 移植 - 系统机制与BUFF效果开发状态报告

## 📊 当前完成度评估

### ✅ 已完成的核心系统

#### 1. 效果注册系统 (ModEffects.java)
**状态**: 完整实现 (36种效果)
- **感染类效果** (5种): COTH, INFECTION_II/III, VIRULENCE, COAGULATION
- **进化类效果** (4种): EVOLUTION, ADAPTATION, SENTIENCE, DERIVATION
- **腐化类效果** (4种): CORRUPTION, DECAY, DECOMPOSITION, PUTREFACTION
- **寄生虫增益** (5种): PARASITE_VITALITY, RESISTANCE, STRENGTH, SPEED, REGENERATION
- **减益效果** (4种): FEAR, SLOWNESS_PARASITE, WEAKNESS_PARASITE, WITHER_PARASITE
- **特殊效果** (9种): GESTATION, INCUBATION, ASSIMILATION, MUTAGENIC, CORROSION, VIRAL, SPORE, BLEED, NOVISION, VOMIT
- **抗性/净化** (3种): PURGE, IMMUNITY, CLEANSING
- **Nexus连接** (2种): NEXUS_LINK, NEXUS_COMMAND
- **其他** (2种): PARASITE_HUNGER, LEECH

**技术实现**:
```java
// 使用Helper方法简化注册
private static RegistryObject<MobEffect> registerHarmful(String name, int color)
private static RegistryObject<MobEffect> registerBeneficial(String name, int color)
private static RegistryObject<MobEffect> registerNeutral(String name, int color)
```

#### 2. 实体组件式效果系统
**InfectionComponent.java** - 感染传播组件
- ✅ COTH传播逻辑 (接触/光环/死亡爆发)
- ✅ 感染转化率计算 (基于生命值、护甲、阶段)
- ✅ 免疫检测 (寄生虫/BOSS/创造模式玩家/配置黑名单)
- ✅ COTH效果应用 (WITHER + SLOWDOWN + WEAKNESS 基于阶段)
- ✅ 转化实体逻辑 (COTHMapping映射表)

**CombatComponent.java** - 战斗组件
- ✅ 伤害帽限制
- ✅ 最小伤害强制
- ✅ 毒素免疫治疗
- ✅ 反伤效果 (POISON, SLOWDOWN)

**EvolutionComponent.java** - 进化组件
- ✅ 进化点数追踪
- ✅ 阶段转换触发
- ✅ 基因突变系统

#### 3. AI目标行为中的效果应用
**ParasiteGiveEffectsGoal.java** - 群体增益AI
- ✅ 周期性给附近寄生虫施加效果
- ✅ 可配置效果列表 (speed, strength, resistance等)
- ✅ 范围检测 + 冷却机制
- ⚠️ **待完善**: 仅支持原版效果，需添加自定义MOD效果支持

**ParasiteSkillGoal.java** - 技能执行AI
- ✅ 技能ID系统 (13=方块破坏, 14=跳跃攻击)
- ✅ 距离检测 + 视线检测
- ✅ 冷却管理
- ⚠️ **待完善**: 技能效果回调未完全实现

#### 4. 基础能力接口 (ICanAbility.java)
**12种能力类型**:
- CLIMB, COLONY, FLY, HAVE_BODIES, MELT
- PULL_MOBS, SHOOT, SPAWN, SUMMON, SWIM
- VECTORS, CUSTOM_ATTACK

---

## ⚠️ 待完善的关键系统

### P0 优先级 - 核心机制缺失

#### 1. 自定义MobEffect实现
**问题**: ModEffects.java中所有效果都是空实现
```java
// 当前实现 (无实际逻辑)
return EFFECTS.register(name, () -> new MobEffect(MobEffectCategory.HARMFUL, color) {});
```

**需要实现**:
- [ ] `applyEffectTick()` - 每tick的效果逻辑
- [ ] `isDurationEffectTick()` - 效果持续时间判断
- [ ] 各效果的独特行为:
  - **COTH**: 持续伤害 + 感染值累积 + 转化倒计时
  - **BLEED**: 基于移动速度的持续出血伤害
  - **CORROSION**: 降低护甲耐久 + 护甲值
  - **VIRAL**: 增加受到的治疗效果降低
  - **SPORE**: 生成孢子粒子 + 窒息伤害
  - **NOVISION**: 完全黑屏 + 听觉增强补偿
  - **PARASITE_*系列**: 寄生虫专属增益叠加逻辑

#### 2. Capability感染状态系统集成
**ParasiteCapability.java** 已存在但未与效果系统联动
```java
// 已有字段
- infectionLevel (0-100)
- infectionResistance
- infectionCooldown
- isImmune
```

**需要集成**:
- [ ] COTH效果tick时增加infectionLevel
- [ ] infectionLevel达到阈值触发转化
- [ ] 净化效果减少infectionLevel
- [ ] 网络同步感染UI显示

#### 3. 效果叠加与冲突系统
**缺失功能**:
- [ ] 同种效果多层叠加逻辑 (COTH I/II/III)
- [ ] 冲突效果互斥 (PURGE vs COTH)
- [ ] 效果免疫检测扩展
- [ ] 效果来源追踪 (用于击杀归属)

#### 4. 高级技能效果系统
**EntityParasiteBase.java** 中的技能系统待完善:
```java
// 当前仅支持2个基础技能
case 13 -> skillBreakBlocks();
case 14 -> skillLeap();
```

**需要添加**:
- [ ] Skill ID 0-12: 特殊攻击效果
  - Skill 1: 毒素喷射 (POISON cloud)
  - Skill 2: 吸血攻击 (LEECH life steal)
  - Skill 3: 恐惧光环 (FEAR area effect)
  - Skill 4: 召唤支援 (SUMMON minions)
  - Skill 5: 自爆预备 (SWELL charge)
  - Skill 6-12: 各Tier专属技能
- [ ] 技能效果可视化 (粒子/声音)
- [ ] 技能网络同步

---

## 🎯 下一步开发计划

### 阶段一：基础效果逻辑实现 (立即执行)

#### Task 1.1: 创建CustomMobEffect基类
```java
public abstract class CustomMobEffect extends MobEffect {
    protected final int maxAmplifier;
    protected final boolean canStack;
    
    // 通用方法
    public void applyInstantenousEffect(...)
    public void applyEffectTick(LivingEntity, int amplifier)
    public boolean isDurationEffectTick(int duration, int amplifier)
}
```

#### Task 1.2: 实现核心感染效果
- `CothEffect.java` - Call of the Hive主效果
- `InfectionProgressionEffect.java` - INFECTION_II/III进阶
- `VirulenceEffect.java` - 毒力效果
- `CoagulationEffect.java` - 凝血效果

#### Task 1.3: 实现寄生虫增益效果
- `ParasiteVitalityEffect.java` - 生命力加成
- `ParasiteAdaptationEffect.java` - 适应性进化
- `ParasiteSentienceEffect.java` - 感知觉醒

### 阶段二：系统集成 (短期目标)

#### Task 2.1: Capability-效果联动
- 修改`ParasiteCapabilityEvents`监听效果应用
- 在`InfectionComponent`中追踪效果层数
- 网络同步感染进度到客户端

#### Task 2.2: ParasiteGiveEffectsGoal扩展
- 支持自定义MOD效果解析
- 添加效果强度基于进化阶段
- 优化范围检测性能

#### Task 2.3: 技能效果框架
- 扩展`doSpecialSkill()`支持15+技能
- 创建`SkillEffectHandler`统一处理
- 添加技能动画钩子

### 阶段三：高级机制 (中期目标)

#### Task 3.1: 效果组合系统
- 连携效果 (COTH + BLEED = 快速转化)
- 效果链 (感染→腐化→转化)
- 动态难度调整

#### Task 3.2: Boss专属效果
- NEXUS链接效果
- 殖民地意识共享
- 领袖光环效果

#### Task 3.3: 配置驱动平衡
- 所有效果参数可配置
- 生物群系效果修正
- 难度等级影响

---

## 📈 技术架构建议

### 1. 效果工厂模式
```java
public interface IEffectFactory {
    CustomMobEffect createEffect(String effectId, int amplifier);
}
```

### 2. 效果数据组件
```java
public class EffectDataComponent {
    Map<ResourceLocation, EffectInstanceData> activeEffects;
    // 持久化 + 网络同步
}
```

### 3. 事件驱动架构
```java
@SubscribeEvent
public void onEffectApplied(MobEffectEvent.Added event) {
    // 统一处理逻辑
}
```

---

## 🔧 立即可执行的代码改进

### 改进1: ParasiteGiveEffectsGoal支持自定义效果
```java
protected MobEffectInstance parseEffect(String name) {
    // 先检查原版效果
    // 再检查ModEffects注册的效果
    // 最后尝试ResourceLocation解析
}
```

### 改进2: InfectionComponent效果应用增强
```java
protected void applyCOTH(LivingEntity target) {
    // 添加分层效果逻辑
    // 追踪感染源
    // 触发Capability更新
}
```

### 改进3: EntityParasiteBase技能扩展
```java
@Override
public void doSpecialSkill(byte skillId) {
    switch (skillId) {
        case 0 -> skillPoisonCloud();
        case 1 -> skillLifeLeech();
        case 2 -> skillFearAura();
        // ...更多技能
        default -> super.doSpecialSkill(skillId);
    }
}
```

---

## 📝 总结

**当前优势**:
- ✅ 完整的36效果注册框架
- ✅ 组件式实体架构
- ✅ 感染传播基础逻辑
- ✅ AI目标行为框架

**关键缺口**:
- ❌ 自定义效果实际逻辑实现
- ❌ Capability与效果系统联动
- ❌ 高级技能效果系统
- ❌ 效果叠加/冲突管理

**建议优先级**:
1. **立即**: 实现CustomMobEffect基类和4个核心感染效果
2. **本周**: 完成Capability-效果联动和网络同步
3. **下周**: 扩展技能系统到15+技能
4. **本月**: 完成所有36效果的完整逻辑

此架构已具备极高的技术上限，补完效果逻辑后即可实现SRP的完整还原。
