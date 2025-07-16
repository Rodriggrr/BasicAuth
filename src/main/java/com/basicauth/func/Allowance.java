package com.basicauth.func;

import net.minecraft.world.GameMode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.basicauth.util.LocatedAndParsed.parseFromJSON;
import static com.basicauth.BasicAuth.REGISTER_NEEDS_ALLOWANCE;

import com.basicauth.exception.*;
import com.basicauth.player.*;
import com.basicauth.util.helper.MovementState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Allowance {
    private static Logger LOGGER = LoggerFactory.getLogger(Allowance.class);

    public static boolean allow(String player, ServerCommandSource source) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
        try {
            if(playerData == null) {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("player_not_found", player)), false);
                return false;
            }

            if(playerData.isAllowed()) {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.already_allowed", player)), false);
                return false;
            }

            playerData.setAllowed(true);
            PlayerDataHandler.savePlayerData(playerData);

            source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.allowed_successfully", player)), false);
            

            // Feedback to the player being allowed
            var other = source.getServer().getPlayerManager().getPlayer(player);
            if (other != null) {
                other.sendMessage(parseFromJSON("admin.allowed_notification"), false);
            }

        } catch (Exception e) {
            LOGGER.error("Error occurred while checking allowance for player {}: {}", player, e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean deny(String player, ServerCommandSource source) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player);
        try {
            if(playerData == null) {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("player_not_found", player)), false);
                return false;
            }

            if(!playerData.isAllowed()) {
                source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.already_denied", player)), false);
                return false;
            }
            playerData.setAllowed(false);
            PlayerDataHandler.savePlayerData(playerData);

            source.sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.denied_successfully", player)), false);

            // Feedback to the player being denied
            var other = source.getServer().getPlayerManager().getPlayer(player);
            MovementState.reset(other);
            if (other != null) {
                other.sendMessage(parseFromJSON("admin.denied_notification"), false);
            }

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
