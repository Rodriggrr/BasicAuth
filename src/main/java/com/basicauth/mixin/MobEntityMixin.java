package com.basicauth.mixin;

import static com.basicauth.func.Allowance.allowed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;

import net.minecraft.entity.LivingEntity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void ifCanTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof ServerPlayerEntity player) {
            try {
                if (!allowed(player)) {
                    ci.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
