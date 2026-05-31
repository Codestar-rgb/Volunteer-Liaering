package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityFlog - Pure阶段的Flog变体
 * 特性：重型地面单位，具有冲击能力。
 * 对应资源：MCMOS_extracted/pure/flog
 */
public class EntityFlog extends EntityParasiteBase {
    public static final float MAX_HEALTH = 160.0F;
    public static final float ATTACK_DAMAGE = 15.0F;
    public static final float MOVEMENT_SPEED = 0.23F;
    public static final float ARMOR = 11.0F;

    public EntityFlog(EntityType<EntityFlog> type, Level world) {
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
