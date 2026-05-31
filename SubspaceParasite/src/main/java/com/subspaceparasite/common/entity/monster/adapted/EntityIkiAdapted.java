package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityIkiAdapted - Adapted阶段的Iki变体
 * 特性：精神干扰能力，可削弱目标意志。
 * 对应资源：MCMOS_extracted/adapted/ikiAdapted
 */
public class EntityIkiAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 75.0F;
    public static final float ATTACK_DAMAGE = 10.5F;
    public static final float MOVEMENT_SPEED = 0.25F;
    public static final float ARMOR = 6.5F;

    public EntityIkiAdapted(EntityType<EntityIkiAdapted> type, Level world) {
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
