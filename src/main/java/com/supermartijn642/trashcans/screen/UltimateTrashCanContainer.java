package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCanResourceHandlers;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanContainer extends TrashCanContainer {

    List<CustomSlot> deletedItemSlots;

    public UltimateTrashCanContainer(Player player, BlockPos pos){
        super(TrashCans.ultimate_trash_can_container, player, pos, 202, 247);
    }

    @Override
    protected void addSlots(Player player, TrashCanBlockEntity entity){
        // Items
        this.addSlot(
            CustomSlot.builder()
                .position(63, 25)
                .filter(s -> this.object.matchesItemFilter(s))
                .inserter(s -> {
                    this.object.pushDeletedItem(s);
                    return s.getCount();
                })
                .canExtract(false)
                .build().getVanillaSlot()
        );
        // Fluids
        this.addSlot(
            CustomSlot.builder()
                .position(93, 25)
                .getter(() -> this.object.getFluidItem())
                .setter(s -> this.object.setFluidItem(s))
                .filter(s -> this.object.matchesFluidFilter(s) && TrashCanResourceHandlers.doesItemContainFluid(s))
                .inserter(s -> {
                    int inserted = Math.min(s.getCount(), s.getMaxStackSize());
                    s = s.copy();
                    s.setCount(inserted);
                    this.object.setFluidItem(s);
                    return inserted;
                })
                .extractor(amount -> {
                    ItemStack stack = this.object.getFluidItem();
                    amount = Math.min(amount, stack.getCount());
                    if(amount <= 0)
                        return ItemStack.EMPTY;
                    ItemStack extractedStack = stack.copy();
                    extractedStack.setCount(amount);
                    stack = stack.copy();
                    stack.shrink(amount);
                    this.object.setFluidItem(stack);
                    return extractedStack;
                })
                .build().getVanillaSlot()
        );
        // Energy
        this.addSlot(
            CustomSlot.builder()
                .position(123, 25)
                .getter(() -> this.object.getEnergyItem())
                .setter(s -> this.object.setEnergyItem(s))
                .filter(TrashCanResourceHandlers::doesItemContainEnergy)
                .inserter(s -> {
                    int inserted = Math.min(s.getCount(), s.getMaxStackSize());
                    s = s.copy();
                    s.setCount(inserted);
                    this.object.setEnergyItem(s);
                    return inserted;
                })
                .extractor(amount -> {
                    ItemStack stack = this.object.getEnergyItem();
                    amount = Math.min(amount, stack.getCount());
                    if(amount <= 0)
                        return ItemStack.EMPTY;
                    ItemStack extractedStack = stack.copy();
                    extractedStack.setCount(amount);
                    stack = stack.copy();
                    stack.shrink(amount);
                    this.object.setEnergyItem(stack);
                    return extractedStack;
                })
                .build().getVanillaSlot()
        );

        // Item filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(
                CustomSlot.builder()
                    .position(8 + column * 18, 64)
                    .getter(() -> this.object.getItemFilter(slotIndex))
                    .canInsertExtract(false)
                    .build().getVanillaSlot()
            );
        }
        // Fluid filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(
                CustomSlot.builder()
                    .position(8 + column * 18, 94)
                    .getter(() -> {
                        ItemFilter filter = this.object.getFluidFilter(slotIndex);
                        return filter == null ? ItemStack.EMPTY : filter.getRepresentingItem();
                    })
                    .canInsertExtract(false)
                    .build().getVanillaSlot()
            );
        }

        // Deleted items
        List<CustomSlot> deletedItemSlots = new ArrayList<>(TrashCanBlockEntity.MAX_DELETED_ITEMS);
        for(int i = 0; i < TrashCanBlockEntity.MAX_DELETED_ITEMS; i++){
            int index = i;
            deletedItemSlots.add(
                CustomSlot.builder()
                    .getter(() -> {
                        LinkedList<ItemStack> items = this.object.getDeletedItems();
                        return items.size() > index ? items.get(index) : ItemStack.EMPTY;
                    })
                    .setter(s -> {
                        LinkedList<ItemStack> items = this.object.getDeletedItems();
                        if(items.size() > index){
                            if(s.isEmpty())
                                this.object.removeDeletedItem(index);
                            else
                                this.object.setDeletedItem(index, s);
                        }
                    })
                    .canInsert(false)
                    .extractor(amount -> {
                        ItemStack stack = this.object.getDeletedItem(index);
                        int extracted = Math.min(amount, stack.getCount());
                        ItemStack extractedStack = stack.copy();
                        extractedStack.setCount(extracted);
                        if(extracted == stack.getCount())
                            this.object.removeDeletedItem(index);
                        else{
                            stack = stack.copy();
                            stack.shrink(extracted);
                            this.object.setDeletedItem(index, stack);
                        }
                        return extractedStack;
                    })
                    .build()
            );
        }
        deletedItemSlots.forEach(s -> s.setActive(false));
        deletedItemSlots.forEach(s -> this.addSlot(s.getVanillaSlot()));
        this.deletedItemSlots = deletedItemSlots;
    }

    @Override
    public void clicked(int index, int dragType, ClickType clickType, Player player){
        if(!this.validateObjectOrClose())
            return;

        if(index >= 3 && index <= 11){
            this.object.setItemFilter(index - 3, this.getCarried());
            return;
        }else if(index >= 12 && index <= 20){
            ItemStack carried = this.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 12, filter);
            }
            return;
        }
        super.clicked(index, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 1){
            ItemStack stack = this.object.getFluidItem().copy();
            if(this.moveItemStackTo(stack, 21 + TrashCanBlockEntity.MAX_DELETED_ITEMS, this.slots.size(), true))
                this.object.setFluidItem(stack);
        }else if(index == 2){
            ItemStack stack = this.object.getEnergyItem().copy();
            if(this.moveItemStackTo(stack, 21 + TrashCanBlockEntity.MAX_DELETED_ITEMS, this.slots.size(), true))
                this.object.setEnergyItem(stack);
        }else if(index >= 3 && index <= 11)
            this.object.setItemFilter(index - 3, this.getCarried());
        else if(index >= 12 && index <= 20){
            ItemStack carried = this.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 12, filter);
            }
        }else if(index >= 21 && index < 21 + TrashCanBlockEntity.MAX_DELETED_ITEMS){
            ItemStack stack = this.object.getDeletedItem(index - 21).copy();
            if(this.moveItemStackTo(stack, 21 + TrashCanBlockEntity.MAX_DELETED_ITEMS, this.slots.size(), true)){
                if(stack.isEmpty())
                    this.object.removeDeletedItem(index - 21);
                else
                    this.object.setDeletedItem(index - 21, stack);
            }
        }else if(index >= 21 + TrashCanBlockEntity.MAX_DELETED_ITEMS && !this.getSlot(index).getItem().isEmpty()){
            Slot slot = this.getSlot(index);
            ItemStack stack = slot.getItem().copy();
            if(TrashCanResourceHandlers.doesItemHaveFluidHandler(stack)){
                if(this.moveItemStackTo(stack, 1, 2, false))
                    slot.set(stack);
            }else if(TrashCanResourceHandlers.doesItemHaveEnergyHandler(stack)){
                if(this.moveItemStackTo(stack, 2, 3, false))
                    slot.set(stack);
            }else{
                if(!stack.isEmpty() && this.object.matchesItemFilter(stack)){
                    this.object.pushDeletedItem(stack);
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
