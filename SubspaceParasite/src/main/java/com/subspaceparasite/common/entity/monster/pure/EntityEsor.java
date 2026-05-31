package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityEsor - Pure阶段的Esor变体
 * 特性：能量吸收能力，可从攻击中恢复生命。
 * 对应资源：MCMOS_extracted/pure/esor
 */
public class EntityEsor extends EntityParasiteBase {
    public static final float MAX_HEALTH = 140.0F;
    public static final float ATTACK_DAMAGE = 12.0F;
    public static final float MOVEMENT_SPEED = 0.25F;
    public static final float ARMOR = 9.5F;

    public EntityEsor(EntityType<EntityEsor> type, Level world) {
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
