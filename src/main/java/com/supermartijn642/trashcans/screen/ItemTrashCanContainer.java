package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanContainer extends TrashCanContainer {

    public ItemTrashCanContainer(PlayerEntity player, BlockPos pos){
        super(TrashCans.item_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(PlayerEntity player, TrashCanBlockEntity entity){
        this.addSlot(
            CustomSlot.builder()
                .position(93, 25)
                .filter(s -> this.object.matchesItemFilter(s))
                .inserter(ItemStack::getCount)
                .canExtract(false)
                .build().getVanillaSlot()
        );

        // item filter
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
    }

    @Override
    public ItemStack clicked(int index, int dragType, ClickType clickType, PlayerEntity player){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9){
            this.object.setItemFilter(index - 1, this.player.inventory.getCarried());
            return ItemStack.EMPTY;
        }
        return super.clicked(index, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9)
            this.object.setItemFilter(index - 1, this.player.inventory.getCarried());
        else if(index >= 10){
            Slot slot = this.getSlot(index);
            if(this.object.matchesItemFilter(slot.getItem()))
                slot.set(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
