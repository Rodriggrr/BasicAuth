package com.auth.func;

import com.auth.player.PlayerModel;
import com.auth.player.PlayerDataHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import static com.auth.util.LocatedAndParsed.parseFromJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Register {
    private static final Logger LOGGER = LoggerFactory.getLogger(Register.class);
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
                player.sendMessage(parseFromJSON("player_already_exists"), false);
                return;
            }

            playerData = new PlayerModel(player.getName().getString(), password, player);
            PlayerDataHandler.savePlayerData(playerData);
            player.sendMessage(parseFromJSON("register.success"), false);
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
