package com.supermartijn642.trashcans.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public interface IFilterManager {

    ItemFilter createFilter(ItemStack stack);

    ItemFilter readFilter(CompoundTag compound);

}
