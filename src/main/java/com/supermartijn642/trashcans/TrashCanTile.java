package com.supermartijn642.trashcans;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanTile extends TileEntity {

    private static final IItemHandler ITEM_HANDLER = new IItemHandler() {
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
            return ItemStack.EMPTY;
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
            return true;
        }
    };

    private static final IFluidHandler FLUID_HANDLER = new IFluidHandler() {
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
                    return true;
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack){
                    return false;
                }
            }};
        }

        @Override
        public int fill(FluidStack resource, boolean doFill){
            return resource.amount;
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

    private static final IEnergyStorage ENERGY_STORAGE = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate){
            return maxReceive;
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

    public static class ItemTrashCanTile extends TrashCanTile {
        public ItemTrashCanTile(){
            super(true, false, false);
        }
    }

    public static class LiquidTrashCanTile extends TrashCanTile {
        public LiquidTrashCanTile(){
            super(false, true, false);
        }
    }

    public static class EnergyTrashCanTile extends TrashCanTile {
        public EnergyTrashCanTile(){
            super(false, false, true);
        }
    }

    public static class UltimateTrashCanTile extends TrashCanTile {
        public UltimateTrashCanTile(){
            super(true, true, true);
        }
    }

    private final boolean items;
    private final boolean liquids;
    private final boolean energy;

    public TrashCanTile(boolean items, boolean liquids, boolean energy){
        super();
        this.items = items;
        this.liquids = liquids;
        this.energy = energy;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing){
        return (this.items && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
            (this.liquids && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) ||
            (this.energy && cap == CapabilityEnergy.ENERGY) ||
            super.hasCapability(cap, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing facing){
        if(this.items && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ITEM_HANDLER);
        if(this.liquids && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(FLUID_HANDLER);
        if(this.energy && cap == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(ENERGY_STORAGE);
        return super.getCapability(cap, facing);
    }
}
