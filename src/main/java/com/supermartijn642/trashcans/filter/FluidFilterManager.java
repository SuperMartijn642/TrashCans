package com.supermartijn642.trashcans.filter;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

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

    @SuppressWarnings("UnstableApiUsage")
    private static class FluidFilter extends ItemFilter {

        FluidVariant stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
        }

        public FluidFilter(CompoundTag compound){
            this.stack = FluidVariant.fromNbt(compound);
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
        public CompoundTag write(){
            return this.stack.toNbt();
        }

        @Override
        public boolean isValid(){
            return this.stack != null && !this.stack.isBlank();
        }

        private static FluidVariant getFluid(ItemStack stack){
            Storage<FluidVariant> fluidHandler = FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack));
            if(fluidHandler != null){
                try(Transaction transaction = Transaction.openOuter()){
                    for(StorageView<FluidVariant> slot : fluidHandler.iterable(transaction)){
                        if(!slot.isResourceBlank())
                            return slot.getResource();
                    }
                }
            }
            return FluidVariant.blank();
        }
    }
}
