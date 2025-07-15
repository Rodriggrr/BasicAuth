package com.auth.commands;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import com.auth.func.Login;
import com.auth.func.Register;

public class Commands {
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
			CommandManager.literal("login")
				.then(CommandManager.argument("password", StringArgumentType.string())
					.executes(context -> {
						Login.authenticate(context.getSource().getPlayer(), StringArgumentType.getString(context, "password"));
						return 1;
					})
				)
		);
        dispatcher.register(
            CommandManager.literal("register")
                .then(CommandManager.argument("password", StringArgumentType.string())
                    .executes(context -> {
                        Register.register(context.getSource().getPlayer(), StringArgumentType.getString(context, "password"));
                        return 1;
                    })
                )
        );
    }
}
