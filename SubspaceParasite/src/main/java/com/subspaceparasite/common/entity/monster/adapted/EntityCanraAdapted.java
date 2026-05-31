package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

/**
 * EntityCanraAdapted - Adapted阶段的Canra变体
 * 特性：增强的机动性和攻击范围，适应性强。
 * 对应资源：MCMOS_extracted/adapted/canraAdapted
 */
public class EntityCanraAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 90.0F;
    public static final float ATTACK_DAMAGE = 12.0F;
    public static final float MOVEMENT_SPEED = 0.26F;
    public static final float ARMOR = 8.0F;

    public EntityCanraAdapted(EntityType<EntityCanraAdapted> type, Level world) {
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
