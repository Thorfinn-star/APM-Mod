package fr.chatgpt.wardenarmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WardenInfectionArmor implements ModInitializer {
    public static final String MOD_ID = "warden-infection-armor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        WardenArmorItems.register();
        WardenArmorDrops.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("wardenarmor")
                        .requires(source -> source.hasPermission(2))
                        .then(CommandManager.literal("reset")
                                .executes(context -> WardenArmorDrops.reset(context.getSource())))));
        LOGGER.info("Warden Infection Armor initialized.");
    }
}
