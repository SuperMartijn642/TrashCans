package com.supermartijn642.trashcans.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class FluidFilterManager implements IFilterManager {

    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new FluidFilter(stack);
    }

    @Override
    public ItemFilter readFilter(CompoundTag compound){
        return new FluidFilter(compound);
    }

    private static class FluidFilter extends ItemFilter {

        FluidStack stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public FluidFilter(CompoundTag compound){
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
        public CompoundTag write(){
            return this.stack.writeToNBT(new CompoundTag());
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isEmpty();
        }

        private static FluidStack getFluid(ItemStack stack){
            IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            return fluidHandler == null || fluidHandler.getTanks() != 1 || fluidHandler.getFluidInTank(0).isEmpty() ? null : fluidHandler.getFluidInTank(0);
        }
    }
}
