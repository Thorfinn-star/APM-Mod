package com.apmsmp.apm.mixin;

import com.apmsmp.apm.AdvancementDatabaseLogger;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow private ServerPlayer player;

    @Inject(method = "award", at = @At("RETURN"))
    private void apm$recordCompletedAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        PlayerAdvancements self = (PlayerAdvancements) (Object) this;
        if (!self.getOrStartProgress(advancement).isDone()) return;

        AdvancementDatabaseLogger.recordLive(
                player.getUUID().toString(),
                player.getGameProfile().name(),
                advancement.id().toString()
        );
    }
}
