package com.apmsmp.apm;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ApmWorldgen {
    public static final ResourceKey<PlacedFeature> APM_ORE_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, ApmMod.id("apm_ore"));
    public static void initialize() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, APM_ORE_PLACED);
    }
    private ApmWorldgen() {}
}
