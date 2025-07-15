package com.basicauth.commands;

import static com.basicauth.BasicAuth.REGISTER_NEEDS_ALLOWANCE;
import static com.basicauth.BasicAuth.SHOULD_TRANSLATE_COMMANDS;
import static com.basicauth.util.LocatedAndParsed.commandFromJSON;

import com.basicauth.exception.MalformedParsedString;
import com.basicauth.func.Allowance;
import com.basicauth.func.Login;
import com.basicauth.func.Register;
import com.basicauth.util.Colored;
import com.basicauth.util.LocalizationManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;



public class Commands {
    private static boolean translate;
    private static String locale = "en_US";
    
    public Commands(){
        if(SHOULD_TRANSLATE_COMMANDS){
            locale = LocalizationManager.LOCALE;
        }
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
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
		dispatcher.register(
			CommandManager.literal(commandFromJSON("command.login"))
				.then(CommandManager.argument(commandFromJSON("command.password"), StringArgumentType.string())
					.executes(context -> {
						Login.awaitAdminAllowance(Login.authenticate(context.getSource().getPlayer(), StringArgumentType.getString(context, commandFromJSON("command.password"))), context.getSource().getPlayer());
						return 1;
					})
				)
		);
        dispatcher.register(
            CommandManager.literal(commandFromJSON("command.register"))
                .then(CommandManager.argument(commandFromJSON("command.password"), StringArgumentType.string())
                    .executes(context -> {
                        Register.register(context.getSource().getPlayer(), StringArgumentType.getString(context, commandFromJSON("command.password")));
                        return 1;
                    })
                )
        );
        dispatcher.register(
            CommandManager.literal("basicauth")
                .then(CommandManager.literal(commandFromJSON("command.allow"))
                    .then(CommandManager.argument(commandFromJSON("command.player_name"), StringArgumentType.string())
                        .executes(context -> {
                            Allowance.allow(StringArgumentType.getString(context, commandFromJSON("command.player_name")), context.getSource().getPlayer());
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal(commandFromJSON("command.deny"))
                    .then(CommandManager.argument(commandFromJSON("command.player_name"), StringArgumentType.string())
                        .executes(context -> {
                            Allowance.deny(StringArgumentType.getString(context, commandFromJSON("command.player_name")), context.getSource().getPlayer());
                            MinecraftServer server = context.getSource().getServer();
                            String playerName = StringArgumentType.getString(context, commandFromJSON("command.player_name"));
                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

                            if (player != null) {
                                player.changeGameMode(GameMode.SPECTATOR);
                            }
                            return 1;
                        })
                    )    
                )
        );
    }
}
