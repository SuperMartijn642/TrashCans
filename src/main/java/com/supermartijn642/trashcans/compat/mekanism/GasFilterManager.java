package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.IFilterManager;
import com.supermartijn642.trashcans.filter.ItemFilter;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class GasFilterManager implements IFilterManager {

    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new GasFilter(stack);
    }

    @Override
    public ItemFilter readFilter(CompoundTag compound){
        return new GasFilter(compound);
    }

    private static class GasFilter extends ItemFilter {

        GasStack stack;

        public GasFilter(ItemStack stack){
            this.stack = getGas(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public GasFilter(CompoundTag compound){
            this.stack = GasStack.readFromNBT(compound);
        }

        @Override
        public boolean matches(Object stack){
            GasStack fluid = stack instanceof GasStack ? (GasStack)stack :
                stack instanceof ItemStack ? getGas((ItemStack)stack) : null;
            return fluid != null && fluid.isTypeEqual(this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return Compatibility.MEKANISM.getChemicalTankForGasStack(this.stack);
        }

        @Override
        public CompoundTag write(){
            return this.stack.write(new CompoundTag());
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isEmpty();
        }

        private static GasStack getGas(ItemStack stack){
            IGasHandler gasHandler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).orElse(null);
            return gasHandler == null || gasHandler.getTanks() != 1 || gasHandler.getChemicalInTank(0).isEmpty() ? null : gasHandler.getChemicalInTank(0);
        }
    }
}
