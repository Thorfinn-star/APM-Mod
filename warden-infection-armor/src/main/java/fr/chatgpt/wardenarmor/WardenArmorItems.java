package fr.chatgpt.wardenarmor;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

public final class WardenArmorItems {
    public static final Item HELMET = register("warden_infection_helmet", ArmorType.HELMET, 11, helmetEnchantments());
    public static final Item CHESTPLATE = register("warden_infection_chestplate", ArmorType.CHESTPLATE, 16, chestEnchantments());
    public static final Item LEGGINGS = register("warden_infection_leggings", ArmorType.LEGGINGS, 15, leggingsEnchantments());
    public static final Item BOOTS = register("warden_infection_boots", ArmorType.BOOTS, 13, bootsEnchantments());

    private static Item register(String name, ArmorType type, int unitDurability, ItemEnchantmentsComponent enchants) {
        Identifier id = Identifier.of(WardenInfectionArmor.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(Registries.ITEM.getKey(), id);
        Item.Properties properties = new Item.Properties()
                .setId(key)
                .stacksTo(1)
                .humanoidArmor(WardenArmorMaterial.INSTANCE, type)
                .durability(unitDurability * WardenArmorMaterial.BASE_DURABILITY)
                .enchantable(0)
                .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponentTypes.ENCHANTMENTS, enchants)
                .component(DataComponentTypes.REPAIR_COST, 0);
        return Registry.register(Registries.ITEM, id, new Item(properties));
    }

    private static ItemEnchantmentsComponent helmetEnchantments() {
        ItemEnchantmentsComponent.Builder b = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        b.set(lookup(Enchantments.PROTECTION), 4);
        b.set(lookup(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(lookup(Enchantments.FIRE_PROTECTION), 4);
        b.set(lookup(Enchantments.BINDING_CURSE), 1);
        b.set(lookup(Enchantments.RESPIRATION), 5);
        b.set(lookup(Enchantments.AQUA_AFFINITY), 1);
        return b.build();
    }

    private static ItemEnchantmentsComponent chestEnchantments() {
        ItemEnchantmentsComponent.Builder b = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        b.set(lookup(Enchantments.PROTECTION), 4);
        b.set(lookup(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(lookup(Enchantments.FIRE_PROTECTION), 4);
        b.set(lookup(Enchantments.BINDING_CURSE), 1);
        b.set(lookup(Enchantments.THORNS), 4);
        return b.build();
    }

    private static ItemEnchantmentsComponent leggingsEnchantments() {
        ItemEnchantmentsComponent.Builder b = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        b.set(lookup(Enchantments.PROTECTION), 4);
        b.set(lookup(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(lookup(Enchantments.FIRE_PROTECTION), 4);
        b.set(lookup(Enchantments.BINDING_CURSE), 1);
        b.set(lookup(Enchantments.SWIFT_SNEAK), 3);
        return b.build();
    }

    private static ItemEnchantmentsComponent bootsEnchantments() {
        ItemEnchantmentsComponent.Builder b = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        b.set(lookup(Enchantments.PROTECTION), 4);
        b.set(lookup(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(lookup(Enchantments.FIRE_PROTECTION), 4);
        b.set(lookup(Enchantments.BINDING_CURSE), 1);
        b.set(lookup(Enchantments.FEATHER_FALLING), 5);
        b.set(lookup(Enchantments.DEPTH_STRIDER), 3);
        return b.build();
    }

    private static net.minecraft.registry.entry.RegistryEntry<net.minecraft.enchantment.Enchantment> lookup(
            net.minecraft.registry.RegistryKey<net.minecraft.enchantment.Enchantment> key) {
        return Registries.ENCHANTMENT.getEntry(key).orElseThrow();
    }

    public static void register() {}
    private WardenArmorItems() {}
}
