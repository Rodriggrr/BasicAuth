package com.basicauth.mixin;

import static com.basicauth.func.Allowance.allowed;
import static com.basicauth.util.LocatedAndParsed.parseFromJSON;

import com.basicauth.debug.LoggerStatic;
import com.basicauth.exception.Wrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void onExecute(com.mojang.brigadier.ParseResults<ServerCommandSource> parseResult, String command, CallbackInfo ci) {
        if (parseResult.getContext().getSource().getEntity() instanceof ServerPlayerEntity player) {
            String firstCommand = command.toLowerCase().split(" ")[0];
            
            try {
                if (!allowed(player) && !firstCommand.equals("login") && !firstCommand.equals("register")) {
                    parseResult.getContext().getSource().sendFeedback(Wrapper.wrap(() -> parseFromJSON("admin.no_login_op")), false);
                    ci.cancel(); // cancela o comando
                }
            } catch (Exception e) {
                LoggerStatic.error("Error while checking allowance for player " + player.getGameProfile().getName() + ": " + e.getMessage());
            }
        }
    }
}