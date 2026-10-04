package fr.chatgpt.wardenarmor;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;

public final class WardenArmorItems {
    public static final Item HELMET = register("warden_infection_helmet", ArmorType.HELMET, 11, helmetEnchantments());
    public static final Item CHESTPLATE = register("warden_infection_chestplate", ArmorType.CHESTPLATE, 16, chestEnchantments());
    public static final Item LEGGINGS = register("warden_infection_leggings", ArmorType.LEGGINGS, 15, leggingsEnchantments());
    public static final Item BOOTS = register("warden_infection_boots", ArmorType.BOOTS, 13, bootsEnchantments());

    private static Item register(String name, ArmorType type, int unitDurability, ItemEnchantments enchants) {
        Identifier id = Identifier.fromNamespaceAndPath(WardenInfectionArmor.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);

        Item.Properties properties = new Item.Properties()
                .setId(key)
                .stacksTo(1)
                .humanoidArmor(WardenArmorMaterial.INSTANCE, type)
                .durability(unitDurability * WardenArmorMaterial.BASE_DURABILITY)
                .enchantable(0)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.ENCHANTMENTS, enchants)
                .setNoCombineRepair();

        return Registry.register(BuiltInRegistries.ITEM, id, new Item(properties));
    }

    private static ItemEnchantments helmetEnchantments() {
        ItemEnchantments.Mutable b = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        b.set(holder(Enchantments.PROTECTION), 4);
        b.set(holder(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(holder(Enchantments.FIRE_PROTECTION), 4);
        b.set(holder(Enchantments.BINDING_CURSE), 1);
        b.set(holder(Enchantments.RESPIRATION), 5);
        b.set(holder(Enchantments.AQUA_AFFINITY), 1);
        return b.toImmutable();
    }

    private static ItemEnchantments chestEnchantments() {
        ItemEnchantments.Mutable b = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        b.set(holder(Enchantments.PROTECTION), 4);
        b.set(holder(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(holder(Enchantments.FIRE_PROTECTION), 4);
        b.set(holder(Enchantments.BINDING_CURSE), 1);
        b.set(holder(Enchantments.THORNS), 4);
        return b.toImmutable();
    }

    private static ItemEnchantments leggingsEnchantments() {
        ItemEnchantments.Mutable b = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        b.set(holder(Enchantments.PROTECTION), 4);
        b.set(holder(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(holder(Enchantments.FIRE_PROTECTION), 4);
        b.set(holder(Enchantments.BINDING_CURSE), 1);
        b.set(holder(Enchantments.SWIFT_SNEAK), 3);
        return b.toImmutable();
    }

    private static ItemEnchantments bootsEnchantments() {
        ItemEnchantments.Mutable b = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        b.set(holder(Enchantments.PROTECTION), 4);
        b.set(holder(Enchantments.PROJECTILE_PROTECTION), 4);
        b.set(holder(Enchantments.FIRE_PROTECTION), 4);
        b.set(holder(Enchantments.BINDING_CURSE), 1);
        b.set(holder(Enchantments.FEATHER_FALLING), 5);
        b.set(holder(Enchantments.DEPTH_STRIDER), 3);
        return b.toImmutable();
    }

    private static Holder<Enchantment> holder(ResourceKey<Enchantment> key) {
        return BuiltInRegistries.ENCHANTMENT.getHolderOrThrow(key);
    }

    public static void register() {}

    private WardenArmorItems() {}
}
