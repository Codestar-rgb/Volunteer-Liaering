package com.subspaceparasite.common.network;

import com.subspaceparasite.core.ModParticleTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;

/**
 * Server-to-Client packet for spawning custom particles on the client.
 * <p>
 * Sent when the server wants to display parasite-related particle effects
 * at a specific location. The particle type is mapped from an integer ID
 * to the corresponding {@link ModParticleTypes} entry.
 * <p>
 * Particle type mapping:
 * <ul>
 *   <li>0 = SPORE</li>
 *   <li>1 = VIRULENT_SPORE</li>
 *   <li>2 = EVOLUTION_SPARK</li>
 *   <li>3 = EVOLUTION_BURST</li>
 *   <li>4 = ASSIMILATE</li>
 *   <li>5 = DEAD_BLOOD_FALL</li>
 *   <li>6 = CAUSTIC_SPLASH</li>
 *   <li>7 = INFEST</li>
 *   <li>8 = DEINFEST</li>
 *   <li>9 = CORRUPTION</li>
 *   <li>10 = BILE</li>
 *   <li>11 = ACID_SPIT</li>
 *   <li>12 = NEXUS_PULSE</li>
 *   <li>13 = BIOME_FOG</li>
 *   <li>default = vanilla POOF</li>
 * </ul>
 */
public class ParasiteParticlePacket {

    public double x, y, z;
    public int particleType;
    public int count;

    public ParasiteParticlePacket(double x, double y, double z, int particleType, int count) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleType = particleType;
        this.count = count;
    }

    public static void encode(ParasiteParticlePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.particleType);
        buf.writeInt(msg.count);
    }

    public static ParasiteParticlePacket decode(FriendlyByteBuf buf) {
        return new ParasiteParticlePacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt());
    }

    public static void handle(ParasiteParticlePacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level == null) return;

                ParticleOptions particle = resolveParticleType(msg.particleType);
                Random rand = new Random();

                for (int i = 0; i < msg.count; i++) {
                    double offsetX = (rand.nextFloat() - 0.5) * 0.5;
                    double offsetY = rand.nextFloat() * 0.5;
                    double offsetZ = (rand.nextFloat() - 0.5) * 0.5;
                    double dx = rand.nextGaussian() * 0.02;
                    double dy = rand.nextGaussian() * 0.02 + 0.02;
                    double dz = rand.nextGaussian() * 0.02;
                    level.addParticle(particle,
                            msg.x + offsetX, msg.y + 0.5 + offsetY, msg.z + offsetZ,
                            dx, dy, dz);
                }
            });
        });
    }

    /**
     * Maps a particle type integer ID to the corresponding particle options.
     * Falls back to vanilla POOF particle for unknown IDs.
     *
     * @param typeId the particle type ID
     * @return the resolved particle options
     */
    private static ParticleOptions resolveParticleType(int typeId) {
        return switch (typeId) {
            case 0  -> ModParticleTypes.SPORE.get();
            case 1  -> ModParticleTypes.VIRULENT_SPORE.get();
            case 2  -> ModParticleTypes.EVOLUTION_SPARK.get();
            case 3  -> ModParticleTypes.EVOLUTION_BURST.get();
            case 4  -> ModParticleTypes.ASSIMILATE.get();
            case 5  -> ModParticleTypes.DEAD_BLOOD_FALL.get();
            case 6  -> ModParticleTypes.CAUSTIC_SPLASH.get();
            case 7  -> ModParticleTypes.INFEST.get();
            case 8  -> ModParticleTypes.DEINFEST.get();
            case 9  -> ModParticleTypes.CORRUPTION.get();
            case 10 -> ModParticleTypes.BILE.get();
            case 11 -> ModParticleTypes.ACID_SPIT.get();
            case 12 -> ModParticleTypes.NEXUS_PULSE.get();
            case 13 -> ModParticleTypes.BIOME_FOG.get();
            default -> net.minecraft.core.particles.ParticleTypes.POOF;
        };
    }
}
