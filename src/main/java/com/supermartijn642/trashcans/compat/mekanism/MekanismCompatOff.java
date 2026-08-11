package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class MekanismCompatOff {

    public boolean isInstalled(){
        return false;
    }

    public ItemCapability<?,Void> getGasHandlerCapability(){
        return null;
    }

    public boolean doesItemHaveGasStored(ItemStack stack){
        return false;
    }

    public boolean doesItemHaveGasHandler(ItemStack stack){
        return false;
    }

    public boolean drainGasFromItem(ItemStack stack){
        return false;
    }

    public Object createGasHandler(TrashCanBlockEntity entity){
        return null;
    }

    public boolean isGasStack(Object obj){
        return false;
    }

    public ItemStack getChemicalTankForGasStack(Object gasStack){
        return ItemStack.EMPTY;
    }
}
