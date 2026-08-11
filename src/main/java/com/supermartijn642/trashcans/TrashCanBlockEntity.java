package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.util.MutableItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public static final int DEFAULT_ENERGY_LIMIT = 10000, MAX_ENERGY_LIMIT = 10000000, MIN_ENERGY_LIMIT = 1;

    public final ResourceHandler<ItemResource> itemHandler = TrashCanResourceHandlers.createItemHandler(this);
    public final ResourceHandler<FluidResource> fluidHandler = TrashCanResourceHandlers.createFluidHandler(this);
    public final EnergyHandler energyHandler = TrashCanResourceHandlers.createEnergyHandler(this);
    public final Object gasHandler = Compatibility.MEKANISM.createGasHandler(this);

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

    public boolean matchesItemFilter(ItemResource resource){
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

    public boolean matchesFluidFilter(FluidResource resource){
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
