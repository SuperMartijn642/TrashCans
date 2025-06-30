package com.supermartijn642.trashcans.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    public ItemFilter readFilter(ValueInput input){
        return new FluidFilter(input);
    }

    private static class FluidFilter extends ItemFilter {

        FluidStack stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public FluidFilter(ValueInput input){
            this.stack = input.read("stack", FluidStack.CODEC).orElse(FluidStack.EMPTY);
        }

        @Override
        public boolean matches(Object stack){
            FluidStack fluid = stack instanceof FluidStack ? (FluidStack)stack :
                stack instanceof ItemStack ? getFluid((ItemStack)stack) : null;
            return fluid != null && FluidStack.isSameFluidSameComponents(fluid, this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return new ItemStack(this.stack.getFluid().getBucket());
        }

        @Override
        public void write(ValueOutput output){
            output.store("stack", FluidStack.CODEC, this.stack);
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
