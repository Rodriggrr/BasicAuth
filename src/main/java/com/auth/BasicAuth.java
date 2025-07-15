package com.auth;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.auth.util.LocalizationManager;
import com.auth.util.SimpleConfig;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
import com.auth.commands.Commands;
import static com.auth.util.LocatedAndParsed.parseFromJSON;
import static com.auth.func.Login.announceLogin;
import static com.auth.func.Login.logout;

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

	static SimpleConfig CONFIG = SimpleConfig.of("config").provider(BasicAuth::provider).request();

	public static final String LOCALE = CONFIG.getOrDefault( "locale", "en_US" );
	public static final boolean REGISTER_NEEDS_ALLOWANCE = CONFIG.getOrDefault( "register_needs_allowance", true );
	public static final int MAX_LOGIN_ATTEMPTS = CONFIG.getOrDefault( "max_login_attempts", 3 );

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LocalizationManager.loadFromResource(LOCALE);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {Commands.registerCommands(dispatcher);});


		LOGGER.info(LocalizationManager.get("test", LOCALE));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
			player.sendMessage(announceLogin(player));
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			logout(player);
		});
		
	}

	private static String provider(String filename) {
			// Provide default config content or load from resources if needed
			return 
			"locale=en_US\n" +
			"register_needs_allowance=true\n" +
			"max_login_attempts=3\n";
	}
}