package com.supermartijn642.trashcans.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

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

        FluidResource fluid;

        public FluidFilter(ItemStack stack){
            this.fluid = getFluid(stack);
        }

        public FluidFilter(ValueInput input){
            this.fluid = input.read("fluid", FluidResource.CODEC).orElse(FluidResource.EMPTY);
        }

        @Override
        public boolean matches(Object stack){
            if(this.fluid.isEmpty())
                return false;
            if(stack instanceof FluidStack) return this.fluid.matches((FluidStack)stack);
            FluidResource fluid = stack instanceof FluidResource ? (FluidResource)stack :
                stack instanceof ItemStack ? getFluid((ItemStack)stack) : FluidResource.EMPTY;
            return this.fluid.equals(fluid);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return new ItemStack(this.fluid.getFluid().getBucket());
        }

        @Override
        public void write(ValueOutput output){
            output.store("fluid", FluidResource.CODEC, this.fluid);
        }

        @Override
        public boolean isValid(){
            return this.fluid != null && !this.fluid.isEmpty();
        }

        private static FluidResource getFluid(ItemStack stack){
            ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
            if(handler == null) return FluidResource.EMPTY;
            for(int i = 0; i < handler.size(); i++){
                FluidResource resource = handler.getResource(i);
                if(!resource.isEmpty())
                    return resource;
            }
            return FluidResource.EMPTY;
        }
    }
}
