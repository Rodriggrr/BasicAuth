package com.basicauth.func;

import net.minecraft.server.network.ServerPlayerEntity;

import static com.basicauth.util.LocatedAndParsed.parseFromJSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basicauth.player.PlayerDataHandler;
import com.basicauth.player.PlayerModel;
import com.basicauth.util.Logging;
import com.basicauth.util.helper.OpsHelper;


public class Register {
    private static final Logger LOGGER = LoggerFactory.getLogger(Register.class);
    private static Logging log = new Logging();
    /**
     * Registers a new player with the given username and password.
     * 
     * @param player The player entity to register.
     * @param password The password for the new player.
     */
    public static void register(ServerPlayerEntity player, String password) {
        PlayerModel playerData = PlayerDataHandler.loadPlayerData(player.getName().getString());
        try {
            if (playerData != null) {
                player.sendMessage(parseFromJSON("register.already_registered"), false);
                return;
            }

            playerData = new PlayerModel(player.getName().getString(), password, player);
            PlayerDataHandler.savePlayerData(playerData);
            player.sendMessage(parseFromJSON("register.success"), false);
            OpsHelper.broadcastToOps("admin.new_player_registered", player.getName().getString());
            log.info("Player {} has been registered successfully.", player.getName().getString());

        } catch (Exception e) {
            LOGGER.error("Failed to register player: {}", player.getName().getString(), e);
            try {
                player.sendMessage(parseFromJSON("error_occurred"), false);
            } catch (Exception ex) {
                LOGGER.error("Failed to parse error message", ex);
                e.printStackTrace();
            }
        }
    }
}
