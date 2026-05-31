package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityNoglaAdapted - Adapted阶段的Nogla变体
 * 特性：隐形潜行能力，难以被发现。
 * 对应资源：MCMOS_extracted/adapted/noglaAdapted
 */
public class EntityNoglaAdapted extends EntityParasiteBase {
    public static final float MAX_HEALTH = 80.0F;
    public static final float ATTACK_DAMAGE = 11.0F;
    public static final float MOVEMENT_SPEED = 0.28F;
    public static final float ARMOR = 7.0F;

    public EntityNoglaAdapted(EntityType<EntityNoglaAdapted> type, Level world) {
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
