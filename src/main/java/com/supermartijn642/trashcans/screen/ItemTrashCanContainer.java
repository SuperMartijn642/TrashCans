package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanContainer extends TrashCanContainer {

    List<CustomSlot> deletedItemSlots;

    public ItemTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(TrashCans.item_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanBlockEntity entity){
        this.addSlot(
            CustomSlot.builder()
                .position(93, 25)
                .filter(s -> this.object.matchesItemFilter(s))
                .inserter(s -> {
                    this.object.pushDeletedItem(s);
                    return s.getCount();
                })
                .canExtract(false)
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
    public ItemStack slotClick(int index, int dragType, ClickType clickType, EntityPlayer player){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9){
            this.object.setItemFilter(index - 1, this.player.inventory.getItemStack());
            return ItemStack.EMPTY;
        }
        return super.slotClick(index, dragType, clickType, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9)
            this.object.setItemFilter(index - 1, this.player.inventory.getItemStack());
        else if(index >= 10 && index < 10 + TrashCanBlockEntity.MAX_DELETED_ITEMS){
            ItemStack stack = this.object.getDeletedItem(index - 10).copy();
            if(this.mergeItemStack(stack, 10 + TrashCanBlockEntity.MAX_DELETED_ITEMS, this.inventorySlots.size(), true)){
                if(stack.isEmpty())
                    this.object.removeDeletedItem(index - 10);
                else
                    this.object.setDeletedItem(index - 10, stack);
            }
        }else if(index >= 10 + TrashCanBlockEntity.MAX_DELETED_ITEMS){
            Slot slot = this.getSlot(index);
            ItemStack stack = slot.getStack();
            if(!stack.isEmpty() && this.object.matchesItemFilter(stack)){
                this.object.pushDeletedItem(stack.copy());
                slot.putStack(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }
}
