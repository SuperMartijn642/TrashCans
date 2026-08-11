package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;

    public final Storage<ItemVariant> itemHandler = TrashCanResourceHandlers.createItemHandler(this);
    public final Storage<FluidVariant> fluidHandler = TrashCanResourceHandlers.createFluidHandler(this);
    public final EnergyStorage energyHandler = TrashCanResourceHandlers.createEnergyHandler(this);

    private final boolean items;
    private final ArrayList<ItemStack> itemFilter = new ArrayList<>();
    private boolean itemFilterWhitelist = false;
    private final boolean liquids;
    private final ArrayList<ItemFilter> liquidFilter = new ArrayList<>();
    private boolean liquidFilterWhitelist = false;
    private ItemStack liquidItem = ItemStack.EMPTY;
    private final boolean energy;
    private int energyLimit = DEFAULT_ENERGY_LIMIT;
    private boolean useEnergyLimit = false;
    private ItemStack energyItem = ItemStack.EMPTY;

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

    public boolean handlesItems(){
        return this.items;
    }

    public ItemStack getItemFilter(int index){
        return this.itemFilter.get(index);
    }

    public void setItemFilter(int index, ItemStack stack){
        if(stack.isEmpty())
            stack = ItemStack.EMPTY;
        else{
            stack = stack.copy();
            stack.setCount(1);
        }
        this.itemFilter.set(index, stack);
        this.dataChanged();
    }

    public boolean matchesItemFilter(ItemStack stack){
        for(ItemStack filter : this.itemFilter){
            if(!filter.isEmpty() && ItemStack.isSameItem(stack, filter))
                return this.itemFilterWhitelist;
        }
        return !this.itemFilterWhitelist;
    }

    public boolean matchesItemFilter(ItemVariant resource){
        for(ItemStack filter : this.itemFilter){
            if(!filter.isEmpty() && resource.matches(filter))
                return this.itemFilterWhitelist;
        }
        return !this.itemFilterWhitelist;
    }

    public boolean isItemFilterWhitelist(){
        return this.itemFilterWhitelist;
    }

    public void toggleItemFilterWhitelist(){
        this.itemFilterWhitelist = !this.itemFilterWhitelist;
        this.dataChanged();
    }

    public boolean handlesFluids(){
        return this.liquids;
    }

    public ItemStack getFluidItem(){
        return this.liquidItem;
    }

    public void setFluidItem(ItemStack stack){
        this.liquidItem = stack;
        this.dataChanged();
    }

    @Nullable
    public ItemFilter getFluidFilter(int index){
        return this.liquidFilter.get(index);
    }

    public void setFluidFilter(int index, @Nullable ItemFilter filter){
        this.liquidFilter.set(index, filter);
        this.dataChanged();
    }

    public boolean matchesFluidFilter(ItemStack stack){
        for(ItemFilter filter : this.liquidFilter){
            if(filter != null && filter.matches(stack))
                return this.liquidFilterWhitelist;
        }
        return !this.liquidFilterWhitelist;
    }

    public boolean matchesFluidFilter(FluidVariant resource){
        for(ItemFilter filter : this.liquidFilter){
            if(filter != null && filter.matches(resource))
                return this.liquidFilterWhitelist;
        }
        return !this.liquidFilterWhitelist;
    }

    public boolean isFluidFilterWhitelist(){
        return this.liquidFilterWhitelist;
    }

    public void toggleFluidFilterWhitelist(){
        this.liquidFilterWhitelist = !this.liquidFilterWhitelist;
        this.dataChanged();
    }

    public boolean handlesEnergy(){
        return this.energy;
    }

    public ItemStack getEnergyItem(){
        return this.energyItem;
    }

    public void setEnergyItem(ItemStack stack){
        this.energyItem = stack;
        this.dataChanged();
    }

    public int getEnergyLimit(){
        return this.energyLimit;
    }

    public void setEnergyLimit(int limit){
        limit = Math.clamp(limit, MIN_ENERGY_LIMIT, MAX_ENERGY_LIMIT);
        if(limit == this.energyLimit)
            return;
        this.energyLimit = limit;
        this.dataChanged();
    }

    public boolean isEnergyLimited(){
        return this.useEnergyLimit;
    }

    public void toggleEnergyLimited(){
        this.useEnergyLimit = !this.useEnergyLimit;
        this.dataChanged();
    }

    public int getMaxEnergyInsertion(){
        return this.useEnergyLimit ? this.energyLimit : Integer.MAX_VALUE;
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
