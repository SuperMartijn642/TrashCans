package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class LiquidTrashCanContainer extends TrashCanContainer {

    public LiquidTrashCanContainer(int id, PlayerEntity player, BlockPos pos){
        super(TrashCans.liquid_trash_can_container, id, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(PlayerEntity player, TrashCanTile tile){
        this.addSlot(new SlotItemHandler(tile.LIQUID_ITEM_HANDLER, 0, 93, 25));

        for(int column = 0; column < 9; column++)
            this.addSlot(new SlotItemHandler(this.itemHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean canTakeStack(PlayerEntity playerIn){
                    return false;
                }
            });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player){
        if(slotId >= 1 && slotId <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.liquidFilter.set(slotId - 1, null);
                else{
                    ItemFilter filter = LiquidTrashCanFilters.createFilter(player.inventory.getItemStack());
                    if(filter != null)
                        tile.liquidFilter.set(slotId - 1, filter);
                }
                tile.dataChanged();
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index){
        if(index == 0){
            if(this.mergeItemStack(this.getSlot(index).getStack(), 10, this.inventorySlots.size(), true))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }else if(index >= 1 && index <= 9){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.liquidFilter.set(index - 1, null);
                else{
                    ItemFilter filter = LiquidTrashCanFilters.createFilter(player.inventory.getItemStack());
                    if(filter != null)
                        tile.liquidFilter.set(index - 1, filter);
                }
                tile.dataChanged();
            }
        }else if(index >= 10 && !this.getSlot(index).getStack().isEmpty() && this.getSlot(0).getStack().isEmpty() && this.getSlot(0).isItemValid(this.getSlot(index).getStack())){
            TrashCanTile tile = this.getObjectOrClose();
            if(tile != null){
                this.getSlot(0).putStack(this.getSlot(index).getStack());
                this.getSlot(index).putStack(ItemStack.EMPTY);
                tile.dataChanged();
            }
        }
        return ItemStack.EMPTY;
    }

    private IItemHandlerModifiable itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanTile tile = LiquidTrashCanContainer.this.getObjectOrClose();
                return tile == null || tile.liquidFilter.get(slot) == null ? ItemStack.EMPTY : tile.liquidFilter.get(slot).getRepresentingItem();
            }
        };
    }
}
