package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityAnged - Pure阶段的愤怒变体
 * 特性：高攻击力，狂暴状态下伤害倍增。
 * 对应资源：MCMOS_extracted/pure/anged
 */
public class EntityAnged extends EntityParasiteBase {
    public static final float MAX_HEALTH = 130.0F;
    public static final float ATTACK_DAMAGE = 16.0F;
    public static final float MOVEMENT_SPEED = 0.28F;
    public static final float ARMOR = 9.0F;

    public EntityAnged(EntityType<EntityAnged> type, Level world) {
        super(type, world, EvoPhase.FOUR);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(MAX_HEALTH).setBaseValue(MAX_HEALTH);
        this.getAttribute(ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        this.getAttribute(MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
        this.getAttribute(ARMOR).setBaseValue(ARMOR);
    }
}
