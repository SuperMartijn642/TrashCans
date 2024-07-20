package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.core.util.TriFunction;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.screen.DummySlot;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
@SuppressWarnings("UnstableApiUsage")
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;

    public final Storage<ItemVariant> ITEM_HANDLER = new Storage<>() {
        private static final Supplier<Iterator<StorageView<ItemVariant>>> ITERATOR = List.<StorageView<ItemVariant>>of(new BlankVariantView<>(ItemVariant.blank(), Integer.MAX_VALUE))::iterator;

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return this.isItemValid(resource) ? maxAmount : 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return 0;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator(){
            return ITERATOR.get();
        }

        private boolean isItemValid(ItemVariant item){
            for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
                if(!filter.isEmpty() && item.matches(filter))
                    return TrashCanBlockEntity.this.itemFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.itemFilterWhitelist;
        }
    };

    public final Storage<FluidVariant> FLUID_HANDLER = new Storage<>() {
        private static final Supplier<Iterator<StorageView<FluidVariant>>> ITERATOR = List.<StorageView<FluidVariant>>of(new BlankVariantView<>(FluidVariant.blank(), Integer.MAX_VALUE))::iterator;

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return this.isFluidValid(resource) ? maxAmount : 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction){
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator(){
            return ITERATOR.get();
        }

        private boolean isFluidValid(FluidVariant fluid){
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(fluid))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.liquidFilterWhitelist;
        }
    };
    public final TriFunction<Integer,Integer,Integer,Slot> LIQUID_ITEM_HANDLER = (slot, x, y) -> new DummySlot(slot, x, y) {
        @Override
        public boolean mayPlace(ItemStack stack){
            return this.isItemValid(stack);
        }

        @Override
        public ItemStack getItem(){
            return TrashCanBlockEntity.this.liquidItem;
        }

        @Override
        public void set(ItemStack stack){
            TrashCanBlockEntity.this.liquidItem = stack.copy();
            TrashCanBlockEntity.this.liquidItem.setCount(1);
            TrashCanBlockEntity.this.dataChanged();
        }

        @Override
        public void setChanged(){
        }

        @Override
        public int getMaxStackSize(){
            return 1;
        }

        @Override
        public ItemStack remove(int count){
            ItemStack result = TrashCanBlockEntity.this.liquidItem.split(count);
            TrashCanBlockEntity.this.dataChanged();
            return result;
        }

        public boolean isItemValid(ItemStack stack){
            boolean filtered = !TrashCanBlockEntity.this.liquidFilterWhitelist;
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(stack)){
                    filtered = TrashCanBlockEntity.this.liquidFilterWhitelist;
                    break;
                }
            }
            if(!filtered)
                return false;

            Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
            if(storage == null || !storage.supportsExtraction())
                return false;
            for(StorageView<FluidVariant> slot : storage){
                if(!slot.isResourceBlank())
                    return true;
            }
            return false;
        }
    };

    public final EnergyStorage ENERGY_STORAGE = new EnergyStorage() {
        @Override
        public boolean supportsExtraction(){
            return false;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction){
            return TrashCanBlockEntity.this.useEnergyLimit ? Math.min(maxAmount, TrashCanBlockEntity.this.energyLimit) : maxAmount;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction){
            return 0;
        }

        @Override
        public long getAmount(){
            return 0;
        }

        @Override
        public long getCapacity(){
            return Long.MAX_VALUE;
        }
    };
    public final TriFunction<Integer,Integer,Integer,Slot> ENERGY_ITEM_HANDLER = (slot, x, y) -> new DummySlot(slot, x, y) {
        @Override
        public boolean mayPlace(ItemStack stack){
            return this.isItemValid(stack);
        }

        @Override
        public ItemStack getItem(){
            return TrashCanBlockEntity.this.energyItem;
        }

        @Override
        public void set(ItemStack stack){
            TrashCanBlockEntity.this.energyItem = stack.copy();
            TrashCanBlockEntity.this.energyItem.setCount(1);
            TrashCanBlockEntity.this.dataChanged();
        }

        @Override
        public void setChanged(){
        }

        @Override
        public int getMaxStackSize(){
            return 1;
        }

        @Override
        public ItemStack remove(int count){
            ItemStack result = TrashCanBlockEntity.this.energyItem.split(count);
            TrashCanBlockEntity.this.dataChanged();
            return result;
        }

        public boolean isItemValid(ItemStack stack){
            EnergyStorage storage = EnergyStorage.ITEM.find(stack, null);
            return storage != null && storage.supportsExtraction() && storage.getAmount() > 0;
        }
    };

    public final boolean items;
    public final ArrayList<ItemStack> itemFilter = new ArrayList<>();
    public boolean itemFilterWhitelist = false;
    public final boolean liquids;
    public final ArrayList<ItemFilter> liquidFilter = new ArrayList<>();
    public boolean liquidFilterWhitelist = false;
    public ItemStack liquidItem = ItemStack.EMPTY;
    public final boolean energy;
    public int energyLimit = DEFAULT_ENERGY_LIMIT;
    public boolean useEnergyLimit = false;
    public ItemStack energyItem = ItemStack.EMPTY;

    public TrashCanBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, boolean items, boolean liquids, boolean energy){
        super(blockEntityType, pos, state);
        this.items = items;
        this.liquids = liquids;
        this.energy = energy;

        for(int i = 0; i < 9; i++){
            this.itemFilter.add(ItemStack.EMPTY);
            this.liquidFilter.add(null);
        }
    }

    @Override
    public void update(){
        if(this.liquids && !this.liquidItem.isEmpty() && this.liquidItem.getItem() != Items.BUCKET){
            ContainerItemContext context = ContainerItemContext.withConstant(this.liquidItem);
            Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
            if(storage != null && storage.supportsExtraction()){
                try(Transaction transaction = Transaction.openOuter()){
                    boolean changed = false;
                    for(StorageView<FluidVariant> slot : storage){
                        if(!slot.isResourceBlank() && slot.getAmount() > 0 && slot.extract(slot.getResource(), slot.getAmount(), transaction) > 0)
                            changed = true;
                    }
                    if(changed && (context.getItemVariant().isBlank() || context.getAmount() <= context.getItemVariant().getItem().getDefaultMaxStackSize())){
                        transaction.commit();
                        this.liquidItem = context.getItemVariant().toStack((int)context.getAmount());
                        this.dataChanged();
                    }
                }
            }
        }
        if(this.energy && !this.energyItem.isEmpty()){
            ContainerItemContext context = ContainerItemContext.withConstant(this.energyItem);
            EnergyStorage storage = context.find(EnergyStorage.ITEM);
            if(storage != null && storage.supportsExtraction() && storage.getAmount() > 0){
                try(Transaction transaction = Transaction.openOuter()){
                    boolean changed = storage.supportsExtraction() && storage.extract(storage.getAmount(), transaction) > 0;
                    if(changed && (context.getItemVariant().isBlank() || context.getAmount() <= context.getItemVariant().getItem().getDefaultMaxStackSize())){
                        transaction.commit();
                        this.energyItem = context.getItemVariant().toStack((int)context.getAmount());
                        this.dataChanged();
                    }
                }
            }
        }
    }

    public boolean isRegularItemValid(ItemStack stack){
        for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
            if(!filter.isEmpty() && ItemStack.isSameItem(stack, filter))
                return TrashCanBlockEntity.this.itemFilterWhitelist;
        }
        return !TrashCanBlockEntity.this.itemFilterWhitelist;
    }

    @Override
    protected CompoundTag writeData(){
        CompoundTag tag = new CompoundTag();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                tag.put("itemFilter" + i, this.itemFilter.get(i).saveOptional(this.level.registryAccess()));
            tag.putBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    tag.put("liquidFilter" + i, LiquidTrashCanFilters.write(this.liquidFilter.get(i), this.level.registryAccess()));
            tag.putBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                tag.put("liquidItem", this.liquidItem.saveOptional(this.level.registryAccess()));
        }
        if(this.energy){
            tag.putBoolean("useEnergyLimit", this.useEnergyLimit);
            tag.putInt("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                tag.put("energyItem", this.energyItem.saveOptional(this.level.registryAccess()));
        }
        return tag;
    }

    @Override
    protected void readData(CompoundTag tag){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, tag.contains("itemFilter" + i) ? ItemStack.parseOptional(CommonUtils.getRegistryAccess(), tag.getCompound("itemFilter" + i)) : ItemStack.EMPTY);
            this.itemFilterWhitelist = tag.contains("itemFilterWhitelist") && tag.getBoolean("itemFilterWhitelist");
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, tag.contains("liquidFilter" + i) ? LiquidTrashCanFilters.read(tag.getCompound("liquidFilter" + i), CommonUtils.getRegistryAccess()) : null);
            this.liquidFilterWhitelist = tag.contains("liquidFilterWhitelist") && tag.getBoolean("liquidFilterWhitelist");
            this.liquidItem = tag.contains("liquidItem") ? ItemStack.parseOptional(CommonUtils.getRegistryAccess(), tag.getCompound("liquidItem")) : ItemStack.EMPTY;
        }
        if(this.energy){
            this.useEnergyLimit = tag.contains("useEnergyLimit") && tag.getBoolean("useEnergyLimit");
            this.energyLimit = tag.contains("energyLimit") ? tag.getInt("energyLimit") : DEFAULT_ENERGY_LIMIT;
            this.energyItem = tag.contains("energyItem") ? ItemStack.parseOptional(CommonUtils.getRegistryAccess(), tag.getCompound("energyItem")) : ItemStack.EMPTY;
        }
    }
}
