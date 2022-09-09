package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;

    public final IItemHandler ITEM_HANDLER = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack){
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
                if(!filter.isEmpty() && ItemStack.areItemsEqual(stack, filter))
                    return TrashCanBlockEntity.this.itemFilterWhitelist ? ItemStack.EMPTY : stack;
            }
            return TrashCanBlockEntity.this.itemFilterWhitelist ? stack : ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot){
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
                if(!filter.isEmpty() && ItemStack.areItemsEqual(stack, filter))
                    return TrashCanBlockEntity.this.itemFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.itemFilterWhitelist;
        }
    };

    public final IFluidHandler FLUID_HANDLER = new IFluidHandler() {
        @Override
        public IFluidTankProperties[] getTankProperties(){
            return new IFluidTankProperties[]{new IFluidTankProperties() {
                @Nullable
                @Override
                public FluidStack getContents(){
                    return null;
                }

                @Override
                public int getCapacity(){
                    return Integer.MAX_VALUE;
                }

                @Override
                public boolean canFill(){
                    return true;
                }

                @Override
                public boolean canDrain(){
                    return false;
                }

                @Override
                public boolean canFillFluidType(FluidStack fluidStack){
                    return fluidStack == null || isFluidValid(0, fluidStack);
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack){
                    return false;
                }
            }};
        }

        public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
            if(stack == null)
                return false;
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(stack))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.liquidFilterWhitelist;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill){
            if(resource == null)
                return 0;
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(resource))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist ? resource.amount : 0;
            }
            return TrashCanBlockEntity.this.liquidFilterWhitelist ? 0 : resource.amount;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain){
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain){
            return null;
        }
    };
    public final IItemHandler LIQUID_ITEM_HANDLER = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack){
            TrashCanBlockEntity.this.liquidItem = stack;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return TrashCanBlockEntity.this.liquidItem;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            if(!this.isItemValid(slot, stack) || !TrashCanBlockEntity.this.liquidItem.isEmpty() || stack.isEmpty())
                return stack;
            if(!simulate){
                TrashCanBlockEntity.this.liquidItem = stack.copy();
                TrashCanBlockEntity.this.liquidItem.setCount(1);
                TrashCanBlockEntity.this.dataChanged();
            }
            ItemStack stack1 = stack.copy();
            stack1.shrink(1);
            return stack1;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(amount <= 0 || TrashCanBlockEntity.this.liquidItem.isEmpty())
                return ItemStack.EMPTY;
            ItemStack stack = TrashCanBlockEntity.this.liquidItem.copy();
            stack.setCount(Math.min(amount, stack.getCount()));
            if(!simulate){
                TrashCanBlockEntity.this.liquidItem.shrink(amount);
                TrashCanBlockEntity.this.dataChanged();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot){
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            boolean filtered = !TrashCanBlockEntity.this.liquidFilterWhitelist;
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(stack)){
                    filtered = TrashCanBlockEntity.this.liquidFilterWhitelist;
                    break;
                }
            }
            if(!filtered)
                return false;

            if(Compatibility.MEKANISM.doesItemHaveGasStored(stack))
                return true;
            if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                return false;
            IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if(handler == null)
                return false;
            IFluidTankProperties[] properties = handler.getTankProperties();
            if(properties == null)
                return false;
            for(IFluidTankProperties property : properties)
                if(property != null && property.canDrain() && property.getContents() != null && property.getContents().amount > 0)
                    return true;
            return false;
        }
    };

    public final IEnergyStorage ENERGY_STORAGE = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate){
            return TrashCanBlockEntity.this.useEnergyLimit ? Math.min(maxReceive, TrashCanBlockEntity.this.energyLimit) : maxReceive;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate){
            return 0;
        }

        @Override
        public int getEnergyStored(){
            return 0;
        }

        @Override
        public int getMaxEnergyStored(){
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract(){
            return false;
        }

        @Override
        public boolean canReceive(){
            return true;
        }
    };
    public final IItemHandler ENERGY_ITEM_HANDLER = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack){
            TrashCanBlockEntity.this.energyItem = stack;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return TrashCanBlockEntity.this.energyItem;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            if(!this.isItemValid(slot, stack) || !TrashCanBlockEntity.this.energyItem.isEmpty() || stack.isEmpty())
                return stack;
            if(!simulate){
                TrashCanBlockEntity.this.energyItem = stack.copy();
                TrashCanBlockEntity.this.energyItem.setCount(1);
                TrashCanBlockEntity.this.dataChanged();
            }
            ItemStack stack1 = stack.copy();
            stack1.shrink(1);
            return stack1;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(amount <= 0 || TrashCanBlockEntity.this.energyItem.isEmpty())
                return ItemStack.EMPTY;
            ItemStack stack = TrashCanBlockEntity.this.energyItem.copy();
            stack.setCount(Math.min(amount, stack.getCount()));
            if(!simulate){
                TrashCanBlockEntity.this.energyItem.shrink(amount);
                TrashCanBlockEntity.this.dataChanged();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot){
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            if(!stack.hasCapability(CapabilityEnergy.ENERGY, null))
                return false;
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            return storage != null && storage.canExtract() && storage.getEnergyStored() > 0;
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.ITEM_HANDLER);
        if(this.liquids){
            if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.FLUID_HANDLER);
            else if(Compatibility.MEKANISM.isInstalled() && cap == Compatibility.MEKANISM.getGasHandlerCapability())
                return Compatibility.MEKANISM.getGasHandler(this.liquidFilter, () -> TrashCanBlockEntity.this.liquidFilterWhitelist);
        }
        if(this.energy && cap == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(this.ENERGY_STORAGE);
        return super.getCapability(cap, facing);
    }

    @Override
    protected NBTTagCompound writeData(){
        NBTTagCompound tag = new NBTTagCompound();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                tag.setTag("itemFilter" + i, this.itemFilter.get(i).serializeNBT());
            tag.setBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
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
