package com.subspaceparasite.common.network;

import com.subspaceparasite.core.ModParticleTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Server-to-Client packet for spawning custom particles on the client.
 * Ported from SPPacketParticle (1.12) to 1.20.1.
 * Supports multiple particle effect types with position and spread data.
 */
public class S2CParticlePacket implements SPacket {

    /**
     * Particle effect types matching the original SRP particle types.
     */
    public static final byte TYPE_GREEN_SPLASH = 1;
    public static final byte TYPE_GREEN_CLOUD = 2;
    public static final byte TYPE_RED_HAPPY = 3;
    public static final byte TYPE_GREEN_CLOUD_TINTED = 4;
    public static final byte TYPE_GORE_SMALL = 10;
    public static final byte TYPE_GORE_LARGE = 11;

    private final double x;
    private final double y;
    private final double z;
    private final float width;
    private final float height;
    private final byte particleType;

    public S2CParticlePacket(double x, double y, double z, float width, float height, byte particleType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.particleType = particleType;
    }

    public S2CParticlePacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.width = buf.readFloat();
        this.height = buf.readFloat();
        this.particleType = buf.readByte();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.width);
        buf.writeFloat(this.height);
        buf.writeByte(this.particleType);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level == null) return;

                Random rand = new Random();
                switch (this.particleType) {
                    case TYPE_GREEN_SPLASH:
                        spawnModParticles(level, rand, ModParticleTypes.SPORE.get(), 3);
                        break;
                    case TYPE_GREEN_CLOUD:
                        spawnModParticles(level, rand, ModParticleTypes.SPORE.get(), 4);
                        break;
                    case TYPE_RED_HAPPY:
                        spawnModParticles(level, rand, ModParticleTypes.EVOLUTION_SPARK.get(), 6);
                        break;
                    case TYPE_GREEN_CLOUD_TINTED:
                        spawnModParticles(level, rand, ModParticleTypes.ASSIMILATE.get(), 24);
                        break;
                    case TYPE_GORE_SMALL:
                        spawnModParticles(level, rand, ModParticleTypes.DEAD_BLOOD_FALL.get(), 5);
                        spawnModParticles(level, rand, ModParticleTypes.CAUSTIC_SPLASH.get(), 5);
                        break;
                    case TYPE_GORE_LARGE:
                        spawnModParticles(level, rand, ModParticleTypes.DEAD_BLOOD_FALL.get(), 33);
                        spawnModParticles(level, rand, ModParticleTypes.CAUSTIC_SPLASH.get(), 13);
                        break;
                    default:
                        // Fallback: use vanilla particles
                        spawnVanillaParticles(level, rand, 5);
                        break;
                }
            });
        });
        context.get().setPacketHandled(true);
    }

    /**
     * Spawn mod particles with random offset within the entity bounding box.
     */
    private void spawnModParticles(ClientLevel level, Random rand, SimpleParticleType particleType, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (rand.nextFloat() * this.width * 2.0f) - this.width;
            double offsetY = rand.nextFloat() * this.height;
            double offsetZ = (rand.nextFloat() * this.width * 2.0f) - this.width;
            double dx = rand.nextGaussian() * 0.02;
            double dy = rand.nextGaussian() * 0.02;
            double dz = rand.nextGaussian() * 0.02;
            level.addParticle(particleType,
                    this.x + offsetX, this.y + 0.5 + offsetY, this.z + offsetZ,
                    dx, dy, dz);
        }
    }

    /**
     * Fallback: spawn vanilla particles.
     */
    private void spawnVanillaParticles(ClientLevel level, Random rand, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (rand.nextFloat() * this.width * 2.0f) - this.width;
            double offsetY = rand.nextFloat() * this.height;
            double offsetZ = (rand.nextFloat() * this.width * 2.0f) - this.width;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.POOF,
                    this.x + offsetX, this.y + 0.5 + offsetY, this.z + offsetZ,
                    rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
        }
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getZ() { return this.z; }
    public float getWidth() { return this.width; }
    public float getHeight() { return this.height; }
    public byte getParticleType() { return this.particleType; }
}
