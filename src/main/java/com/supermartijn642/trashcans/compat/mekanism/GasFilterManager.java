package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.IFilterManager;
import com.supermartijn642.trashcans.filter.ItemFilter;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
    public ItemFilter readFilter(CompoundTag compound, HolderLookup.Provider provider){
        return new GasFilter(compound, provider);
    }

    private static class GasFilter extends ItemFilter {

        ChemicalStack stack;

        public GasFilter(ItemStack stack){
            this.stack = getGas(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public GasFilter(CompoundTag compound, HolderLookup.Provider provider){
            this.stack = ChemicalStack.parseOptional(provider, compound);
        }

        @Override
        public boolean matches(Object stack){
            ChemicalStack fluid = stack instanceof ChemicalStack ? (ChemicalStack)stack :
                stack instanceof ItemStack ? getGas((ItemStack)stack) : null;
            return fluid != null && ChemicalStack.isSameChemical(fluid, this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return Compatibility.MEKANISM.getChemicalTankForGasStack(this.stack);
        }

        @Override
        public Tag write(HolderLookup.Provider provider){
            return this.stack.saveOptional(provider);
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isEmpty();
        }

        private static ChemicalStack getGas(ItemStack stack){
            IChemicalHandler gasHandler = stack.getCapability(Capabilities.CHEMICAL.item());
            return gasHandler == null || gasHandler.getChemicalTanks() != 1 || gasHandler.getChemicalInTank(0).isEmpty() ? null : gasHandler.getChemicalInTank(0);
        }
    }
}
