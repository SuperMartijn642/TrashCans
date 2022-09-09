package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanContainer extends TrashCanContainer {

    public EnergyTrashCanContainer(PlayerEntity player, BlockPos pos){
        super(TrashCans.energy_trash_can_container, player, pos, 202, 187);
    }

    @Override
    protected void addSlots(PlayerEntity player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.ENERGY_ITEM_HANDLER, 0, 93, 25));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 0){
            if(this.moveItemStackTo(this.getSlot(index).getItem(), 1, this.slots.size(), true))
                this.getSlot(index).set(ItemStack.EMPTY);
        }else if(!this.getSlot(index).getItem().isEmpty() && this.getSlot(0).getItem().isEmpty() && this.getSlot(0).mayPlace(this.getSlot(index).getItem())){
            this.getSlot(0).set(this.getSlot(index).getItem());
            this.getSlot(index).set(ItemStack.EMPTY);
            this.object.dataChanged();
        }
        return ItemStack.EMPTY;
    }
}
