package com.supermartijn642.trashcans.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class FluidFilterManager implements IFilterManager {
    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new FluidFilter(stack);
    }

    @Override
    public ItemFilter readFilter(NBTTagCompound compound){
        return new FluidFilter(compound);
    }

    private static class FluidFilter extends ItemFilter {
        FluidStack stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public FluidFilter(NBTTagCompound compound){
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
            return FluidUtil.getFilledBucket(this.stack);
        }

        @Override
        public NBTTagCompound write(){
            return this.stack.writeToNBT(new NBTTagCompound());
        }

        @Override
        public boolean isValid(){
            return this.stack != null && this.stack.amount > 0;
        }

        public static FluidStack getFluid(ItemStack stack){
            if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                return null;
            IFluidHandler fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            return fluidHandler == null || fluidHandler.getTankProperties() == null || fluidHandler.getTankProperties().length != 1 ||
                fluidHandler.getTankProperties()[0].getContents() == null || fluidHandler.getTankProperties()[0].getContents().amount == 0
                ? null : fluidHandler.getTankProperties()[0].getContents();
        }
    }
}
