package com.apmsmp.apm;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ApmArmorMaterial {
    public static final int BASE_DURABILITY = 40;
    public static final TagKey<Item> REPAIRS_APM = TagKey.create(BuiltInRegistries.ITEM.key(), ApmMod.id("repairs_apm"));
    public static final ResourceKey<EquipmentAsset> ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, ApmMod.id("apm"));
    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
        BASE_DURABILITY,
        Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 8, ArmorType.LEGGINGS, 6, ArmorType.BOOTS, 3),
        18, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5F, 0.12F, REPAIRS_APM, ASSET
    );
    private ApmArmorMaterial() {}
}
