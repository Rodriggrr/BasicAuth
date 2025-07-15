package com.auth.func;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import static com.auth.util.LocatedAndParsed.parseFromJSON;
import net.minecraft.entity.player.PlayerEntity;
import com.auth.exception.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth.player.*;

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

    public static boolean shouldMove(PlayerEntity player) {
        return PlayerDataHandler.loadPlayerData(player.getName().getString()).isAllowed();
    }
}
