package fr.chatgpt.wardenarmor;

import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvents;

public final class WardenArmorMaterial {
    public static final int BASE_DURABILITY = 37;

    public static final ResourceKey<EquipmentAsset> ASSET_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(WardenInfectionArmor.MOD_ID, "warden_infection"));

    public static final TagKey<Item> REPAIR_TAG =
            TagKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath("minecraft", "repairs_netherite_armor"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 9,
                    ArmorType.LEGGINGS, 7,
                    ArmorType.BOOTS, 4
            ),
            1,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.1F,
            REPAIR_TAG,
            ASSET_KEY
    );

    private WardenArmorMaterial() {}
}
