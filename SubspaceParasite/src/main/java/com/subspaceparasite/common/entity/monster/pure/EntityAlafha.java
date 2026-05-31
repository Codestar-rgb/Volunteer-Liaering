package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityAlafha - Pure阶段的Alpha领袖单位
 * 特性：群体指挥能力，强化周围寄生虫。
 * 对应资源：MCMOS_extracted/pure/alafha
 */
public class EntityAlafha extends EntityParasiteBase {
    public static final float MAX_HEALTH = 150.0F;
    public static final float ATTACK_DAMAGE = 14.0F;
    public static final float MOVEMENT_SPEED = 0.26F;
    public static final float ARMOR = 10.0F;

    public EntityAlafha(EntityType<EntityAlafha> type, Level world) {
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
