package com.auth;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.auth.util.SimpleConfig;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.fabricmc.loader.api.FabricLoader;

public class BasicAuth implements ModInitializer {
	public static final String MOD_ID = "basicauth";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {registerCommands(dispatcher);
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
			SimpleConfig.ConfigRequest request = new SimpleConfig.ConfigRequest(
				FabricLoader.getInstance().getConfigDir().resolve("basicauth.json").toFile(),
				"basicauth.json"
			).provider(namespace -> {
				// Default config values
				if (namespace.equals("basicauth")) {
					return "{ \"enabled\": true, \"maxLoginAttempts\": 5 }";
				}
				return "";
			});
		});
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("parse")
				.then(CommandManager.argument("message", StringArgumentType.string())
					.executes(context -> {
						String message = StringArgumentType.getString(context, "message");
						try {
							MutableText parsedMessage = Colored.parse(message);
							context.getSource().sendFeedback(() -> parsedMessage, false);
						} catch (MalformedParsedString e) {
							context.getSource().sendError(Text.literal("Failed to parse message: " + e.getMessage()));
						}
						return 1;
					})
				)
		);
	}
}