package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityLumAdapted - Adapted阶段的Lum变体
 * 特性：发光诱捕能力，可干扰玩家视野。
 * 对应资源：MCMOS_extracted/adapted/lumAdapted
 */
public class EntityLumAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 70.0F;
    public static final float ATTACK_DAMAGE = 9.5F;
    public static final float MOVEMENT_SPEED = 0.27F;
    public static final float ARMOR = 6.0F;

    public EntityLumAdapted(EntityType<EntityLumAdapted> type, Level world) {
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
