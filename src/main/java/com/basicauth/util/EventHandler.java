package com.basicauth.util;

import com.basicauth.commands.Commands;
import com.basicauth.util.helper.OpsHelper;
import static com.basicauth.func.Allowance.allowed;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import static com.basicauth.func.Login.announceLogin;
import static com.basicauth.func.Login.logout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

    public static void init() {
        JOIN();
        DISCONNECT();
        UseBlockCallback();
        CommandRegistrationCallback();
        ALLOW_DAMAGE();
        LOGGER.info("BasicAuth is working!");
    }

    public static void JOIN() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			player.sendMessage(announceLogin(player));
			if (OpsHelper.isOp(player)) {
				OpsHelper.ops.put(player.getName().getString(), player);
			}
		});
    }

    public static void DISCONNECT() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			logout(player);
			OpsHelper.refreshOps(server);
		});
    }

    public static void UseBlockCallback() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			try {
				if (!allowed(player.getServer().getPlayerManager().getPlayer(player.getUuid()))) {
					return ActionResult.FAIL; // Bloqueia a interação apenas se não for aprovado
				}
			} catch (Exception e) {
				LOGGER.error("Error in UseBlockCallback: {}", e.getMessage());
			}
			return ActionResult.PASS;
		});
    }

    public static void CommandRegistrationCallback() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			Commands.registerCommands(dispatcher);
		});
    }

    public static void ALLOW_DAMAGE() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof PlayerEntity player) {
                if (!allowed(player.getName().getString())) {
                    return false; // cancela dano
                }
            }
            return true; // permite dano normalmente
        });
    }
}
