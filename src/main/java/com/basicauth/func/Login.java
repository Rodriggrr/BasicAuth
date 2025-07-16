package com.basicauth.func;

import net.minecraft.server.network.ServerPlayerEntity;

import static com.basicauth.util.LocatedAndParsed.parseFromJSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basicauth.BasicAuth;
import com.basicauth.exception.MalformedParsedString;
import com.basicauth.player.*;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Login {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);


    /**
     * Authenticates a player with the given password.
     * 
     * @param player The player entity to authenticate.
     * @param password The password to authenticate with.
     */
    public static boolean authenticate(ServerPlayerEntity player, String password) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
        try {
            if (playerData == null) {
                player.sendMessage(parseFromJSON("login.not_registered", player), false);
                return false;
            }
            if (playerData.isAuthenticated()) {
                player.sendMessage(parseFromJSON("login.already_authenticated"), false);
                return false;
            }
            if (playerData.getPassword().equals(password)) {
                playerData.setAuthenticated(true);
                PlayerDataHandler.savePlayerData(playerData);
                player.sendMessage(parseFromJSON("login.success"), false);

            } else {
                playerData.incrementLoginAttemptCount();
                PlayerDataHandler.savePlayerData(playerData);
                if (playerData.getLoginAttemptCount() >= BasicAuth.MAX_LOGIN_ATTEMPTS) {
                    player.sendMessage(parseFromJSON("login.too_many_attempts"), false);
                    // Optionally kick the player or take other actions
                } else {
                    player.sendMessage(parseFromJSON("login.failed"), false);
                }
            }
            return true;
        } catch (Exception e) {
            try {
                player.sendMessage(parseFromJSON("error_occurred"), false);
            } catch (MalformedParsedString malformedParsedString) {
                LOGGER.error("Failed to parse error message", malformedParsedString);
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void logout(ServerPlayerEntity player) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);

        try {
            if (playerData != null) {
                playerData.setAuthenticated(false);
                PlayerDataHandler.savePlayerData(playerData);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to logout player: {}", player.getName().getString(), e);
            e.printStackTrace();
        }
    }

    public static MutableText announceLogin(ServerPlayerEntity player) {
        try {

            PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
            if (playerData == null) {
                return parseFromJSON("welcome.new_player", player.getName().getString());
            }
            return parseFromJSON("welcome.back_player", playerData.getUsername());
        } catch (MalformedParsedString e) {
            LOGGER.error("Failed to parse welcome message", e);
            return Text.literal("Welcome to the server!");
        }
    }

    public static void awaitAdminAllowance(boolean loginSuccessful, ServerPlayerEntity player) {
        //method to broadcast to ops that soeone new have registered
        PlayerModel playerData =  PlayerDataHandler.loadPlayerData(player);
        boolean needs_allowance = BasicAuth.REGISTER_NEEDS_ALLOWANCE;
        if(!loginSuccessful)
            return;

        try {
            if(playerData.isAuthenticated() && (!playerData.isAllowed() && needs_allowance)) 
                player.sendMessage(parseFromJSON("admin.needs_allowance"));

        } catch (Exception e) {
            LOGGER.error("Error while getting player " + playerData.getUsername() + " permission.", e);
        }
    }
}
