package com.apmsmp.apm.mixin;

import com.apmsmp.apm.ApmItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentCompatibilityMixin {
    private ItemStack apm$vanillaEquivalent(ItemStack stack) {
        if (stack.is(ApmItems.APM_HELMET)) return new ItemStack(Items.DIAMOND_HELMET);
        if (stack.is(ApmItems.APM_CHESTPLATE)) return new ItemStack(Items.DIAMOND_CHESTPLATE);
        if (stack.is(ApmItems.APM_LEGGINGS)) return new ItemStack(Items.DIAMOND_LEGGINGS);
        if (stack.is(ApmItems.APM_BOOTS)) return new ItemStack(Items.DIAMOND_BOOTS);
        if (stack.is(ApmItems.APM_SWORD)) return new ItemStack(Items.DIAMOND_SWORD);
        if (stack.is(ApmItems.APM_PICKAXE)) return new ItemStack(Items.DIAMOND_PICKAXE);
        if (stack.is(ApmItems.APM_AXE)) return new ItemStack(Items.DIAMOND_AXE);
        if (stack.is(ApmItems.APM_SHOVEL)) return new ItemStack(Items.DIAMOND_SHOVEL);
        if (stack.is(ApmItems.APM_HOE)) return new ItemStack(Items.DIAMOND_HOE);
        return null;
    }

    @Inject(method = "isSupportedItem", at = @At("HEAD"), cancellable = true)
    private void apm$matchDiamondSupportedItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack vanilla = apm$vanillaEquivalent(stack);
        if (vanilla != null) cir.setReturnValue(((Enchantment)(Object)this).isSupportedItem(vanilla));
    }

    @Inject(method = "isPrimaryItem", at = @At("HEAD"), cancellable = true)
    private void apm$matchDiamondPrimaryItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack vanilla = apm$vanillaEquivalent(stack);
        if (vanilla != null) cir.setReturnValue(((Enchantment)(Object)this).isPrimaryItem(vanilla));
    }
}
