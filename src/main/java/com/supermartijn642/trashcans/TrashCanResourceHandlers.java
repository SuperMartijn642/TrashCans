package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created 11/08/2026 by SuperMartijn642
 */
public class TrashCanResourceHandlers {

    public static boolean doesItemContainFluid(ItemStack stack){
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if(handler != null){
            for(IFluidTankProperties tank : handler.getTankProperties()){
                FluidStack contents = tank.getContents();
                if(tank.canDrain() && contents != null && contents.amount > 0)
                    return true;
            }
        }
        return Compatibility.MEKANISM.doesItemHaveGasStored(stack);
    }

    public static boolean doesItemHaveFluidHandler(ItemStack stack){
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) != null
            || Compatibility.MEKANISM.doesItemHaveGasHandler(stack);
    }

    public static boolean doesItemContainEnergy(ItemStack stack){
        IEnergyStorage handler = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return handler != null && handler.canExtract() && handler.getEnergyStored() > 0;
    }

    public static boolean doesItemHaveEnergyHandler(ItemStack stack){
        return stack.getCapability(CapabilityEnergy.ENERGY, null) != null;
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

        private final IFluidTankProperties[] tankProperties = {new IFluidTankProperties() {
            @Override
            public @Nullable FluidStack getContents(){
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
            public boolean canFillFluidType(FluidStack stack){
                return TrashCanFluidHandler.this.entity.matchesFluidFilter(stack);
            }

            @Override
            public boolean canDrainFluidType(FluidStack stack){
                return false;
            }
        }};

        @Override
        public IFluidTankProperties[] getTankProperties(){
            return this.tankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill){
            return this.entity.matchesFluidFilter(resource) ? resource.amount : 0;
        }

        @Override
        public @Nullable FluidStack drain(FluidStack resource, boolean doDrain){
            return null;
        }

        @Override
        public @Nullable FluidStack drain(int amount, boolean doDrain){
            return null;
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
