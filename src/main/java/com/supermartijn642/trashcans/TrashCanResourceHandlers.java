package com.supermartijn642.trashcans;

import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

import java.util.Iterator;

/**
 * Created 11/08/2026 by SuperMartijn642
 */
@SuppressWarnings("UnstableApiUsage")
public class TrashCanResourceHandlers {

    public static boolean doesItemContainFluid(ItemStack stack){
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack));
        if(storage == null || !storage.supportsExtraction())
            return false;
        try(Transaction transaction = Transaction.openOuter()){
            for(StorageView<FluidVariant> slot : storage.iterable(transaction)){
                if(!slot.isResourceBlank())
                    return true;
            }
        }
        return false;
    }

    public static boolean doesItemHaveFluidHandler(ItemStack stack){
        return FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack)) != null;
    }

    public static boolean doesItemContainEnergy(ItemStack stack){
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, null);
        return storage != null && storage.supportsExtraction() && storage.getAmount() > 0;
    }

    public static boolean doesItemHaveEnergyHandler(ItemStack stack){
        return EnergyStorage.ITEM.find(stack, null) != null;
    }

    public static Storage<ItemVariant> createItemHandler(TrashCanBlockEntity entity){
        return new TrashCanItemHandler(entity);
    }

    public static Storage<FluidVariant> createFluidHandler(TrashCanBlockEntity entity){
        return new TrashCanFluidHandler(entity);
    }

    public static EnergyStorage createEnergyHandler(TrashCanBlockEntity entity){
        return new TrashCanEnergyHandler(entity);
    }

    private static class TrashCanItemHandler extends SnapshotParticipant<TrashCanItemHandler.SnapShot> implements Storage<ItemVariant> {

        private final TrashCanBlockEntity entity;

        private TrashCanItemHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction){
            return ObjectIterators.emptyIterator();
        }

        @Override
        public long insert(ItemVariant resource, long amount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, amount);
            if(!this.entity.matchesItemFilter(resource))
                return 0;
            this.updateSnapshots(transaction);
            this.entity.pushDeletedItem(resource.toStack((int)Math.min(amount, Integer.MAX_VALUE))); // This may discard some items, but if the amount is greater than what an int can hold, it shouldn't matter anyway
            return amount;
        }

        @Override
        public boolean supportsExtraction(){
            return false;
        }

        @Override
        public long extract(ItemVariant resource, long amount, TransactionContext transaction){
            return 0;
        }

        @Override
        public long getVersion(){
            return 0;
        }

        @Override
        protected SnapShot createSnapshot(){
            return new SnapShot(this.entity.getDeletedItems().toArray(new ItemStack[0]));
        }

        @Override
        protected void readSnapshot(SnapShot snapshot){
            assert snapshot != null;
            this.entity.restoreDeletedItems(snapshot.deletedItems);
        }

        private record SnapShot(ItemStack[] deletedItems){
        }
    }

    private static class TrashCanFluidHandler implements Storage<FluidVariant> {

        private final TrashCanBlockEntity entity;

        private TrashCanFluidHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public Iterator<? extends StorageView<FluidVariant>> iterator(TransactionContext transaction){
            return ObjectIterators.emptyIterator();
        }

        @Override
        public long insert(FluidVariant resource, long amount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, amount);
            return this.entity.matchesFluidFilter(resource) ? amount : 0;
        }

        @Override
        public boolean supportsExtraction(){
            return false;
        }

        @Override
        public long extract(FluidVariant resource, long amount, TransactionContext transaction){
            return 0;
        }

        @Override
        public long getVersion(){
            return 0;
        }
    }

    private static class TrashCanEnergyHandler implements EnergyStorage {

        private final TrashCanBlockEntity entity;

        private TrashCanEnergyHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public long getAmount(){
            return 0;
        }

        @Override
        public long getCapacity(){
            return Long.MAX_VALUE;
        }

        @Override
        public long insert(long amount, TransactionContext transaction){
            StoragePreconditions.notNegative(amount);
            return Math.min(amount, this.entity.getMaxEnergyInsertion());
        }

        @Override
        public boolean supportsExtraction(){
            return false;
        }

        @Override
        public long extract(long amount, TransactionContext transaction){
            return 0;
        }
    }
}
