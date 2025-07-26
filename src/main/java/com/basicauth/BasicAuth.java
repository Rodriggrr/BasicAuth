package com.basicauth;

import com.basicauth.util.LocalizationManager;
import com.basicauth.util.SimpleConfig;
import com.basicauth.util.helper.StopPlayerActions;
import com.basicauth.player.*;
import com.basicauth.util.EventHandler;

import net.fabricmc.api.ModInitializer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicAuth implements ModInitializer {
	public static final String MOD_ID = "basicauth";
	public static final PlayerDataHandler playerDataHandler = new PlayerDataHandler();
	

	static SimpleConfig CONFIG = SimpleConfig.of("config").provider(BasicAuth::provider).request();

	public static final String LOCALE = CONFIG.getOrDefault("locale", "en_US");
	public static final boolean REGISTER_NEEDS_ALLOWANCE = CONFIG.getOrDefault("register_needs_allowance", true);
	public static final boolean SHOULD_TRANSLATE_COMMANDS = CONFIG.getOrDefault("should_translate_commands", true);
	public static final int MAX_LOGIN_ATTEMPTS = CONFIG.getOrDefault("max_login_attempts", 3);

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<String, PlayerModel> players = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		StopPlayerActions.init();
		LocalizationManager.loadFromResource(LOCALE);

		EventHandler.init();
	}

	private static String provider(String filename) {
		// Provide default config content or load from resources if needed
		return "#Localization of the commands and text. Note: the JSON callback will show if there's no translation for that specific language.\n#To contribute, visit: https://github.com/Rodriggrr/BasicAuth/blob/1.21.7/src/main/resources/lang/locales.json\n"
				+
				"locale=en_US\n" +
				"shoud_translate_commands=true\n" +
				"register_needs_allowance=true\n" +
				"max_login_attempts=3\n";
	}
}