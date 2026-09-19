package com.apmsmp.apm;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public final class ApmCommands {
    private static final int MAX_RADIUS = 256;
    private static final int MIN_Y = -64;
    private static final int MAX_Y = -45;

    private ApmCommands() {}

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("apmlocate")
                .executes(context -> locate(context.getSource().getPlayerOrException())));
            dispatcher.register(Commands.literal("colisee")
                .executes(context -> colisee(context.getSource().getPlayerOrException()))));
    }

    private static int colisee(ServerPlayer player) {
        ServerLevel level = player.level().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (level == null) return 0;
        player.teleportTo(level, -20.5, 98.0, -299.5, java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        player.sendSystemMessage(Component.literal("Téléporté au Colisée."));
        return 1;
    }

    private static int locate(ServerPlayer player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.sendSystemMessage(Component.literal("/apmlocate est une commande de diagnostic : passe en créatif ou spectateur pour l’utiliser."));
            return 0;
        }

        ServerLevel level = player.level();
        BlockPos origin = player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        long bestDistance = Long.MAX_VALUE;
        int minX = origin.getX() - MAX_RADIUS;
        int maxX = origin.getX() + MAX_RADIUS;
        int minZ = origin.getZ() - MAX_RADIUS;
        int maxZ = origin.getZ() + MAX_RADIUS;

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) continue;
                int startX = Math.max(minX, chunkX << 4);
                int endX = Math.min(maxX, (chunkX << 4) + 15);
                int startZ = Math.max(minZ, chunkZ << 4);
                int endZ = Math.min(maxZ, (chunkZ << 4) + 15);
                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; z <= endZ; z++) {
                        for (int y = MIN_Y; y <= MAX_Y; y++) {
                            cursor.set(x, y, z);
                            if (!level.getBlockState(cursor).is(ApmBlocks.APM_ORE)) continue;
                            long dx = x - origin.getX();
                            long dz = z - origin.getZ();
                            long distance = dx * dx + dz * dz;
                            if (distance < bestDistance) {
                                bestDistance = distance;
                                best = cursor.immutable();
                            }
                        }
                    }
                }
            }
        }

        if (best == null) {
            player.sendSystemMessage(Component.literal("Aucun minerai d’APM trouvé dans les chunks chargés à moins de " + MAX_RADIUS + " blocs. Va dans des chunks neufs puis réessaie /apmlocate."));
            return 0;
        }
        BlockPos found = best;
        int horizontalDistance = (int)Math.sqrt(bestDistance);
        player.sendSystemMessage(Component.literal("Minerai d’APM trouvé : X " + found.getX() + " Y " + found.getY() + " Z " + found.getZ() + " (≈ " + horizontalDistance + " blocs)"));
        player.sendSystemMessage(Component.literal("Téléportation : /tp " + found.getX() + " " + (found.getY() + 1) + " " + found.getZ()));
        return 1;
    }
}
