package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCanResourceHandlers;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class LiquidTrashCanContainer extends TrashCanContainer {

    public LiquidTrashCanContainer(PlayerEntity player, BlockPos pos){
        super(TrashCans.liquid_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(PlayerEntity player, TrashCanBlockEntity entity){
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

        // liquid filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(
                CustomSlot.builder()
                    .position(8 + column * 18, 64)
                    .getter(() -> {
                        ItemFilter filter = this.object.getFluidFilter(slotIndex);
                        return filter == null ? ItemStack.EMPTY : filter.getRepresentingItem();
                    })
                    .canInsertExtract(false)
                    .build().getVanillaSlot()
            );
        }
    }

    @Override
    public ItemStack clicked(int index, int dragType, ClickType clickType, PlayerEntity player){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9){
            ItemStack carried = this.player.inventory.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 1, filter);
            }
            return ItemStack.EMPTY;
        }
        return super.clicked(index, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 0){
            ItemStack stack = this.object.getFluidItem().copy();
            if(this.moveItemStackTo(stack, 10, this.slots.size(), true))
                this.object.setFluidItem(stack);
        }else if(index >= 1 && index <= 9){
            ItemStack carried = this.player.inventory.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 1, filter);
            }
        }else if(index >= 10){
            Slot slot = this.getSlot(index);
            ItemStack stack = slot.getItem().copy();
            if(this.moveItemStackTo(stack, 0, 1, false))
                slot.set(stack);
        }
        return ItemStack.EMPTY;
    }
}
