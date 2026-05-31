package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityHullAdapted - Adapted阶段的Hull变体
 * 特性：高防御单位，具有护盾生成能力。
 * 对应资源：MCMOS_extracted/adapted/hullAdapted
 */
public class EntityHullAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 110.0F;
    public static final float ATTACK_DAMAGE = 10.0F;
    public static final float MOVEMENT_SPEED = 0.20F;
    public static final float ARMOR = 12.0F;

    public EntityHullAdapted(EntityType<EntityHullAdapted> type, Level world) {
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
