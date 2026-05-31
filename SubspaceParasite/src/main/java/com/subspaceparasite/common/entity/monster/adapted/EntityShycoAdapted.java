package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityShycoAdapted - Adapted阶段的Shyco变体
 * 特性：胆怯但危险，受击时爆发强力反击。
 * 对应资源：MCMOS_extracted/adapted/shycoAdapted
 */
public class EntityShycoAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 65.0F;
    public static final float ATTACK_DAMAGE = 13.0F;
    public static final float MOVEMENT_SPEED = 0.24F;
    public static final float ARMOR = 5.5F;

    public EntityShycoAdapted(EntityType<EntityShycoAdapted> type, Level world) {
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
