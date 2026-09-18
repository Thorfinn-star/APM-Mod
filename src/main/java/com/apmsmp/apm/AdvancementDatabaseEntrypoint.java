package com.apmsmp.apm;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class AdvancementDatabaseEntrypoint implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> AdvancementDatabaseLogger.start());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> AdvancementDatabaseLogger.stop());
    }
}
