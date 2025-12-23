package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.core.util.TriFunction;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.screen.DummySlot;
import com.supermartijn642.trashcans.util.MutableItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;

    public final ResourceHandler<ItemResource> ITEM_HANDLER = new ResourceHandler<ItemResource>() {
        @Override
        public int size(){
            return 1;
        }

        @Override
        public ItemResource getResource(int index){
            return ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index){
            return 0;
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource){
            return this.isValid(index, resource) ? Long.MAX_VALUE : 0;
        }

        @Override
        public boolean isValid(int index, ItemResource resource){
            for(ItemStack filter : TrashCanBlockEntity.this.itemFilter){
                if(!filter.isEmpty() && resource.matches(filter))
                    return TrashCanBlockEntity.this.itemFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.itemFilterWhitelist;
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction){
            return this.isValid(index, resource) ? amount : 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction){
            return 0;
        }
    };

    public final ResourceHandler<FluidResource> FLUID_HANDLER = new ResourceHandler<>() {
        @Override
        public int size(){
            return 1;
        }

        @Override
        public FluidResource getResource(int index){
            return FluidResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index){
            return 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource){
            return this.isValid(index, resource) ? Long.MAX_VALUE : 0;
        }

        @Override
        public boolean isValid(int index, FluidResource resource){
            for(ItemFilter filter : TrashCanBlockEntity.this.liquidFilter){
                if(filter != null && filter.matches(resource))
                    return TrashCanBlockEntity.this.liquidFilterWhitelist;
            }
            return !TrashCanBlockEntity.this.liquidFilterWhitelist;
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction){
            return this.isValid(index, resource) ? amount : 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction){
            return 0;
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

            ResourceHandler<FluidResource> handler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
            if(handler != null){
                for(int i = 0; i < handler.size(); i++){
                    if(!handler.getResource(i).isEmpty() && handler.getAmountAsInt(i) > 0)
                        return true;
                }
            }
            return Compatibility.MEKANISM.doesItemHaveGasStored(stack);
        }
    };

    public final EnergyHandler ENERGY_STORAGE = new EnergyHandler() {
        @Override
        public long getAmountAsLong(){
            return 0;
        }

        @Override
        public long getCapacityAsLong(){
            return Long.MAX_VALUE;
        }

        @Override
        public int insert(int amount, TransactionContext transaction){
            return TrashCanBlockEntity.this.useEnergyLimit ? Math.min(amount, TrashCanBlockEntity.this.energyLimit) : amount;
        }

        @Override
        public int extract(int amount, TransactionContext transaction){
            return 0;
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
            EnergyHandler handler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
            return handler != null && handler.getAmountAsInt() > 0;
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

    private final MutableItemAccess itemAccess = new MutableItemAccess();

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
            this.itemAccess.setStack(this.liquidItem);
            ResourceHandler<FluidResource> handler = this.itemAccess.getCapability(Capabilities.Fluid.ITEM);
            if(handler != null){
                try(Transaction transaction = Transaction.openRoot()){
                    for(int i = 0; i < handler.size(); i++){
                        int amount = handler.getAmountAsInt(i);
                        if(amount > 0)
                            handler.extract(i, handler.getResource(i), amount, transaction);
                    }
                    transaction.commit();
                }
            }
            if(!this.itemAccess.matches(this.liquidItem)){
                this.liquidItem = this.itemAccess.getStack();
                this.dataChanged();
            }
            if(Compatibility.MEKANISM.drainGasFromItem(this.liquidItem))
                this.dataChanged();
        }
        if(this.energy && !this.energyItem.isEmpty()){
            this.itemAccess.setStack(this.energyItem);
            EnergyHandler handler = this.itemAccess.getCapability(Capabilities.Energy.ITEM);
            if(handler != null && handler.getAmountAsLong() > 0){
                try(Transaction transaction = Transaction.openRoot()){
                    handler.extract(handler.getAmountAsInt(), transaction);
                    transaction.commit();
                }
            }
            if(!this.itemAccess.matches(this.energyItem)){
                this.energyItem = this.itemAccess.getStack();
                this.dataChanged();
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
    protected void writeData(ValueOutput output){
        if(this.level.isClientSide()) // Forge/NeoForge try to save block entity data on the client and this can lead to crashes with items as some data like enchantments registries is not available on the client
            return;
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                if(!this.itemFilter.get(i).isEmpty())
                    output.store("itemFilter" + i, ItemStack.CODEC, this.itemFilter.get(i));
            output.putBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    LiquidTrashCanFilters.write(this.liquidFilter.get(i), output.child("liquidFilter" + i));
            output.putBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                output.store("liquidItem", ItemStack.CODEC, this.liquidItem);
        }
        if(this.energy){
            output.putBoolean("useEnergyLimit", this.useEnergyLimit);
            output.putInt("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                output.store("energyItem", ItemStack.CODEC, this.energyItem);
        }
    }

    @Override
    protected void readData(ValueInput input){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, input.read("itemFilter" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
            this.itemFilterWhitelist = input.getBooleanOr("itemFilterWhitelist", false);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, input.child("liquidFilter" + i).map(LiquidTrashCanFilters::read).orElse(null));
            this.liquidFilterWhitelist = input.getBooleanOr("liquidFilterWhitelist", false);
            this.liquidItem = input.read("liquidItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
        if(this.energy){
            this.useEnergyLimit = input.getBooleanOr("useEnergyLimit", false);
            this.energyLimit = input.getIntOr("energyLimit", DEFAULT_ENERGY_LIMIT);
            this.energyItem = input.read("energyItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
    }
}
