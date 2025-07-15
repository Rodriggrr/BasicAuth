package com.auth;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.auth.util.LocalizationManager;
import com.auth.util.SimpleConfig;
import com.auth.commands.Commands;

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

	SimpleConfig CONFIG = SimpleConfig.of("config").provider(this::provider).request();

	public final String LOCALE = CONFIG.getOrDefault( "locale", "en_US" );
	public final boolean REGISTER_NEEDS_ALLOWANCE = CONFIG.getOrDefault( "register_needs_allowance", true );
	public final int MAX_LOGIN_ATTEMPTS = CONFIG.getOrDefault( "max_login_attempts", 3 );

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {Commands.registerCommands(dispatcher);});


		LOGGER.info(LocalizationManager.get("test", LOCALE));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
		});
		
	}

	private String provider(String filename) {
			// Provide default config content or load from resources if needed
			return 
			"locale=en_US\n" +
			"register_needs_allowance=true\n" +
			"max_login_attempts=3\n";
	}
}