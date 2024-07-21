package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.core.util.TriFunction;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.screen.DummySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
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
                if(!filter.isEmpty() && ItemStack.isSameItem(stack, filter))
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
                if(!filter.isEmpty() && ItemStack.isSameItem(stack, filter))
                    return TrashCanBlockEntity.this.itemFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.itemFilterWhitelist;
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
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(stack))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.liquidFilterWhitelist;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action){
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(resource))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist ? resource.getAmount() : 0;
            }
            return TrashCanBlockEntity.this.liquidFilterWhitelist ? 0 : resource.getAmount();
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

//            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).filter(handler -> { // TODO
//                for(int tank = 0; tank < handler.getTanks(); tank++)
//                    if(!handler.getFluidInTank(tank).isEmpty())
//                        return true;
//                return false;
//            }).isPresent();
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
//            return stack.getCapability(ForgeCapabilities.ENERGY).filter(storage -> storage.canExtract() && storage.getEnergyStored() > 0).isPresent(); // TODO
            return false;
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
//            this.liquidItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> { // TODO
//                boolean changed = false;
//                for(int tank = 0; tank < fluidHandler.getTanks(); tank++)
//                    if(!fluidHandler.getFluidInTank(tank).isEmpty()){
//                        fluidHandler.drain(fluidHandler.getFluidInTank(tank), IFluidHandler.FluidAction.EXECUTE);
//                        changed = true;
//                    }
//                if(changed){
//                    this.liquidItem = fluidHandler.getContainer();
//                    this.dataChanged();
//                }
//            });
        }
        if(this.energy && !this.energyItem.isEmpty()){
//            TrashCanBlockEntity.this.energyItem.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> { // TODO
//                energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
//                TrashCanBlockEntity.this.dataChanged();
//            });
        }
    }

    public boolean isRegularItemValid(ItemStack stack){
        for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
            if(!filter.isEmpty() && ItemStack.isSameItem(stack, filter))
                return TrashCanBlockEntity.this.itemFilterWhitelist;
        }
        return !TrashCanBlockEntity.this.itemFilterWhitelist;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
        if(this.items && cap == ForgeCapabilities.ITEM_HANDLER)
            return LazyOptional.of(() -> this.ITEM_HANDLER).cast();
        if(this.liquids && cap == ForgeCapabilities.FLUID_HANDLER)
            return LazyOptional.of(() -> this.FLUID_HANDLER).cast();
        if(this.energy && cap == ForgeCapabilities.ENERGY)
            return LazyOptional.of(() -> this.ENERGY_STORAGE).cast();
        return LazyOptional.empty();
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
