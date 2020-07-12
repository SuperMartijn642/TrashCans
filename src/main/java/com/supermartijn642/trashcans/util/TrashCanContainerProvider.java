package com.supermartijn642.trashcans.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public interface TrashCanContainerProvider {

    Container createContainer(int windowId, PlayerEntity player, BlockPos pos);
}
