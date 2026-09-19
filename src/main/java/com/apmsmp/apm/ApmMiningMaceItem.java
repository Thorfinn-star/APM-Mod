package com.apmsmp.apm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ApmMiningMaceItem extends Item {
    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    public ApmMiningMaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, ServerLevel level, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean result = super.mineBlock(stack, level, state, pos, miner);
        if (!(miner instanceof ServerPlayer player) || BREAKING.get()) return result;

        BREAKING.set(true);
        try {
            int dx = Math.abs(player.getX() - (pos.getX() + 0.5)) > Math.abs(player.getZ() - (pos.getZ() + 0.5)) ? 0 : 1;
            int dz = dx == 0 ? 1 : 0;
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (a == 0 && b == 0) continue;
                    BlockPos target = dx == 0 ? pos.offset(0, a, b) : pos.offset(a, b, 0);
                    BlockState targetState = level.getBlockState(target);
                    if (targetState.isAir() || targetState.getDestroySpeed(level, target) < 0) continue;
                    if (!stack.isCorrectToolForDrops(targetState)) continue;
                    if (player.gameMode.destroyBlock(target)) {
                        stack.hurtAndBreak(1, player, miner.getEquipmentSlotForItem(stack));
                    }
                }
            }
        } finally {
            BREAKING.set(false);
        }
        return result;
    }
}
