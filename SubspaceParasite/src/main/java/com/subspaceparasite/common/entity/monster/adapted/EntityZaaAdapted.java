package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityZaaAdapted - Adapted阶段的Zaa变体
 * 特性：基础单位的强化版，均衡型战斗单位。
 * 对应资源：MCMOS_extracted/adapted/zaaAdapted
 */
public class EntityZaaAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 95.0F;
    public static final float ATTACK_DAMAGE = 12.5F;
    public static final float MOVEMENT_SPEED = 0.22F;
    public static final float ARMOR = 8.5F;

    public EntityZaaAdapted(EntityType<EntityZaaAdapted> type, Level world) {
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
