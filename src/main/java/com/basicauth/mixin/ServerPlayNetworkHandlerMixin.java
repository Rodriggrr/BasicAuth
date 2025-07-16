package com.basicauth.mixin;

import static com.basicauth.func.Allowance.allowed;

import com.basicauth.debug.LoggerStatic;
import com.basicauth.util.helper.MovementState;
import com.basicauth.util.helper.OpsHelper;

import net.minecraft.server.network.ServerPlayerEntity;

// Mixin essentials
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void handleUnlockTeleport(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).player;

        try {
            if (allowed(player)) {
                if (!MovementState.wasTeleported(player)) {
                    MovementState.markTeleported(player);
    
                    // Teleporta para posição atual no servidor
                    player.networkHandler.requestTeleport(
                        player.getX(), player.getY(), player.getZ(),
                        player.getYaw(), player.getPitch()
                    );

                    player.changeGameMode(player.getServer().getDefaultGameMode());
                    if(OpsHelper.isOp(player)) {
                        player.getServer().getCommandManager().sendCommandTree(player);
                    }
                    OpsHelper.playersAwaitingAllowanceReminder(player);
                }
            } else {
                // Se ainda estiver bloqueado, cancela o movimento
                ci.cancel();
            }

        } catch (Exception e) {
            LoggerStatic.error(e.getMessage());
        }
    }
}
