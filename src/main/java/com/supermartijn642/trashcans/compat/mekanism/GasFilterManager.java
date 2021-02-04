package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.IFilterManager;
import com.supermartijn642.trashcans.filter.ItemFilter;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class GasFilterManager implements IFilterManager {
    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new GasFilter(stack);
    }

    @Override
    public ItemFilter readFilter(NBTTagCompound compound){
        return new GasFilter(compound);
    }

    private static class GasFilter extends ItemFilter {
        GasStack stack;

        public GasFilter(ItemStack stack){
            this.stack = getGas(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public GasFilter(NBTTagCompound compound){
            this.stack = GasStack.readFromNBT(compound);
        }

        @Override
        public boolean matches(Object stack){
            GasStack fluid = stack instanceof GasStack ? (GasStack)stack :
                stack instanceof ItemStack ? getGas((ItemStack)stack) : null;
            return fluid != null && fluid.isGasEqual(this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return Compatibility.MEKANISM.getChemicalTankForGasStack(this.stack);
        }

        @Override
        public NBTTagCompound write(){
            return this.stack.write(new NBTTagCompound());
        }

        @Override
        public boolean isValid(){
            return this.stack != null && this.stack.amount > 0;
        }

        private static GasStack getGas(ItemStack stack){
            GasStack gasStack = stack.getItem() instanceof IGasItem ? ((IGasItem)stack.getItem()).getGas(stack) : null;
            return gasStack != null && gasStack.amount > 0 ? gasStack : null;
        }
    }
}
