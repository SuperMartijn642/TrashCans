package com.supermartijn642.trashcans.filter;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
    public ItemFilter readFilter(Tag tag, HolderLookup.Provider provider){
        return new FluidFilter(tag, provider);
    }

    private static class FluidFilter extends ItemFilter {

        FluidVariant stack;

        public FluidFilter(ItemStack stack){
            this.stack = getFluid(stack);
        }

        public FluidFilter(Tag tag, HolderLookup.Provider provider){
            this.stack = FluidVariant.CODEC.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst();
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
        public Tag write(HolderLookup.Provider provider){
            return FluidVariant.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.stack).getOrThrow();
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
