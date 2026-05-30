package com.subspaceparasite.common.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.entity.PartEntity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkHooks;

/**
 * Multi-part hitbox entity extending PartEntity<EntityParasiteBase>.
 * Allows parasites to have multiple damageable hitboxes (e.g., for large bosses
 * or entities with separate body parts that can be targeted independently).
 *
 * All damage received by hitbox parts is forwarded to the parent parasite entity.
 * The parent's CombatComponent handles damage cap and adaptation logic.
 */
public class EntityHitbox extends PartEntity<EntityParasiteBase> {

    // Hitbox dimensions (relative to parent)
    private final float hitboxWidth;
    private final float hitboxHeight;

    // Scale factors for positioning relative to parent
    private final float widthScale;
    private final float heightScale;

    // Offsets from parent position
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;

    // Vulnerability flag - some hitboxes may be invulnerable
    private boolean vulnerable;

    // Damage multiplier for this hitbox part
    private float damageMultiplier;

    // Dislodgment flag - whether hitting this part triggers dislodgment effects
    private boolean triggersDislodgment;

    /**
     * Create a new hitbox part for a parasite entity.
     *
     * @param parent           The parent parasite entity
     * @param hitboxWidth      Width of this hitbox
     * @param hitboxHeight     Height of this hitbox
     * @param widthScale       Scale factor for width positioning
     * @param heightScale      Scale factor for height positioning
     * @param offsetX          X offset from parent position
     * @param offsetY          Y offset from parent position
     * @param offsetZ          Z offset from parent position
     */
    public EntityHitbox(EntityParasiteBase parent, float hitboxWidth, float hitboxHeight,
                        float widthScale, float heightScale,
                        float offsetX, float offsetY, float offsetZ) {
        super(parent);
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.widthScale = widthScale;
        this.heightScale = heightScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.vulnerable = true;
        this.damageMultiplier = 1.0F;
        this.triggersDislodgment = false;

        // Set initial dimensions
        this.setDimensions(hitboxWidth, hitboxHeight);

        // Position relative to parent
        this.moveTo(parent.getX() + offsetX, parent.getY() + offsetY, parent.getZ() + offsetZ);
    }

    /**
     * Simplified constructor with default scale and no offset.
     */
    public EntityHitbox(EntityParasiteBase parent, float hitboxWidth, float hitboxHeight) {
        this(parent, hitboxWidth, hitboxHeight, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
    }

    // ========== Tick ==========

    @Override
    public void tick() {
        super.tick();

        // Update position to follow parent
        EntityParasiteBase parent = getParent();
        if (parent != null && parent.isAlive()) {
            // Calculate position based on parent's rotation and offsets
            double radYaw = Math.toRadians(-parent.getYRot());
            double cosYaw = Math.cos(radYaw);
            double sinYaw = Math.sin(radYaw);

            double rotatedX = offsetX * cosYaw - offsetZ * sinYaw;
            double rotatedZ = offsetX * sinYaw + offsetZ * cosYaw;

            this.setPos(
                    parent.getX() + rotatedX,
                    parent.getY() + offsetY,
                    parent.getZ() + rotatedZ
            );

            // Update rotation to match parent
            this.setYRot(parent.getYRot());
            this.setXRot(parent.getXRot());
            this.yRotO = parent.yRotO;
            this.xRotO = parent.xRotO;
        } else {
            // Parent is dead/gone - discard this hitbox
            this.discard();
        }
    }

    // ========== Damage Forwarding ==========

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!vulnerable) return false;
        if (!this.isAlive()) return false;

        EntityParasiteBase parent = getParent();
        if (parent == null || !parent.isAlive()) return false;

        // Apply damage multiplier for this hitbox part
        float adjustedDamage = amount * damageMultiplier;

        // Apply dislodgment effects if this hitbox triggers them
        if (triggersDislodgment && source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            parent.applyDislodgmentOnHurt(attacker, adjustedDamage);
        }

        // Forward damage to parent
        return parent.hurt(source, adjustedDamage);
    }

    @Override
    public boolean isPickable() {
        return this.isAlive() && vulnerable;
    }

    // ========== Dimensions ==========

    /**
     * Set the hitbox dimensions.
     * Calls Entity.refreshDimensions() internally in 1.20.1.
     */
    public void setDimensions(float width, float height) {
        // In 1.20.1, we set the entity dimensions via the entity size system
        // PartEntity uses the parent's dimensions by default, but we override
        this.setBoundingBox(this.getBoundingBox().inflate(
                (width - this.getDimensions(net.minecraft.world.entity.Pose.STANDING).width) / 2.0,
                (height - this.getDimensions(net.minecraft.world.entity.Pose.STANDING).height) / 2.0,
                (width - this.getDimensions(net.minecraft.world.entity.Pose.STANDING).width) / 2.0));
    }

    @Override
    protected void defineSynchedData() {
        // PartEntity doesn't need its own synced data
    }

    // PartEntity dimensions are handled by the parent entity
    // getBbWidth() and getBbHeight() are final in Entity 1.20.1 and cannot be overridden

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        // PartEntity instances are not sent as separate packets in vanilla
        // They are synced through the parent entity
        throw new UnsupportedOperationException("EntityHitbox should not be spawned via packets");
    }

    // ========== NBT ==========

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("HitboxWidth", hitboxWidth);
        tag.putFloat("HitboxHeight", hitboxHeight);
        tag.putFloat("WidthScale", widthScale);
        tag.putFloat("HeightScale", heightScale);
        tag.putFloat("OffsetX", offsetX);
        tag.putFloat("OffsetY", offsetY);
        tag.putFloat("OffsetZ", offsetZ);
        tag.putBoolean("Vulnerable", vulnerable);
        tag.putFloat("DamageMultiplier", damageMultiplier);
        tag.putBoolean("TriggersDislodgment", triggersDislodgment);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Note: dimensions are final fields, so we can't change them on load
        // This is fine because hitbox configuration is determined at creation time
        vulnerable = tag.getBoolean("Vulnerable");
        damageMultiplier = tag.getFloat("DamageMultiplier");
        triggersDislodgment = tag.getBoolean("TriggersDislodgment");
    }

    /**
     * The hitbox is considered the same entity as its parent for targeting.
     */
    @Override
    public boolean is(Entity entity) {
        return this == entity || getParent() == entity;
    }

    // ========== Accessors ==========

    public boolean isVulnerable() { return vulnerable; }
    public void setVulnerable(boolean value) { this.vulnerable = value; }

    public float getDamageMultiplier() { return damageMultiplier; }
    public void setDamageMultiplier(float value) { this.damageMultiplier = value; }

    public boolean triggersDislodgment() { return triggersDislodgment; }
    public void setTriggersDislodgment(boolean value) { this.triggersDislodgment = value; }

    public float getHitboxWidth() { return hitboxWidth; }
    public float getHitboxHeight() { return hitboxHeight; }
}
