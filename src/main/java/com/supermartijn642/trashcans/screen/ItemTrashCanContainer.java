package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanContainer extends TrashCanContainer {

    public ItemTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(player, pos, 202, 180);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanTile tile){
        this.addSlotToContainer(new SlotItemHandler(tile.ITEM_HANDLER, 0, 93, 25));

        for(int column = 0; column < 9; column++)
            this.addSlotToContainer(new SlotItemHandler(this.itemHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn){
                    return false;
                }
            });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(slotId >= 1 && slotId <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.itemFilter.set(slotId - 1, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getItemStack().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(slotId - 1, stack);
                }
                tile.dataChanged();
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        if(index >= 1 && index <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.itemFilter.set(index - 1, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getItemStack().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(index - 1, stack);
                }
                tile.dataChanged();
            }
        }else if(index >= 10 && this.getSlot(0).isItemValid(this.getSlot(index).getStack()))
            this.getSlot(index).putStack(ItemStack.EMPTY);
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
