package fr.chatgpt.wardenarmor;

import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorType;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssets;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class WardenArmorMaterial {
    public static final int BASE_DURABILITY = 37;
    public static final RegistryKey<EquipmentAsset> ASSET_KEY =
            RegistryKey.of(EquipmentAssets.ROOT_ID, Identifier.of(WardenInfectionArmor.MOD_ID, "warden_infection"));
    public static final TagKey<Item> REPAIR_TAG =
            TagKey.of(Registries.ITEM.getKey(), Identifier.of("minecraft", "repairs_netherite_armor"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 9,
                    ArmorType.LEGGINGS, 7,
                    ArmorType.BOOTS, 4
            ),
            0,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.1F,
            REPAIR_TAG,
            ASSET_KEY
    );
    private WardenArmorMaterial() {}
}
