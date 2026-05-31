package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityGimAdapted - Adapted阶段的Gim变体
 * 特性：小型快速单位，群体协作能力增强。
 * 对应资源：MCMOS_extracted/adapted/gimAdapted
 */
public class EntityGimAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 65.0F;
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float MOVEMENT_SPEED = 0.30F;
    public static final float ARMOR = 5.0F;

    public EntityGimAdapted(EntityType<EntityGimAdapted> type, Level world) {
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
