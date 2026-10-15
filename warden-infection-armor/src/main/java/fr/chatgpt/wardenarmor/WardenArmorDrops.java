package fr.chatgpt.wardenarmor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public final class WardenArmorDrops {
    private static final Set<Item> DROPPED_PIECES = new HashSet<>();
    private static final List<Item> ALL_PIECES = List.of(
            WardenArmorItems.HELMET,
            WardenArmorItems.CHESTPLATE,
            WardenArmorItems.LEGGINGS,
            WardenArmorItems.BOOTS
    );

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, net.minecraft.world.damagesource.DamageSource damageSource) -> {
            if (entity.getType() != EntityType.WARDEN || entity.level().isClientSide()) {
                return;
            }
            if (entity.getRandom().nextFloat() >= 0.05F) {
                return;
            }

            List<Item> available = new ArrayList<>(ALL_PIECES);
            available.removeAll(DROPPED_PIECES);
            if (available.isEmpty()) {
                return;
            }

            Item piece = available.get(entity.getRandom().nextInt(available.size()));
            DROPPED_PIECES.add(piece);
            entity.spawnAtLocation(piece);
        });
    }

    public static int reset(CommandSourceStack source) {
        DROPPED_PIECES.clear();
        source.sendSuccess(() -> Component.literal("Warden Armor: progression reset. Les 4 pieces peuvent a nouveau tomber sur les Warden (5% par mort)."), true);
        return 1;
    }

    private WardenArmorDrops() {}
}
