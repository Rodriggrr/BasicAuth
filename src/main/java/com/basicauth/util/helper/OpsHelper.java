package com.basicauth.util.helper;

import static com.basicauth.util.LocatedAndParsed.parseFromJSON;

import com.basicauth.exception.Wrapper;
import java.util.HashMap;
import java.util.Map;

import com.basicauth.player.PlayerDataHandler;
import com.basicauth.player.PlayerModel;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class OpsHelper {

    public static Map<String, ServerPlayerEntity> ops = new HashMap<>();
    public static Map<String, PlayerModel> playersDenied = new HashMap<>();

    public static boolean isOp(ServerPlayerEntity player) {
        return player.getServer().getPlayerManager().isOperator(player.getGameProfile());
    }

    public static Map<String, ServerPlayerEntity> getOps(MinecraftServer server) {
        var players = server.getPlayerManager().getPlayerList();
        ops.clear(); // Clear previous ops to avoid duplicates
        for (var player : players) {
            if (isOp(player)) {
                ops.put(player.getGameProfile().getName(), player);
            }
        }
        return ops;
    }

    public static void refreshOps(MinecraftServer server) {
        getOps(server);
    }

    public static void clearOps() {
        ops.clear();
    }

    public static void broadcastToOps(String message, Object... args) {
        for (ServerPlayerEntity op : ops.values()) {
            try {
                op.sendMessage(parseFromJSON(message, args), false);
            } catch (Exception e) {
                // Log the error but continue to send messages to other ops
                System.err.println("Failed to send message to op " + op.getGameProfile().getName() + ": " + e.getMessage());
            }
        }
    }

    public static void updateDeniedPlayers() {
        var playersData = PlayerDataHandler.loadAllPlayerData();
        playersDenied.clear(); // Clear previous denied players to avoid duplicates
        for (var playerData : playersData.values()) {
            if (!playerData.isAllowed()) {
                playersDenied.put(playerData.getUsername(), playerData);
            }
        }
    }

    public static void playersAwaitingAllowanceReminder(ServerPlayerEntity op) {
        updateDeniedPlayers();
        int amount = playersDenied.size();
        boolean found = amount > 0;
        try {
            if(found) op.sendMessage(parseFromJSON("admin.players_waiting_for_allowance", amount), false);
        } catch (Exception e) {
            System.err.println("Failed to send allowance reminder to op " + op.getGameProfile().getName() + ": " + e.getMessage());
        }
    }

    public static void listDeniedPlayers(ServerCommandSource source) {
        updateDeniedPlayers();
        if (playersDenied.isEmpty()) {
            try {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.no_players_waiting_for_allowance")), false);
            } catch (Exception e) {
                System.err.println("Failed to send no players message to op " + source.getName() + ": " + e.getMessage());
            }
            return;
        }

        StringBuilder message = new StringBuilder("");
        for (String playerName : playersDenied.keySet()) {
            message.append(playerName).append("\n");
            // remove last newline character
            message.setLength(message.length() - 1);
        }

        try {
            source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.list_denied", message.toString())), false);
        } catch (Exception e) {
            System.err.println("Failed to send denied players list to op " + source.getName() + ": " + e.getMessage());
        }
    }
    
    public static void deletePlayerData(ServerCommandSource source, String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return;
        }
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(playerName);
        if( playerData == null) {
            try {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("player_not_found", playerName)), false);
            } catch (Exception e) {
                System.err.println("Failed to send player not found message to op " + source.getName() + ": " + e.getMessage());
            }
            return;
        }

        var other = source.getServer().getPlayerManager().getPlayer(playerName);
        if (other != null) {
            other.changeGameMode(GameMode.SPECTATOR);
            try {
                other.sendMessage(parseFromJSON("player.has_been_deleted", playerName), false);
            } catch (Exception e) {
                System.err.println("Failed to send player deleted message to player " + playerName + ": " + e.getMessage());
            }
        }

        playersDenied.remove(playerName);
        if (playerData != null) {
            PlayerDataHandler.deletePlayerData(playerName);
        }

        try {
            broadcastToOps("admin.player_deleted", playerName);
        } catch (Exception e) {
            System.err.println("Failed to send player deletion message to op " + source.getName() + ": " + e.getMessage());
        }
    }

    public static void resetPassword(ServerCommandSource source, String playerName, String newPassword) {
        if (playerName == null || playerName.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return;
        }
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(playerName);
        if (playerData == null) {
            try {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("player_not_found", playerName)), false);
            } catch (Exception e) {
                System.err.println("Failed to send player not found message to op " + source.getName() + ": " + e.getMessage());
            }
            return;
        }

        playerData.setPassword(newPassword);
        playerData.setAuthenticated(false); // Reset authentication status
        playerData.resetLoginAttemptCount(); // Reset login attempts
        playerData.setAllowed(false); // Reset allowance status
        PlayerDataHandler.savePlayerData(playerData);

        try {
            source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.password_reset_success", playerName)), false);
        } catch (Exception e) {
            System.err.println("Failed to send password reset message to op " + source.getName() + ": " + e.getMessage());
        }
    }
}
