package com.basicauth.player;

import com.basicauth.debug.LoggerStatic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;



public class PlayerModel {
    private String username;
    private String password = "";
    private int loginAttemptCount = 0;
    private String latestGameMode = null;

    private boolean isAuthenticated = false;
    private boolean isAllowed = false;

    public PlayerModel(String username, String password, ServerPlayerEntity playerReference) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void incrementLoginAttemptCount() {
        this.loginAttemptCount++;
    }

    public void resetLoginAttemptCount() {
        this.loginAttemptCount = 0;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }

    public String getLatestGameMode() {
        return latestGameMode;
    }

    public void setLatestGameMode(String latestGameMode) {
        LoggerStatic.info("Setting latest game mode for player " + username + ": " + latestGameMode);
        this.latestGameMode = latestGameMode;
    }

    public static void changeGameModeToLatest(ServerPlayerEntity player) {
        var playerData = PlayerDataHandler.loadPlayerData(player.getName().getString());
        if (playerData != null) {
            String latestGameMode = playerData.getLatestGameMode();
            if (latestGameMode != null && !latestGameMode.isEmpty()) {
                try {
                    GameMode mode = GameMode.valueOf(latestGameMode.toUpperCase());
                    // Assuming there's a method to change the player's game mode
                    player.changeGameMode(mode);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid game mode: " + latestGameMode);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        PlayerModel that = (PlayerModel) obj;
        return username.equals(that.username);
    }

    public boolean equals(ServerPlayerEntity player) {
        return this.username.equals(player.getName().getString());
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;   
    }
}
