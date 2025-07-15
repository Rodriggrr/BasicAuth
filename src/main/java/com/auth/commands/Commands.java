package com.auth.commands;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static com.auth.util.LocatedAndParsed.commandFromJSON;
import com.auth.func.Login;
import com.auth.func.Register;
import com.auth.func.Allowance;
import com.auth.util.LocalizationManager;
import static com.auth.BasicAuth.SHOULD_TRANSLATE_COMMANDS;
import static com.auth.BasicAuth.REGISTER_NEEDS_ALLOWANCE;


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
                            return 1;
                        })
                    )    
                )
        );
    }
}
