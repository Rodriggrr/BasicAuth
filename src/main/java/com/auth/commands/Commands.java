package com.auth.commands;

import com.auth.exception.MalformedParsedString;
import com.auth.util.Colored;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
				.then(CommandManager.argument("username", StringArgumentType.string())
					.executes(context -> {
						String username = StringArgumentType.getString(context, "username");
						ServerCommandSource source = context.getSource();
						source.sendFeedback(() -> Text.literal("Login command executed for user: " + username), false);
						return 1;
					})
				)
		);
	}

}
