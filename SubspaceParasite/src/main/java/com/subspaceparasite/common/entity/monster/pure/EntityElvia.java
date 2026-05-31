package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityElvia - Pure阶段的Elvia变体
 * 特性：优雅而致命，具有高闪避能力。
 * 对应资源：MCMOS_extracted/pure/elvia
 */
public class EntityElvia extends EntityParasiteBase {
    public static final float MAX_HEALTH = 120.0F;
    public static final float ATTACK_DAMAGE = 13.5F;
    public static final float MOVEMENT_SPEED = 0.30F;
    public static final float ARMOR = 8.5F;

    public EntityElvia(EntityType<EntityElvia> type, Level world) {
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
