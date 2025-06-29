package com.supermartijn642.trashcans.filter;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

        FluidVariant stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
        }

        public FluidFilter(ValueInput input){
            this.stack = input.read("stack", FluidVariant.CODEC).orElse(FluidVariant.blank());
        }

        @Override
        public boolean matches(Object stack){
            FluidVariant fluid = stack instanceof FluidVariant ? (FluidVariant)stack :
                stack instanceof ItemStack ? getFluid((ItemStack)stack) : null;
            return fluid != null && fluid.equals(this.stack);
        }

        @Override
        public ItemStack getRepresentingItem(){
            return new ItemStack(this.stack.getFluid().getBucket());
        }

        @Override
        public void write(ValueOutput output){
            output.store("stack", FluidVariant.CODEC, this.stack);
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isBlank();
        }

        private static FluidVariant getFluid(ItemStack stack){
            Storage<FluidVariant> fluidHandler = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
            if(fluidHandler != null){
                for(StorageView<FluidVariant> slot : fluidHandler){
                    if(!slot.isResourceBlank())
                        return slot.getResource();
                }
            }
            return FluidVariant.blank();
        }
    }
}
