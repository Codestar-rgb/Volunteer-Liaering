package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityFlam - Pure阶段的火焰变体
 * 特性：火焰攻击能力，可点燃目标。
 * 对应资源：MCMOS_extracted/pure/flam
 */
public class EntityFlam extends EntityParasiteBase {
    public static final float MAX_HEALTH = 125.0F;
    public static final float ATTACK_DAMAGE = 14.5F;
    public static final float MOVEMENT_SPEED = 0.27F;
    public static final float ARMOR = 8.0F;

    public EntityFlam(EntityType<EntityFlam> type, Level world) {
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
