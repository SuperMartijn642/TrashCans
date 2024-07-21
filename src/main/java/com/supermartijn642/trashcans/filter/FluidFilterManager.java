package com.supermartijn642.trashcans.filter;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class FluidFilterManager implements IFilterManager {

    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new FluidFilter(stack);
    }

    @Override
    public ItemFilter readFilter(CompoundTag tag, HolderLookup.Provider provider){
        return new FluidFilter(tag, provider);
    }

    private static class FluidFilter extends ItemFilter {

        FluidStack stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public FluidFilter(CompoundTag compound, HolderLookup.Provider provider){
            this.stack = FluidStack.loadFluidStackFromNBT(compound);
        }

        @Override
        public boolean matches(Object stack){
            FluidStack fluid = stack instanceof FluidStack ? (FluidStack)stack :
                stack instanceof ItemStack ? getFluid((ItemStack)stack) : null;
            return fluid != null && fluid.isFluidEqual(this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return new ItemStack(this.stack.getFluid().getBucket());
        }

        @Override
        public Tag write(HolderLookup.Provider provider){
            return this.stack.writeToNBT(new CompoundTag());
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isEmpty();
        }

        private static FluidStack getFluid(ItemStack stack){
//            IFluidHandler fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null); // TODO
//            return fluidHandler == null || fluidHandler.getTanks() != 1 || fluidHandler.getFluidInTank(0).isEmpty() ? null : fluidHandler.getFluidInTank(0);
            return null;
        }
    }
}
