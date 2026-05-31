package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityRanracAdapted - Adapted阶段的Ranrac变体
 * 特性：高速追击者，擅长长途奔袭。
 * 对应资源：MCMOS_extracted/adapted/ranracAdapted
 */
public class EntityRanracAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 75.0F;
    public static final float ATTACK_DAMAGE = 10.0F;
    public static final float MOVEMENT_SPEED = 0.31F;
    public static final float ARMOR = 6.5F;

    public EntityRanracAdapted(EntityType<EntityRanracAdapted> type, Level world) {
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
