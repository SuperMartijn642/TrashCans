package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;
    public static final int MAX_DELETED_ITEMS = 6;

    private final IItemHandler itemHandler = TrashCanResourceHandlers.createItemHandler(this);
    private final IFluidHandler fluidHandler = TrashCanResourceHandlers.createFluidHandler(this);
    private final IEnergyStorage energyHandler = TrashCanResourceHandlers.createEnergyHandler(this);

    private final boolean items;
    private final ArrayList<ItemStack> itemFilter = new ArrayList<>();
    private boolean itemFilterWhitelist = false;
    private final LinkedList<ItemStack> deletedItems = new LinkedList<>(); // We don't care about iteration, insertion at index 0 and removal of last element should be as fast as possible
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
            this.liquidItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {
                boolean changed = false;
                for(int tank = 0; tank < fluidHandler.getTanks(); tank++)
                    if(!fluidHandler.getFluidInTank(tank).isEmpty()){
                        fluidHandler.drain(fluidHandler.getFluidInTank(tank), IFluidHandler.FluidAction.EXECUTE);
                        changed = true;
                    }
                if(changed){
                    this.liquidItem = fluidHandler.getContainer();
                    this.dataChanged();
                }
            });
        }
        if(this.energy && !this.energyItem.isEmpty()){
            TrashCanBlockEntity.this.energyItem.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
                TrashCanBlockEntity.this.dataChanged();
            });
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

    public boolean isItemFilterWhitelist(){
        return this.itemFilterWhitelist;
    }

    public void toggleItemFilterWhitelist(){
        this.itemFilterWhitelist = !this.itemFilterWhitelist;
        this.dataChanged();
    }

    public void pushDeletedItem(ItemStack stack){
        if(!this.deletedItems.isEmpty()){
            ItemStack last = this.deletedItems.getFirst();
            if(ItemStack.isSameItemSameComponents(last, stack)){
                int added = Math.min(stack.getCount(), last.getMaxStackSize() - last.getCount());
                if(added > 0){
                    last.grow(added);
                    stack.shrink(added);
                    if(stack.isEmpty()){
                        this.dataChanged();
                        return;
                    }
                }
            }
        }
        this.deletedItems.addFirst(stack);
        if(this.deletedItems.size() > MAX_DELETED_ITEMS)
            this.deletedItems.removeLast();
        this.dataChanged();
    }

    public void setDeletedItem(int index, ItemStack stack){
        if(stack.isEmpty())
            throw new IllegalArgumentException("Stack cannot be empty!");
        this.deletedItems.set(index, stack);
        this.dataChanged();
    }

    public void removeDeletedItem(int index){
        this.deletedItems.remove(index);
        this.dataChanged();
    }

    public LinkedList<ItemStack> getDeletedItems(){
        return this.deletedItems;
    }

    public ItemStack getDeletedItem(int index){
        return this.deletedItems.get(index);
    }

    public void restoreDeletedItems(ItemStack[] items){ // Used when transaction are canceled
        this.deletedItems.clear();
        this.deletedItems.addAll(Arrays.asList(items));
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

    public boolean matchesFluidFilter(FluidStack stack){
        for(ItemFilter filter : this.liquidFilter){
            if(filter != null && filter.matches(stack))
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
        if(this.items && cap == ForgeCapabilities.ITEM_HANDLER)
            return LazyOptional.of(() -> this.itemHandler).cast();
        if(this.liquids && cap == ForgeCapabilities.FLUID_HANDLER)
            return LazyOptional.of(() -> this.fluidHandler).cast();
        if(this.energy && cap == ForgeCapabilities.ENERGY)
            return LazyOptional.of(() -> this.energyHandler).cast();
        return LazyOptional.empty();
    }

    @Override
    protected CompoundTag writeData(){
        if(this.level.isClientSide) // Forge/NeoForge try to save block entity data on the client and this can lead to crashes with items as some data like enchantments registries is not available on the client
            return new CompoundTag();
        CompoundTag tag = new CompoundTag();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                if(!this.itemFilter.get(i).isEmpty())
                    tag.put("itemFilter" + i, this.itemFilter.get(i).save(this.level.registryAccess()));
            tag.putBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
            ListTag deletedItems = new ListTag();
            for(ItemStack stack : this.deletedItems)
                deletedItems.add(stack.save(this.level.registryAccess()));
            tag.put("deletedItems", deletedItems);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    tag.put("liquidFilter" + i, LiquidTrashCanFilters.write(this.liquidFilter.get(i), this.level.registryAccess()));
            tag.putBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                tag.put("liquidItem", this.liquidItem.save(this.level.registryAccess()));
        }
        if(this.energy){
            tag.putBoolean("useEnergyLimit", this.useEnergyLimit);
            tag.putInt("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                tag.put("energyItem", this.energyItem.save(this.level.registryAccess()));
        }
        return tag;
    }

    @Override
    protected void readData(CompoundTag tag){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, tag.getCompound("itemFilter" + i).flatMap(t -> ItemStack.parse(CommonUtils.getRegistryAccess(), t)).orElse(ItemStack.EMPTY));
            this.itemFilterWhitelist = tag.getBooleanOr("itemFilterWhitelist", false);
            this.deletedItems.clear();
            for(Tag t : tag.getListOrEmpty("deletedItems"))
                ItemStack.parse(CommonUtils.getRegistryAccess(), t).ifPresent(this.deletedItems::add);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, tag.getCompound("liquidFilter" + i).map(t -> LiquidTrashCanFilters.read(t, CommonUtils.getRegistryAccess())).orElse(null));
            this.liquidFilterWhitelist = tag.getBooleanOr("liquidFilterWhitelist", false);
            this.liquidItem = tag.getCompound("liquidItem").flatMap(t -> ItemStack.parse(CommonUtils.getRegistryAccess(), t)).orElse(ItemStack.EMPTY);
        }
        if(this.energy){
            this.useEnergyLimit = tag.getBooleanOr("useEnergyLimit", false);
            this.energyLimit = tag.getIntOr("energyLimit", DEFAULT_ENERGY_LIMIT);
            this.energyItem = tag.getCompound("energyItem").flatMap(t -> ItemStack.parse(CommonUtils.getRegistryAccess(), t)).orElse(ItemStack.EMPTY);
        }
    }
}
