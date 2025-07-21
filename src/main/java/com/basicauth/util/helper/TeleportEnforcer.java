package com.basicauth.util.helper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import static com.basicauth.func.Allowance.allowed;


import java.util.*;

import com.basicauth.player.PlayerDataHandler;

public class TeleportEnforcer {
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
                        var playerData = PlayerDataHandler.loadPlayerData(player.getGameProfile().getName());
                        if (playerData.getLatestGameMode() == null || playerData.getLatestGameMode().isEmpty()) {
                            playerData.setLatestGameMode(player.getServer().getDefaultGameMode().toString().toUpperCase());
                            PlayerDataHandler.savePlayerData(playerData);
                            PlayerDataHandler.refreshPlayerCache(player);
                        }

                        player.changeGameMode(GameMode.valueOf(playerData.getLatestGameMode().toUpperCase()));
                        player.getServer().getCommandManager().sendCommandTree(player);
                        iter.remove();
                        continue;
                    }

                    if (ticks <= 0) {
                        // Re-teleporta o jogador
                        player.networkHandler.requestTeleport(
                                player.getX(), player.getY(), player.getZ(),
                                player.getYaw(), player.getPitch()
                        );
                        // Reinicia contador (20 ticks = 1 segundo)
                        entry.setValue(20);
                    } else {
                        entry.setValue(ticks - 1); // Decrementa o contador
                    }
                } catch (Exception e) {
                    System.err.println("Error in TeleportEnforcer: " + e.getMessage());
                    iter.remove(); // Remove the entry if an error occurs
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
}