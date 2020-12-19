package com.supermartijn642.trashcans.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public interface IFilterManager {

    ItemFilter createFilter(ItemStack stack);

    ItemFilter readFilter(CompoundNBT compound);

}
