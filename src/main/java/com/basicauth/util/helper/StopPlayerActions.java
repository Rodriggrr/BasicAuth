package com.basicauth.util.helper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.basicauth.func.Allowance.allowed;

public class StopPlayerActions {
    private static final Map<UUID, AtomicInteger> timers = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, AtomicInteger>> iter = timers.entrySet().iterator();

            while (iter.hasNext()) {
                try {
                    Map.Entry<UUID, AtomicInteger> entry = iter.next();
                    UUID uuid = entry.getKey();
                    AtomicInteger ticks = entry.getValue();

                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player == null) {
                        iter.remove();
                        continue;
                    }

                    if (allowed(player)) {
                        player.getServer().getCommandManager().sendCommandTree(player);
                        synchronizeChunk(player);
                        iter.remove();
                        continue;
                    }

                    if (ticks.get() <= 0) {
                        player.networkHandler.requestTeleport(
                                player.getX(), player.getY(), player.getZ(),
                                player.getYaw(), player.getPitch());

                        synchronizeChunk(player);
                        ticks.set(20); // ✅ altera valor sem modificar a estrutura
                    } else {
                        ticks.decrementAndGet(); // ✅ diminui de forma segura
                    }
                } catch (Exception e) {
                    System.err.println("Error in TeleportEnforcer: " + e.getMessage());
                    iter.remove();
                }
            }
        });
    }

    public static void start(ServerPlayerEntity player) {
        timers.putIfAbsent(player.getUuid(), new AtomicInteger(20)); // ✅ cria AtomicInteger
    }

    public static void stop(ServerPlayerEntity player) {
        timers.remove(player.getUuid());
    }

    private static void synchronizeChunk(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        WorldChunk chunk = world.getChunk(player.getBlockX() >> 4, player.getBlockZ() >> 4);
        player.networkHandler.sendPacket(new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null));
    }
}
