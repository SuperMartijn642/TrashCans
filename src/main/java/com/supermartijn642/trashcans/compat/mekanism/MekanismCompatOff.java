package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.filter.ItemFilter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class MekanismCompatOff {

    public boolean isInstalled(){
        return false;
    }

    public Capability<?> getGasHandlerCapability(){
        return null;
    }

    public boolean doesItemHaveGasStored(ItemStack stack){
        return false;
    }

    public boolean drainGasFromItem(ItemStack stack){
        return false;
    }

    public Object getGasHandler(ArrayList<ItemFilter> filters, Supplier<Boolean> whitelist){
        return null;
    }

    public boolean isGasStack(Object obj){
        return false;
    }

    public ItemStack getChemicalTankForGasStack(Object gasStack){
        return ItemStack.EMPTY;
    }
}
