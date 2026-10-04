package fr.chatgpt.wardenarmor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.core.component.DataComponents;

public final class WardenArmorItems {
    public static final Item HELMET = register("warden_infection_helmet", ArmorType.HELMET, 11);
    public static final Item CHESTPLATE = register("warden_infection_chestplate", ArmorType.CHESTPLATE, 16);
    public static final Item LEGGINGS = register("warden_infection_leggings", ArmorType.LEGGINGS, 15);
    public static final Item BOOTS = register("warden_infection_boots", ArmorType.BOOTS, 13);

    private static Item register(String name, ArmorType type, int unitDurability) {
        Identifier id = Identifier.fromNamespaceAndPath(WardenInfectionArmor.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item.Properties properties = new Item.Properties()
                .setId(key)
                .stacksTo(1)
                .humanoidArmor(WardenArmorMaterial.INSTANCE, type)
                .durability(unitDurability * WardenArmorMaterial.BASE_DURABILITY)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        return Registry.register(BuiltInRegistries.ITEM, id, new Item(properties));
    }

    public static void register() {}

    private WardenArmorItems() {}
}
