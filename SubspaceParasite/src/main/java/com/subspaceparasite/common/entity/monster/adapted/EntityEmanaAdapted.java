package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityEmanaAdapted - Adapted阶段的Emana变体
 * 特性：能量操控能力增强，具有范围干扰效果。
 * 对应资源：MCMOS_extracted/adapted/emanAdapted
 */
public class EntityEmanaAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 85.0F;
    public static final float ATTACK_DAMAGE = 11.5F;
    public static final float MOVEMENT_SPEED = 0.24F;
    public static final float ARMOR = 7.5F;

    public EntityEmanaAdapted(EntityType<EntityEmanaAdapted> type, Level world) {
        super(type, world, EvoPhase.THREE);
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
