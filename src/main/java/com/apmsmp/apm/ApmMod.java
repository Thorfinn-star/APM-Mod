package com.apmsmp.apm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApmMod implements ModInitializer {
    public static final String MOD_ID = "apm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }
    @Override public void onInitialize() {
        ApmBlocks.initialize();
        ApmItems.initialize();
        ApmWorldgen.initialize();
        ApmCommands.initialize();
        ApmZones.initialize();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if ((server.getTickCount() % 20) != 0) return;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) applyArmorEffects(p);
        });
        LOGGER.info("APM Mod 0.1.0 loaded for Minecraft 26.2");
    }
    private static void applyArmorEffects(ServerPlayer p) {
        boolean helmet = p.getItemBySlot(EquipmentSlot.HEAD).is(ApmItems.APM_HELMET);
        boolean chest = p.getItemBySlot(EquipmentSlot.CHEST).is(ApmItems.APM_CHESTPLATE);
        boolean legs = p.getItemBySlot(EquipmentSlot.LEGS).is(ApmItems.APM_LEGGINGS);
        boolean boots = p.getItemBySlot(EquipmentSlot.FEET).is(ApmItems.APM_BOOTS);
        if (helmet) p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false, true));
        if (chest) p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, true, false, true));
        // Balanced movement: only the complete set grants Speed I. No Speed II stacking.
        if (helmet && chest && legs && boots) p.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 0, true, false, true));
    }
}
