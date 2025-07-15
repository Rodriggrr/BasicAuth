package com.basicauth.func;

import net.minecraft.world.GameMode;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.basicauth.util.LocatedAndParsed.parseFromJSON;
import static com.basicauth.BasicAuth.REGISTER_NEEDS_ALLOWANCE;

import com.basicauth.exception.*;
import com.basicauth.player.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Allowance {
    private static Logger LOGGER = LoggerFactory.getLogger(Allowance.class);

    public static boolean allow(String player, ServerPlayerEntity source) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
        try {
            if(playerData == null) {
                source.sendMessage(parseFromJSON("player_not_found"), false);
                return false;
            }

            if(playerData.isAllowed()) {
                source.sendMessage(parseFromJSON("admin.already_allowed"));
                return false;
            }

            playerData.setAllowed(true);
            PlayerDataHandler.savePlayerData(playerData);

            source.sendMessage(parseFromJSON("admin.allowed_successfully"));


        } catch (Exception e) {
            LOGGER.error("Error occurred while checking allowance for player {}: {}", player, e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean deny(String player, ServerPlayerEntity source) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
        try {
            if(playerData == null) {
                source.sendMessage(parseFromJSON("player_not_found"), false);
                return false;
            }

            if(!playerData.isAllowed()) {
                source.sendMessage(parseFromJSON("admin.already_denied"));
                return false;
            }

            playerData.setAllowed(false);
            PlayerDataHandler.savePlayerData(playerData);

            source.sendMessage(parseFromJSON("admin.denied_successfully"));


        } catch (Exception e) {
            LOGGER.error("Error occurred while checking allowance for player {}: {}", player, e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean allowed(ServerPlayerEntity player) throws PlayerDataIsNull {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player.getName().getString());
        
        if(playerData == null) return false;

        return (REGISTER_NEEDS_ALLOWANCE ? playerData.isAllowed() : true) && playerData.isAuthenticated();
    }

    public static void setGameMode(ServerPlayerEntity player) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);

        if(playerData == null || !playerData.isAuthenticated() || (!playerData.isAllowed() && REGISTER_NEEDS_ALLOWANCE)) {
            player.changeGameMode(GameMode.SPECTATOR);
            return;
        }
        
        player.changeGameMode(player.getServer().getDefaultGameMode());
    }
}
