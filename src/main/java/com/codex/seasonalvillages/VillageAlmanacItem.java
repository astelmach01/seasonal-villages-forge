package com.codex.seasonalvillages;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class VillageAlmanacItem extends Item {
    public VillageAlmanacItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            VillageSnapshot snapshot = SeasonalVillageManager.snapshot(serverLevel, player.blockPosition());
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(snapshot.summary()), false);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
