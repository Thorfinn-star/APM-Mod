package fr.chatgpt.wardenarmor;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WardenInfectionArmor implements ModInitializer {
    public static final String MOD_ID = "warden-infection-armor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        WardenArmorItems.register();
        LOGGER.info("Warden Infection Armor initialized.");
    }
}
