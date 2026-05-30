package com.subspaceparasite.client.particle;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

/**
 * Custom particle with configurable behavior modes and SpriteSet support.
 */
public class ParasiteParticle extends TextureSheetParticle {

    /**
     * Behavior modes for different particle types.
     */
    public enum BehaviorMode {
        FLOAT,      // Float upward slowly (spore, viral)
        FALL,       // Fall downward (bile, dissolve)
        EXPAND,     // Expand outward (corruption, evolution)
        SPREAD,     // Spread along ground (infestation)
        ORBIT,      // Orbit around center (nexus)
        RISE_FAST   // Rise quickly (primordial, ancient)
    }

    protected final BehaviorMode behaviorMode;
    protected final SpriteSet spriteSet;

    // Mode-specific parameters
    protected float orbitAngle;
    protected double orbitCenterX;
    protected double orbitCenterZ;
    protected float orbitRadius;
    protected float expandScale;

    protected ParasiteParticle(ClientLevel level, double x, double y, double z,
                               double xSpeed, double ySpeed, double zSpeed,
                               BehaviorMode mode, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.behaviorMode = mode;
        this.spriteSet = spriteSet;

        this.setSpriteFromAge(spriteSet);
        this.lifetime = this.random.nextInt(30) + 20;
        this.gravity = 0.0f;
        this.friction = 0.98f;
        this.quadSize = 0.1f + this.random.nextFloat() * 0.1f;

        // Configure based on behavior mode
        switch (mode) {
            case FLOAT:
                this.yd = 0.02 + this.random.nextDouble() * 0.02;
                this.lifetime = 40 + this.random.nextInt(30);
                break;
            case FALL:
                this.gravity = 0.05f;
                this.yd = -0.02;
                this.lifetime = 30 + this.random.nextInt(20);
                break;
            case EXPAND:
                this.expandScale = 0.02f;
                this.lifetime = 20 + this.random.nextInt(15);
                break;
            case SPREAD:
                this.gravity = -0.01f; // slight upward to stay on ground
                this.yd = 0.01;
                this.lifetime = 35 + this.random.nextInt(25);
                break;
            case ORBIT:
                this.orbitAngle = this.random.nextFloat() * 360.0f;
                this.orbitCenterX = x;
                this.orbitCenterZ = z;
                this.orbitRadius = 1.0f + this.random.nextFloat() * 2.0f;
                this.lifetime = 50 + this.random.nextInt(30);
                break;
            case RISE_FAST:
                this.yd = 0.1 + this.random.nextDouble() * 0.05;
                this.lifetime = 15 + this.random.nextInt(15);
                break;
        }

        // Set color tinting (purple/green parasite palette)
        float colorVariant = this.random.nextFloat();
        if (colorVariant < 0.5f) {
            // Purple tint
            this.rCol = 0.5f + this.random.nextFloat() * 0.2f;
            this.gCol = 0.2f + this.random.nextFloat() * 0.1f;
            this.bCol = 0.6f + this.random.nextFloat() * 0.3f;
        } else {
            // Green tint
            this.rCol = 0.2f + this.random.nextFloat() * 0.1f;
            this.gCol = 0.5f + this.random.nextFloat() * 0.3f;
            this.bCol = 0.2f + this.random.nextFloat() * 0.1f;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        // Update sprite
        this.setSpriteFromAge(this.spriteSet);

        // Behavior-specific tick logic
        switch (this.behaviorMode) {
            case EXPAND:
                this.quadSize += this.expandScale;
                this.alpha = 1.0f - (float) this.age / this.lifetime;
                break;
            case ORBIT:
                this.orbitAngle += 3.0f;
                double rad = Math.toRadians(this.orbitAngle);
                this.x = this.orbitCenterX + Math.cos(rad) * this.orbitRadius;
                this.z = this.orbitCenterZ + Math.sin(rad) * this.orbitRadius;
                this.orbitRadius *= 0.995; // slowly shrink orbit
                this.alpha = 1.0f - (float) this.age / this.lifetime;
                break;
            case SPREAD:
                // Slow down horizontal movement
                this.xd *= 0.96;
                this.zd *= 0.96;
                break;
            default:
                // Standard fade out
                if (this.age > this.lifetime * 0.7f) {
                    this.alpha = 1.0f - ((float) this.age - this.lifetime * 0.7f) / (this.lifetime * 0.3f);
                }
                break;
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Parasite particles glow slightly
        return Math.max(super.getLightColor(partialTick), 100);
    }

    /**
     * Factory for creating ParasiteParticle instances.
     */
    public static class Factory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            // Default to FLOAT behavior
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.FLOAT, this.spriteSet);
        }
    }

    /**
     * Factory for FLOAT behavior particles.
     */
    public static class FloatFactory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FloatFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.FLOAT, this.spriteSet);
        }
    }

    /**
     * Factory for FALL behavior particles.
     */
    public static class FallFactory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FallFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.FALL, this.spriteSet);
        }
    }

    /**
     * Factory for EXPAND behavior particles.
     */
    public static class ExpandFactory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ExpandFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.EXPAND, this.spriteSet);
        }
    }

    /**
     * Factory for SPREAD behavior particles.
     */
    public static class SpreadFactory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SpreadFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.SPREAD, this.spriteSet);
        }
    }

    /**
     * Factory for ORBIT behavior particles.
     */
    public static class OrbitFactory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public OrbitFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public ParasiteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new ParasiteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    BehaviorMode.ORBIT, this.spriteSet);
        }
    }
}
