package com.supermartijn642.trashcans;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created 11/08/2026 by SuperMartijn642
 */
public class TrashCanResourceHandlers {

    public static boolean doesItemContainFluid(ItemStack stack){
        return false;
    }

    public static boolean doesItemHaveFluidHandler(ItemStack stack){
        return false;
    }

    public static boolean doesItemContainEnergy(ItemStack stack){
        return false;
    }

    public static boolean doesItemHaveEnergyHandler(ItemStack stack){
        return false;
    }

    public static IItemHandler createItemHandler(TrashCanBlockEntity entity){
        return new TrashCanItemHandler(entity);
    }

    public static IFluidHandler createFluidHandler(TrashCanBlockEntity entity){
        return new TrashCanFluidHandler(entity);
    }

    public static IEnergyStorage createEnergyHandler(TrashCanBlockEntity entity){
        return new TrashCanEnergyHandler(entity);
    }

    private static class TrashCanItemHandler implements IItemHandler {

        private final TrashCanBlockEntity entity;

        private TrashCanItemHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Override
        public int getSlotLimit(int slot){
            return Integer.MAX_VALUE;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot){
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack){
            return !stack.isEmpty() && this.entity.matchesItemFilter(stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
            return this.isItemValid(0, stack) ? ItemStack.EMPTY : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate){
            return ItemStack.EMPTY;
        }
    }

    private static class TrashCanFluidHandler implements IFluidHandler {

        private final TrashCanBlockEntity entity;

        private TrashCanFluidHandler(TrashCanBlockEntity entity){
            this.entity = entity;
        }

        @Override
        public int getTanks(){
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank){
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank){
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack){
            return !stack.isEmpty() && this.entity.matchesFluidFilter(stack);
        }

        @Override
        public int fill(FluidStack stack, FluidAction action){
            return this.isFluidValid(0, stack) ? stack.getAmount() : 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack stack, FluidAction action){
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int amount, FluidAction action){
            return FluidStack.EMPTY;
        }
    }

    private static class TrashCanEnergyHandler implements IEnergyStorage {

        private final TrashCanBlockEntity entity;

        private TrashCanEnergyHandler(TrashCanBlockEntity entity){
            this.entity = entity;
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
        public boolean canReceive(){
            return true;
        }

        @Override
        public int receiveEnergy(int amount, boolean simulate){
            if(amount < 0)
                return 0;
            return Math.min(amount, this.entity.getMaxEnergyInsertion());
        }

        @Override
        public boolean canExtract(){
            return false;
        }

        @Override
        public int extractEnergy(int amount, boolean simulate){
            return 0;
        }
    }
}
