package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanContainer extends TrashCanContainer {

    public ItemTrashCanContainer(int id, Player player, BlockPos pos){
        super(TrashCans.item_trash_can_container, id, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(Player player, TrashCanTile tile){
        this.addSlot(new SlotItemHandler(tile.ITEM_HANDLER, 0, 93, 25));

        for(int column = 0; column < 9; column++)
            this.addSlot(new SlotItemHandler(this.itemHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean mayPickup(Player playerIn){
                    return false;
                }
            });
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player){
        if(slotId >= 1 && slotId <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(this.getCarried().isEmpty())
                    tile.itemFilter.set(slotId - 1, ItemStack.EMPTY);
                else{
                    ItemStack stack = this.getCarried().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(slotId - 1, stack);
                }
                tile.dataChanged();
            }
            return;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index){
        if(index >= 1 && index <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(this.getCarried().isEmpty())
                    tile.itemFilter.set(index - 1, ItemStack.EMPTY);
                else{
                    ItemStack stack = this.getCarried().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(index - 1, stack);
                }
                tile.dataChanged();
            }
        }else if(index >= 10 && this.getSlot(0).mayPlace(this.getSlot(index).getItem()))
            this.getSlot(index).set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    private IItemHandlerModifiable itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanTile tile = ItemTrashCanContainer.this.getObjectOrClose();
                return tile == null ? ItemStack.EMPTY : tile.itemFilter.get(slot);
            }
        };
    }
}
