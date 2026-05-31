package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.ai.misc.EntityBodyParts;
import com.subspaceparasite.network.ModMessages;
import com.subspaceparasite.network.packet.C2SBodyPartHitPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkDirection;

import java.util.List;

/**
 * EntityBody - Body part entity for multi-part parasites.
 * <p>
 * Direct port from original SRP EntityBody (169 lines).
 * Handles individual body part positioning, damage delegation,
 * and collision with nearby entities.
 * <p>
 * In 1.20.1, uses Forge's PartEntity system instead of 1.12's MultiPartEntityPart.
 */
public class EntityBody extends Entity implements PartEntity<EntityParasiteBase> {
    
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID = 
        SynchedEntityData.defineId(EntityBody.class, EntityDataSerializers.INT);
    
    protected EntityParasiteBase parent;
    protected float damageMultiplier;
    private float eyeHeight;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private int inverted;
    private int partId;
    private boolean logicSide;
    
    /**
     * Constructor for body parts.
     *
     * @param parent           The parent parasite entity
     * @param width            Hitbox width
     * @param height           Hitbox height
     * @param damageMultiplier Damage multiplier for this part
     * @param offset           Horizontal offset from parent
     * @param offheight        Vertical offset from parent
     * @param inv              Inversion flag (-1 or 1)
     * @param id               Part ID
     * @param side             Whether to use side-based positioning logic
     */
    public EntityBody(EntityParasiteBase parent, float width, float height, 
                      float damageMultiplier, float offset, float offheight, 
                      int inv, int id, boolean side) {
        super(EntityType.ARMOR_STAND, parent.level()); // Placeholder type, actual type doesn't matter for parts
        this.parent = parent;
        this.damageMultiplier = damageMultiplier;
        this.eyeHeight = height * 0.8f;
        this.offsetX = offset;
        this.offsetY = offheight;
        this.offsetZ = offset;
        this.inverted = inv;
        this.partId = id;
        this.logicSide = side;
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }
    
    /**
     * Constructor with custom eye height.
     */
    public EntityBody(EntityParasiteBase parent, float width, float height, 
                      float damageMultiplier, float offset, float offheight, 
                      int inv, int id, boolean side, float eyes) {
        this(parent, width, height, damageMultiplier, offset, offheight, inv, id, side);
        this.eyeHeight = eyes;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PARENT_ID, 0);
    }
    
    @Override
    public void tick() {
        if (this.parent == null || this.parent.isRemoved()) {
            this.discard();
            return;
        }
        
        if (this.logicSide) {
            this.updatePositionWithParentSides();
        } else {
            this.updatePositionWithParentFront();
        }
        
        // Collision with nearby entities
        this.collideWithNearbyEntities();
        
        super.tick();
    }
    
    /**
     * Update position based on parent's rotation (side-based logic).
     */
    private void updatePositionWithParentSides() {
        float yawRad = this.parent.getYRot() * ((float) Math.PI / 180.0F);
        float sin = Mth.sin(yawRad);
        float cos = Mth.cos(yawRad);
        
        this.setYRot(this.parent.getYRot());
        this.setPos(
            this.parent.getX() + (double) this.inverted * (double) (cos * this.offsetX),
            this.parent.getY() + (double) this.offsetY,
            this.parent.getZ() + (double) this.inverted * (double) (sin * this.offsetZ)
        );
    }
    
    /**
     * Update position based on parent's rotation (front-based logic).
     */
    private void updatePositionWithParentFront() {
        float yawRad = this.parent.getYRot() * ((float) Math.PI / 180.0F);
        float pitchFactor = 0.17453292f;
        float cosPitch = Mth.cos(pitchFactor);
        float sinYaw = Mth.sin(yawRad);
        float cosYaw = Mth.cos(yawRad);
        
        this.setYRot(this.parent.getYRot());
        this.setPos(
            this.parent.getX() + (double) this.inverted * (double) (sinYaw * this.offsetX * cosPitch),
            this.parent.getY() + (double) this.offsetY,
            this.parent.getZ() - (double) this.inverted * (double) (cosYaw * this.offsetX * cosPitch)
        );
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide && source.getEntity() instanceof Player player) {
            // Send packet to server for damage processing
            ModMessages.INSTANCE.sendToServer(new C2SBodyPartHitPacket(this.parent.getId(), this.partId));
            return false;
        }
        
        if (this.parent instanceof EntityBodyParts bodyParts) {
            return bodyParts.attackEntityBodyFrom(source, amount * this.damageMultiplier, this.partId, false);
        }
        
        if (this.parent != null) {
            return this.parent.hurt(source, amount * this.damageMultiplier);
        }
        
        return super.hurt(source, amount);
    }
    
    /**
     * Collide with and push nearby non-parasite entities.
     */
    protected void collideWithNearbyEntities() {
        List<Entity> entities = this.level().getEntities(this, 
            this.getBoundingBox().inflate(1.0, 1.0, 1.0));
        
        double centerX = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0;
        double centerZ = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0;
        
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living) || 
                entity == this.parent || 
                entity instanceof EntityParasiteBase) {
                continue;
            }
            
            double dx = entity.getX() - centerX;
            double dz = entity.getZ() - centerZ;
            double distSq = dx * dx + dz * dz;
            
            if (distSq > 0.0) {
                entity.push(dx / distSq, 0.2f, dz / distSq);
                entity.hurt(DamageSource.mobAttack(this.parent), 5.0f);
            }
        }
    }
    
    /**
     * Get the part ID.
     */
    public int getId() {
        return this.partId;
    }
    
    /**
     * Get the parent parasite.
     */
    public EntityParasiteBase getParent() {
        return this.parent;
    }
    
    @Override
    public float getEyeHeight() {
        return this.eyeHeight;
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public AABB getBoundingBox() {
        return super.getBoundingBox();
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    /**
     * Set body part size.
     */
    public void setBodySize(float width, float height) {
        this.dimensions = EntityType.ARMOR_STAND.getDimensions();
        // Note: In 1.20.1, entity dimensions are handled differently
    }
    
    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parent == entity;
    }
    
    @Override
    public EntityParasiteBase getParent() {
        return this.parent;
    }
}
