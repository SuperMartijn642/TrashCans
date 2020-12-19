package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.filter.IFilterManager;
import com.supermartijn642.trashcans.filter.ItemFilter;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import mekanism.common.MekanismBlocks;
import mekanism.common.capabilities.Capabilities;
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
            ItemStack stack = new ItemStack(MekanismBlocks.GasTank);
            IGasHandler handler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null);
            if(handler.getTankInfo().length > 0){
                GasStack gas = this.stack.copy();
                gas.amount = handler.getTankInfo()[0].getMaxGas();
                handler.receiveGas(null, gas, true);
            }
            return stack;
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
            IGasHandler gasHandler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null);
            return gasHandler == null || gasHandler.getTankInfo().length != 1 || gasHandler.getTankInfo()[0].getGas() == null || gasHandler.getTankInfo()[0].getStored() == 0 ? null : gasHandler.getTankInfo()[0].getGas();
        }
    }
}
