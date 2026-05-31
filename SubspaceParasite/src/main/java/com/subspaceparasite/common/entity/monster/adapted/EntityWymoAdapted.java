package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityWymoAdapted - Adapted阶段的Wymo变体
 * 特性：飞行骚扰单位，空中优势明显。
 * 对应资源：MCMOS_extracted/adapted/wymoAdapted
 */
public class EntityWymoAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 60.0F;
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float MOVEMENT_SPEED = 0.29F;
    public static final float ARMOR = 5.0F;

    public EntityWymoAdapted(EntityType<EntityWymoAdapted> type, Level world) {
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
