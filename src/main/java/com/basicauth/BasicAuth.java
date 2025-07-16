package com.basicauth;

import com.basicauth.commands.Commands;
import com.basicauth.util.LocalizationManager;
import com.basicauth.util.SimpleConfig;
import com.basicauth.player.*;

import net.fabricmc.api.ModInitializer;

import static com.basicauth.func.Login.announceLogin;
import static com.basicauth.func.Login.logout;
import com.basicauth.func.Allowance;
import com.basicauth.util.TeleportScheduler;
import com.basicauth.util.helper.MovementState;
import com.basicauth.util.helper.OpsHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicAuth implements ModInitializer {
	public static final String MOD_ID = "basicauth";

	static SimpleConfig CONFIG = SimpleConfig.of("config").provider(BasicAuth::provider).request();

	public static final String LOCALE = CONFIG.getOrDefault( "locale", "en_US" );
	public static final boolean REGISTER_NEEDS_ALLOWANCE = CONFIG.getOrDefault( "register_needs_allowance", true );
	public static final boolean SHOULD_TRANSLATE_COMMANDS = CONFIG.getOrDefault( "should_translate_commands", true );
	public static final int MAX_LOGIN_ATTEMPTS = CONFIG.getOrDefault( "max_login_attempts", 3 );

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<String, PlayerModel> players = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		TeleportScheduler.init();
		LocalizationManager.loadFromResource(LOCALE);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {Commands.registerCommands(dispatcher);});


		LOGGER.info(LocalizationManager.get("test", LOCALE));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
			player.sendMessage(announceLogin(player));
			Allowance.setGameMode(player);
			if(OpsHelper.isOp(player)) {
				OpsHelper.ops.put(player.getName().getString(), player);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			logout(player);
			MovementState.reset(player);
			OpsHelper.refreshOps(server);
		});
		
	}

	private static String provider(String filename) {
			// Provide default config content or load from resources if needed
			return 
			"#Localization of the commands and text. Note: the JSON callback will show if there's no translation for that specific language.\n#To contribute, visit: https://github.com/Rodriggrr/BasicAuth/blob/1.21.7/src/main/resources/lang/locales.json\n" +
			"locale=en_US\n" +
			"shoud_translate_commands=true\n" +
			"register_needs_allowance=true\n" +
			"max_login_attempts=3\n";
	}
}