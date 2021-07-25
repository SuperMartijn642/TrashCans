package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseTileEntity;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanTile extends BaseTileEntity implements ITickableTileEntity {

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
            for(ItemStack filter : TrashCanTile.this.itemFilter){
                if(!filter.isEmpty() && ItemStack.isSame(stack, filter))
                    return TrashCanTile.this.itemFilterWhitelist ? ItemStack.EMPTY : stack;
            }
            return TrashCanTile.this.itemFilterWhitelist ? stack : ItemStack.EMPTY;
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
            for(ItemStack filter : TrashCanTile.this.itemFilter){
                if(!filter.isEmpty() && ItemStack.isSame(stack, filter))
                    return TrashCanTile.this.itemFilterWhitelist;
            }
            return !TrashCanTile.this.itemFilterWhitelist;
        }
    };

    public final IFluidHandler FLUID_HANDLER = new IFluidHandler() {
        @Override
        public int getTanks(){
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank){
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank){
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
            for(ItemFilter filter : TrashCanTile.this.liquidFilter){
                if(filter != null && filter.matches(stack))
                    return TrashCanTile.this.liquidFilterWhitelist;
            }
            return !TrashCanTile.this.liquidFilterWhitelist;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action){
            for(ItemFilter filter : TrashCanTile.this.liquidFilter){
                if(filter != null && filter.matches(resource))
                    return TrashCanTile.this.liquidFilterWhitelist ? resource.getAmount() : 0;
            }
            return TrashCanTile.this.liquidFilterWhitelist ? 0 : resource.getAmount();
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action){
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action){
            return FluidStack.EMPTY;
        }
    };
    public final IItemHandler LIQUID_ITEM_HANDLER = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack){
            TrashCanTile.this.liquidItem = stack;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return TrashCanTile.this.liquidItem;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            if(!this.isItemValid(slot, stack) || !TrashCanTile.this.liquidItem.isEmpty() || stack.isEmpty())
                return stack;
            if(!simulate){
                TrashCanTile.this.liquidItem = stack.copy();
                TrashCanTile.this.liquidItem.setCount(1);
                TrashCanTile.this.dataChanged();
            }
            ItemStack stack1 = stack.copy();
            stack1.shrink(1);
            return stack1;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(amount <= 0 || TrashCanTile.this.liquidItem.isEmpty())
                return ItemStack.EMPTY;
            ItemStack stack = TrashCanTile.this.liquidItem.copy();
            stack.setCount(Math.min(amount, stack.getCount()));
            if(!simulate){
                TrashCanTile.this.liquidItem.shrink(amount);
                TrashCanTile.this.dataChanged();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot){
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            boolean filtered = !TrashCanTile.this.liquidFilterWhitelist;
            for(ItemFilter filter : TrashCanTile.this.liquidFilter){
                if(filter != null && filter.matches(stack)){
                    filtered = TrashCanTile.this.liquidFilterWhitelist;
                    break;
                }
            }
            if(!filtered)
                return false;

            return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).filter(handler -> {
                for(int tank = 0; tank < handler.getTanks(); tank++)
                    if(!handler.getFluidInTank(tank).isEmpty())
                        return true;
                return false;
            }).isPresent() ||
                Compatibility.MEKANISM.doesItemHaveGasStored(stack);
        }
    };

    public final IEnergyStorage ENERGY_STORAGE = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate){
            return TrashCanTile.this.useEnergyLimit ? Math.min(maxReceive, TrashCanTile.this.energyLimit) : maxReceive;
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
            TrashCanTile.this.energyItem = stack;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return TrashCanTile.this.energyItem;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            if(!this.isItemValid(slot, stack) || !TrashCanTile.this.energyItem.isEmpty() || stack.isEmpty())
                return stack;
            if(!simulate){
                TrashCanTile.this.energyItem = stack.copy();
                TrashCanTile.this.energyItem.setCount(1);
                TrashCanTile.this.dataChanged();
            }
            ItemStack stack1 = stack.copy();
            stack1.shrink(1);
            return stack1;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(amount <= 0 || TrashCanTile.this.energyItem.isEmpty())
                return ItemStack.EMPTY;
            ItemStack stack = TrashCanTile.this.energyItem.copy();
            stack.setCount(Math.min(amount, stack.getCount()));
            if(!simulate){
                TrashCanTile.this.energyItem.shrink(amount);
                TrashCanTile.this.dataChanged();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot){
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            return stack.getCapability(CapabilityEnergy.ENERGY).filter(storage -> storage.canExtract() && storage.getEnergyStored() > 0).isPresent();
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

    public TrashCanTile(TileEntityType<?> tileEntityTypeIn, boolean items, boolean liquids, boolean energy){
        super(tileEntityTypeIn);
        this.items = items;
        this.liquids = liquids;
        this.energy = energy;

        for(int i = 0; i < 9; i++){
            this.itemFilter.add(ItemStack.EMPTY);
            this.liquidFilter.add(null);
        }
    }

    @Override
    public void tick(){
        if(this.liquids && !this.liquidItem.isEmpty() && this.liquidItem.getItem() != Items.BUCKET){
            this.liquidItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluidHandler -> {
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
            if(Compatibility.MEKANISM.drainGasFromItem(this.liquidItem))
                this.dataChanged();
        }
        if(this.energy && !this.energyItem.isEmpty()){
            TrashCanTile.this.energyItem.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
                energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
                TrashCanTile.this.dataChanged();
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
        if(this.items && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> ITEM_HANDLER).cast();
        if(this.liquids){
            if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return LazyOptional.of(() -> FLUID_HANDLER).cast();
            else if(Compatibility.MEKANISM.isInstalled() && cap == Compatibility.MEKANISM.getGasHandlerCapability()){
                Object handler = Compatibility.MEKANISM.getGasHandler(this.liquidFilter, () -> TrashCanTile.this.liquidFilterWhitelist);
                return handler == null ? LazyOptional.empty() : LazyOptional.of(() -> handler).cast();
            }
        }
        if(this.energy && cap == CapabilityEnergy.ENERGY)
            return LazyOptional.of(() -> ENERGY_STORAGE).cast();
        return LazyOptional.empty();
    }

    @Override
    protected CompoundNBT writeData(){
        CompoundNBT tag = new CompoundNBT();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                tag.put("itemFilter" + i, this.itemFilter.get(i).save(new CompoundNBT()));
            tag.putBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    tag.put("liquidFilter" + i, LiquidTrashCanFilters.write(this.liquidFilter.get(i)));
            tag.putBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                tag.put("liquidItem", this.liquidItem.save(new CompoundNBT()));
        }
        if(this.energy){
            tag.putBoolean("useEnergyLimit", this.useEnergyLimit);
            tag.putInt("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                tag.put("energyItem", this.energyItem.save(new CompoundNBT()));
        }
        return tag;
    }

    @Override
    protected void readData(CompoundNBT tag){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, tag.contains("itemFilter" + i) ? ItemStack.of(tag.getCompound("itemFilter" + i)) : ItemStack.EMPTY);
            this.itemFilterWhitelist = tag.contains("itemFilterWhitelist") && tag.getBoolean("itemFilterWhitelist");
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, tag.contains("liquidFilter" + i) ? LiquidTrashCanFilters.read(tag.getCompound("liquidFilter" + i)) : null);
            this.liquidFilterWhitelist = tag.contains("liquidFilterWhitelist") && tag.getBoolean("liquidFilterWhitelist");
            this.liquidItem = tag.contains("liquidItem") ? ItemStack.of(tag.getCompound("liquidItem")) : ItemStack.EMPTY;
        }
        if(this.energy){
            this.useEnergyLimit = tag.contains("useEnergyLimit") && tag.getBoolean("useEnergyLimit");
            this.energyLimit = tag.contains("energyLimit") ? tag.getInt("energyLimit") : DEFAULT_ENERGY_LIMIT;
            this.energyItem = tag.contains("energyItem") ? ItemStack.of(tag.getCompound("energyItem")) : ItemStack.EMPTY;
        }
    }
}
