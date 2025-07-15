package com.auth.player;

import net.minecraft.server.network.ServerPlayerEntity;



public class PlayerModel {
    private String username;
    private String password = "";
    private ServerPlayerEntity playerReference;
    private int loginAttemptCount = 0;

    private boolean isAuthenticated = false;
    private boolean isAllowed = false;

    public PlayerModel(String username, String password, ServerPlayerEntity playerReference) {
        this.username = username;
        this.password = password;
        this.playerReference = playerReference;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ServerPlayerEntity getPlayerReference() {
        return playerReference;
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

    public void setPlayerReference(ServerPlayerEntity playerReference) {
        this.playerReference = playerReference;
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

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        PlayerModel that = (PlayerModel) obj;
        return username.equals(that.username) && playerReference.equals(that.playerReference);
    }

    public boolean equals(ServerPlayerEntity player) {
        return playerReference.equals(player);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;   
    }
}
