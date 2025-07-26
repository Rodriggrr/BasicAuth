package com.basicauth.util.helper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.world.chunk.WorldChunk;

import static com.basicauth.func.Allowance.allowed;

import java.util.*;

public class StopPlayerActions {
    private static final Map<UUID, Integer> timers = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, Integer>> iter = timers.entrySet().iterator();

            while (iter.hasNext()) {
                try {
                    Map.Entry<UUID, Integer> entry = iter.next();
                    UUID uuid = entry.getKey();
                    int ticks = entry.getValue();

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

                    if (ticks <= 0) {

                        player.networkHandler.requestTeleport(
                                player.getX(), player.getY(), player.getZ(),
                                player.getYaw(), player.getPitch());
                        // Resend the chunk data to the player, as in the client, visually, the player
                        // broke the block
                        // and the client needs to update the chunk data to fix the visual error.
                        synchronizeChunk(player);

                        entry.setValue(20);
                    } else {
                        entry.setValue(ticks - 1);
                    }
                } catch (Exception e) {
                    System.err.println("Error in TeleportEnforcer: " + e.getMessage());
                    iter.remove();
                }
            }
        });
    }

    public static void start(ServerPlayerEntity player) {
        timers.putIfAbsent(player.getUuid(), 20); // só define se não existir ainda
    }

    public static void stop(ServerPlayerEntity player) {
        timers.remove(player.getUuid());
    }


    // sincroniza o chunk reenviando o ckunk para o cliente
    private static void synchronizeChunk(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        WorldChunk chunk = world.getChunk(player.getBlockX() >> 4, player.getBlockZ() >> 4);

        player.networkHandler.sendPacket(new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null));
    }
}