package com.basicauth.mixin;

import static com.basicauth.func.Allowance.allowed;
import static com.basicauth.util.LocatedAndParsed.parseFromJSON;

import com.basicauth.debug.LoggerStatic;
import com.basicauth.util.helper.TeleportEnforcer;


import net.minecraft.server.network.ServerPlayerEntity;

// Mixin essentials
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void handleUnlockTeleport(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;

        try {
            if (!allowed(player)) {
                ci.cancel(); // cancela o movimento
                TeleportEnforcer.start(player); // inicia re-teleporte a cada 1 segundo
            } else {
                TeleportEnforcer.stop(player);
            }
        } catch (Exception e) {
            LoggerStatic.error(e.getMessage());
        }
    }

    @Inject(method = "onSpectatorTeleport", at = @At("HEAD"), cancellable = true)
    private void blockSpectatorTeleport(SpectatorTeleportC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        try {
            if (!allowed(player)) {
                player.sendMessage(parseFromJSON("admin.no_spectator_teleport"), false);
                ci.cancel(); // Cancela o envio do pacote
            }
        } catch (Exception e) {
            LoggerStatic.error("Error while blocking chunk packets: " + e.getMessage());
        }
    }
}
