package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created 11/08/2026 by SuperMartijn642
 */
public class TrashCanResourceHandlers {

    public static boolean doesItemContainFluid(ItemStack stack){
        ResourceHandler<FluidResource> handler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
        if(handler != null){
            for(int i = 0; i < handler.size(); i++){
                if(!handler.getResource(i).isEmpty() && handler.getAmountAsInt(i) > 0)
                    return true;
            }
        }
        return Compatibility.MEKANISM.doesItemHaveGasStored(stack);
    }

    public static boolean doesItemHaveFluidHandler(ItemStack stack){
        return stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack)) != null
            || Compatibility.MEKANISM.doesItemHaveGasHandler(stack);
    }

    public static boolean doesItemContainEnergy(ItemStack stack){
        EnergyHandler handler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return handler != null && handler.getAmountAsInt() > 0;
    }

    public static boolean doesItemHaveEnergyHandler(ItemStack stack){
        return stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack)) != null;
    }

    public static ResourceHandler<ItemResource> createItemHandler(TrashCanBlockEntity entity){
        return new TrashCanItemHandler(entity);
    }

    public static ResourceHandler<FluidResource> createFluidHandler(TrashCanBlockEntity entity){
        return new TrashCanFluidHandler(entity);
    }

    public static EnergyHandler createEnergyHandler(TrashCanBlockEntity entity){
        return new TrashCanEnergyHandler(entity);
    }

    private static class TrashCanItemHandler implements ResourceHandler<ItemResource> {

        private final TrashCanBlockEntity entity;

        private TrashCanItemHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public int size(){
            return 1;
        }

        @Override
        public ItemResource getResource(int index){
            return ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index){
            return 0;
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource){
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isValid(int index, ItemResource resource){
            TransferPreconditions.checkNonEmpty(resource);
            return this.entity.matchesItemFilter(resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction){
            return this.insert(resource, amount, transaction);
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction){
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            return this.isValid(0, resource) ? amount : 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction){
            return 0;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction){
            return 0;
        }
    }

    private static class TrashCanFluidHandler implements ResourceHandler<FluidResource> {

        private final TrashCanBlockEntity entity;

        private TrashCanFluidHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public int size(){
            return 1;
        }

        @Override
        public FluidResource getResource(int index){
            return FluidResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index){
            return 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource){
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isValid(int index, FluidResource resource){
            return this.entity.matchesFluidFilter(resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction){
            return this.insert(resource, amount, transaction);
        }

        @Override
        public int insert(FluidResource resource, int amount, TransactionContext transaction){
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            return this.isValid(0, resource) ? amount : 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction){
            return 0;
        }

        @Override
        public int extract(FluidResource resource, int amount, TransactionContext transaction){
            return 0;
        }
    }

    private static class TrashCanEnergyHandler implements EnergyHandler {

        private final TrashCanBlockEntity entity;

        private TrashCanEnergyHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public long getAmountAsLong(){
            return 0;
        }

        @Override
        public long getCapacityAsLong(){
            return Long.MAX_VALUE;
        }

        @Override
        public int insert(int amount, TransactionContext transaction){
            TransferPreconditions.checkNonNegative(amount);
            return Math.min(amount, this.entity.getMaxEnergyInsertion());
        }

        @Override
        public int extract(int amount, TransactionContext transaction){
            return 0;
        }
    }
}
