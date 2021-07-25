package com.supermartijn642.trashcans.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public interface TrashCanContainerProvider {

    AbstractContainerMenu createContainer(int windowId, Player player, BlockPos pos);
}
