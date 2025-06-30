package com.supermartijn642.trashcans.filter;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class FluidFilterManager implements IFilterManager {

    @Override
    public ItemFilter createFilter(ItemStack stack){
        return new FluidFilter(stack);
    }

    @Override
    public ItemFilter readFilter(Tag tag, HolderLookup.Provider provider){
        return new FluidFilter(tag, provider);
    }

    private static class FluidFilter extends ItemFilter {

        FluidStack stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
            if(this.stack != null)
                this.stack = this.stack.copy();
        }

        public FluidFilter(Tag tag, HolderLookup.Provider provider){
            this.stack = FluidStack.CODEC.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst();
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
            return FluidStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.stack).getOrThrow();
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isEmpty();
        }

        private static FluidStack getFluid(ItemStack stack){
            LazyOptional<IFluidHandlerItem> fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            return fluidHandler.filter(h -> h.getTanks() == 1)
                .map(h -> h.getFluidInTank(0))
                .filter(f -> !f.isEmpty())
                .orElse(null);
        }
    }
}
