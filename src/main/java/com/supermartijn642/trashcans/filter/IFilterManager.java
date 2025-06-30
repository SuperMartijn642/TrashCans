package com.supermartijn642.trashcans.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public interface IFilterManager {

    ItemFilter createFilter(ItemStack stack);

    ItemFilter readFilter(ValueInput input);

}
