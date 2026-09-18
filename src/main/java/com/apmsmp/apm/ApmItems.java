package com.apmsmp.apm;

import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

public final class ApmItems {
    public static final TagKey<Block> INCORRECT_FOR_APM_TOOL = TagKey.create(Registries.BLOCK, ApmMod.id("incorrect_for_apm_tool"));
    public static final ToolMaterial APM_TOOL = new ToolMaterial(INCORRECT_FOR_APM_TOOL, 2500, 10.0F, 4.5F, 18, ApmArmorMaterial.REPAIRS_APM);

    public static final Item RAW_APM = simple("raw_apm");
    public static final Item APM_INGOT = simple("apm_ingot");
    public static final Item APM_SWORD = register("apm_sword", Item::new, new Item.Properties().sword(APM_TOOL, 3.0F, -2.4F));
    public static final Item APM_PICKAXE = register("apm_pickaxe", Item::new, new Item.Properties().pickaxe(APM_TOOL, 1.0F, -2.8F));
    public static final Item APM_AXE = register("apm_axe", p -> new AxeItem(APM_TOOL, 5.0F, -3.0F, p), new Item.Properties());
    public static final Item APM_SHOVEL = register("apm_shovel", p -> new ShovelItem(APM_TOOL, 1.5F, -3.0F, p), new Item.Properties());
    public static final Item APM_HOE = register("apm_hoe", p -> new HoeItem(APM_TOOL, -4.0F, 0.0F, p), new Item.Properties());

    public static final Item APM_HELMET = armor("apm_helmet", ArmorType.HELMET);
    public static final Item APM_CHESTPLATE = armor("apm_chestplate", ArmorType.CHESTPLATE);
    public static final Item APM_LEGGINGS = armor("apm_leggings", ArmorType.LEGGINGS);
    public static final Item APM_BOOTS = armor("apm_boots", ArmorType.BOOTS);

    private static Item simple(String name) { return register(name, Item::new, new Item.Properties()); }
    private static Item armor(String name, ArmorType type) {
        return register(name, Item::new, new Item.Properties().humanoidArmor(ApmArmorMaterial.INSTANCE, type).durability(type.getDurability(ApmArmorMaterial.BASE_DURABILITY)).enchantable(18));
    }
    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ApmMod.id(name));
        Item item = factory.apply(props.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(out -> { out.accept(RAW_APM); out.accept(APM_INGOT); });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(out -> { out.accept(APM_PICKAXE); out.accept(APM_AXE); out.accept(APM_SHOVEL); out.accept(APM_HOE); });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(out -> { out.accept(APM_SWORD); out.accept(APM_HELMET); out.accept(APM_CHESTPLATE); out.accept(APM_LEGGINGS); out.accept(APM_BOOTS); });
    }
    private ApmItems() {}
}
