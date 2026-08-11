package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

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
    private final Object gasHandler = Compatibility.MEKANISM.createGasHandler(this);

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

    public TrashCanBlockEntity(BaseBlockEntityType<?> blockEntityType, boolean items, boolean liquids, boolean energy){
        super(blockEntityType);
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
            if(this.liquidItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)){
                IFluidHandlerItem fluidHandler = this.liquidItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if(fluidHandler != null){
                    IFluidTankProperties[] properties = fluidHandler.getTankProperties();
                    if(properties != null){
                        boolean changed = false;
                        for(IFluidTankProperties property : properties)
                            if(property.getContents() != null && property.canDrain() && property.getContents().amount > 0){
                                fluidHandler.drain(property.getContents(), true);
                                changed = true;
                            }
                        if(changed){
                            this.liquidItem = fluidHandler.getContainer();
                            this.dataChanged();
                        }
                    }
                }
            }
            if(Compatibility.MEKANISM.drainGasFromItem(this.liquidItem))
                this.dataChanged();
        }
        if(this.energy && !this.energyItem.isEmpty() && this.energyItem.hasCapability(CapabilityEnergy.ENERGY, null)){
            IEnergyStorage energyStorage = this.energyItem.getCapability(CapabilityEnergy.ENERGY, null);
            if(energyStorage != null && energyStorage.canExtract() && energyStorage.getEnergyStored() > 0){
                energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
                this.dataChanged();
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
            if(!filter.isEmpty() && ItemStack.areItemsEqual(stack, filter))
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
            if(ItemStack.areItemsEqual(last, stack) && ItemStack.areItemStackTagsEqual(last, stack)){
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
        limit = Math.min(Math.max(limit, MIN_ENERGY_LIMIT), MAX_ENERGY_LIMIT);
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
    public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing){
        return (this.items && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
            (this.liquids &&
                (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || (Compatibility.MEKANISM.isInstalled() && cap == Compatibility.MEKANISM.getGasHandlerCapability()))) ||
            (this.energy && cap == CapabilityEnergy.ENERGY) ||
            super.hasCapability(cap, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing facing){
        if(this.items && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
        if(this.liquids){
            if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidHandler);
            if(Compatibility.MEKANISM.isInstalled() && cap == Compatibility.MEKANISM.getGasHandlerCapability())
                //noinspection unchecked
                return (T)this.gasHandler;
        }
        if(this.energy && cap == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(this.energyHandler);
        return super.getCapability(cap, facing);
    }

    @Override
    protected NBTTagCompound writeData(){
        NBTTagCompound tag = new NBTTagCompound();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                tag.setTag("itemFilter" + i, this.itemFilter.get(i).serializeNBT());
            tag.setBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
            NBTTagList deletedItems = new NBTTagList();
            for(ItemStack stack : this.deletedItems)
                deletedItems.appendTag(stack.serializeNBT());
            tag.setTag("deletedItems", deletedItems);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    tag.setTag("liquidFilter" + i, LiquidTrashCanFilters.write(this.liquidFilter.get(i)));
            tag.setBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                tag.setTag("liquidItem", this.liquidItem.serializeNBT());
        }
        if(this.energy){
            tag.setBoolean("useEnergyLimit", this.useEnergyLimit);
            tag.setInteger("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                tag.setTag("energyItem", this.energyItem.serializeNBT());
        }
        return tag;
    }

    @Override
    protected void readData(NBTTagCompound tag){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, tag.hasKey("itemFilter" + i) ? new ItemStack(tag.getCompoundTag("itemFilter" + i)) : ItemStack.EMPTY);
            this.itemFilterWhitelist = tag.hasKey("itemFilterWhitelist") && tag.getBoolean("itemFilterWhitelist");
            this.deletedItems.clear();
            for(NBTBase t : tag.getTagList("deletedItems", Constants.NBT.TAG_COMPOUND))
                this.deletedItems.add(new ItemStack((NBTTagCompound)t));
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, tag.hasKey("liquidFilter" + i) ? LiquidTrashCanFilters.read(tag.getCompoundTag("liquidFilter" + i)) : null);
            this.liquidFilterWhitelist = tag.hasKey("liquidFilterWhitelist") && tag.getBoolean("liquidFilterWhitelist");
            this.liquidItem = tag.hasKey("liquidItem") ? new ItemStack(tag.getCompoundTag("liquidItem")) : ItemStack.EMPTY;
        }
        if(this.energy){
            this.useEnergyLimit = tag.hasKey("useEnergyLimit") && tag.getBoolean("useEnergyLimit");
            this.energyLimit = tag.hasKey("energyLimit") ? tag.getInteger("energyLimit") : DEFAULT_ENERGY_LIMIT;
            this.energyItem = tag.hasKey("energyItem") ? new ItemStack(tag.getCompoundTag("energyItem")) : ItemStack.EMPTY;
        }
    }
}
