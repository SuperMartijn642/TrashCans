package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanContainer extends TrashCanContainer {

    public EnergyTrashCanContainer(int id, Player player, BlockPos pos){
        super(TrashCans.energy_trash_can_container, id, player, pos, 202, 187);
    }

    @Override
    protected void addSlots(Player player, TrashCanTile tile){
        this.addSlot(new SlotItemHandler(tile.ENERGY_ITEM_HANDLER, 0, 93, 25));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index){
        if(index == 0){
            if(this.moveItemStackTo(this.getSlot(index).getItem(), 1, this.slots.size(), true))
                this.getSlot(index).set(ItemStack.EMPTY);
        }else if(!this.getSlot(index).getItem().isEmpty() && this.getSlot(0).getItem().isEmpty() && this.getSlot(0).mayPlace(this.getSlot(index).getItem())){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                this.getSlot(0).set(this.getSlot(index).getItem());
                this.getSlot(index).set(ItemStack.EMPTY);
                tile.dataChanged();
            }
        }
        return ItemStack.EMPTY;
    }
}
