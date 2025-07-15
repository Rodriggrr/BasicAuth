package com.basicauth.util.helper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.network.ServerPlayerEntity;

public class MovementState {
    private static final Set<UUID> teleportedOnUnlock = new HashSet<>();

    public static boolean wasTeleported(ServerPlayerEntity player) {
        return teleportedOnUnlock.contains(player.getUuid());
    }

    public static void markTeleported(ServerPlayerEntity player) {
        teleportedOnUnlock.add(player.getUuid());
    }

    public static void reset(ServerPlayerEntity player) {
        teleportedOnUnlock.remove(player.getUuid());
    }
}
