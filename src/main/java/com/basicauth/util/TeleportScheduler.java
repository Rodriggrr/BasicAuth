package com.basicauth.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeleportScheduler {
    public static void init() {}
    private static final Map<UUID, Integer> pendingTeleports = new HashMap<>();
    static {
        ServerTickEvents.START_SERVER_TICK.register(TeleportScheduler::onTick); // trocado para START
    }

    public static void schedule(ServerPlayerEntity player, int delayTicks) {
        pendingTeleports.put(player.getUuid(), delayTicks);
    }

    private static void onTick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Integer>> iterator = pendingTeleports.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID uuid = entry.getKey();
            int ticksLeft = entry.getValue() - 1;

            if (ticksLeft <= 0) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    double x = player.getX() + 0.0001; // força mudança
                    double y = player.getY();
                    double z = player.getZ();

                    player.networkHandler.requestTeleport(x, y, z, player.getYaw(), player.getPitch());
                }
                iterator.remove();
            } else {
                entry.setValue(ticksLeft);
            }
        }
    }
}
