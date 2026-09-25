package com.supermartijn642.trashcans;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
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

    /**
     * Copy of {@link ItemStack#CODEC}, but not arbitrary limited to 99 stack size.
     */
    private static final Codec<ItemStack> NOT_LIMITED_ITEM_STACK_CODEC = Codec.lazyInitialized(
        () -> RecordCodecBuilder.create(
            app -> app.group(
                ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
            ).apply(app, ItemStack::new)
        )
    );

    private static Tag encodeItemStack(ItemStack stack, HolderLookup.Provider registryAccess){
        if(stack.isEmpty())
            return new CompoundTag();
        return DataComponentUtil.wrapEncodingExceptions(stack, NOT_LIMITED_ITEM_STACK_CODEC, registryAccess);
    }

    private static ItemStack decodeItemStack(Tag tag, HolderLookup.Provider registryAccess){
        if(tag.getId() == Tag.TAG_COMPOUND && ((CompoundTag)tag).isEmpty())
            return ItemStack.EMPTY;
        return NOT_LIMITED_ITEM_STACK_CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), tag)
            .resultOrPartial(error -> TrashCans.LOGGER.error("Tried to load invalid item: '{}'", error))
            .orElse(ItemStack.EMPTY);
    }

    public final IItemHandler itemHandler = TrashCanResourceHandlers.createItemHandler(this);
    public final IFluidHandler fluidHandler = TrashCanResourceHandlers.createFluidHandler(this);
    public final IEnergyStorage energyHandler = TrashCanResourceHandlers.createEnergyHandler(this);
    public final Object gasHandler = Compatibility.MEKANISM.createGasHandler(this);

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
            IFluidHandlerItem fluidHandler = this.liquidItem.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidHandler != null){
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
            }
            if(Compatibility.MEKANISM.drainGasFromItem(this.liquidItem))
                this.dataChanged();
        }
        if(this.energy && !this.energyItem.isEmpty()){
            IEnergyStorage energyStorage = this.energyItem.getCapability(Capabilities.EnergyStorage.ITEM);
            if(energyStorage != null){
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

    public boolean matchesFluidFilter(FluidStack resource){
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
    protected CompoundTag writeData(){
        if(this.level.isClientSide) // Forge/NeoForge try to save block entity data on the client and this can lead to crashes with items as some data like enchantments registries is not available on the client
            return new CompoundTag();
        CompoundTag tag = new CompoundTag();
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                tag.put("itemFilter" + i, encodeItemStack(this.itemFilter.get(i), this.level.registryAccess()));
            tag.putBoolean("itemFilterWhitelist", this.itemFilterWhitelist);
            ListTag deletedItems = new ListTag();
            for(ItemStack stack : this.deletedItems)
                deletedItems.add(encodeItemStack(stack, this.level.registryAccess()));
            tag.put("deletedItems", deletedItems);
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                if(this.liquidFilter.get(i) != null)
                    tag.put("liquidFilter" + i, LiquidTrashCanFilters.write(this.liquidFilter.get(i), this.level.registryAccess()));
            tag.putBoolean("liquidFilterWhitelist", this.liquidFilterWhitelist);
            if(!this.liquidItem.isEmpty())
                tag.put("liquidItem", encodeItemStack(this.liquidItem, this.level.registryAccess()));
        }
        if(this.energy){
            tag.putBoolean("useEnergyLimit", this.useEnergyLimit);
            tag.putInt("energyLimit", this.energyLimit);
            if(!this.energyItem.isEmpty())
                tag.put("energyItem", encodeItemStack(this.energyItem, this.level.registryAccess()));
        }
        return tag;
    }

    @Override
    protected void readData(CompoundTag tag){
        if(this.items){
            for(int i = 0; i < this.itemFilter.size(); i++)
                this.itemFilter.set(i, tag.contains("itemFilter" + i) ? decodeItemStack(tag.getCompound("itemFilter" + i), CommonUtils.getRegistryAccess()) : ItemStack.EMPTY);
            this.itemFilterWhitelist = tag.contains("itemFilterWhitelist") && tag.getBoolean("itemFilterWhitelist");
            this.deletedItems.clear();
            for(Tag t : tag.getList("deletedItems", Tag.TAG_COMPOUND)){
                ItemStack stack = decodeItemStack(t, CommonUtils.getRegistryAccess());
                if(!stack.isEmpty())
                    this.deletedItems.add(stack);
            }
        }
        if(this.liquids){
            for(int i = 0; i < this.liquidFilter.size(); i++)
                this.liquidFilter.set(i, tag.contains("liquidFilter" + i) ? LiquidTrashCanFilters.read(tag.getCompound("liquidFilter" + i), CommonUtils.getRegistryAccess()) : null);
            this.liquidFilterWhitelist = tag.contains("liquidFilterWhitelist") && tag.getBoolean("liquidFilterWhitelist");
            this.liquidItem = tag.contains("liquidItem") ? decodeItemStack(tag.getCompound("liquidItem"), CommonUtils.getRegistryAccess()) : ItemStack.EMPTY;
        }
        if(this.energy){
            this.useEnergyLimit = tag.contains("useEnergyLimit") && tag.getBoolean("useEnergyLimit");
            this.energyLimit = tag.contains("energyLimit") ? tag.getInt("energyLimit") : DEFAULT_ENERGY_LIMIT;
            this.energyItem = tag.contains("energyItem") ? decodeItemStack(tag.getCompound("energyItem"), CommonUtils.getRegistryAccess()) : ItemStack.EMPTY;
        }
    }
}
