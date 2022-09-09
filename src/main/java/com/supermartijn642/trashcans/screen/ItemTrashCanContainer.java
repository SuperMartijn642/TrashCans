package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
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
        super(TrashCans.item_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.ITEM_HANDLER, 0, 93, 25));

        for(int column = 0; column < 9; column++)
            this.addSlot(new SlotItemHandler(this.itemHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn){
                    return false;
                }
            });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(slotId >= 1 && slotId <= 9){
            if(this.player.inventory.getItemStack().isEmpty())
                this.object.itemFilter.set(slotId - 1, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getItemStack().copy();
                stack.setCount(1);
                this.object.itemFilter.set(slotId - 1, stack);
            }
            this.object.dataChanged();
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index >= 1 && index <= 9){
            if(this.player.inventory.getItemStack().isEmpty())
                this.object.itemFilter.set(index - 1, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getItemStack().copy();
                stack.setCount(1);
                this.object.itemFilter.set(index - 1, stack);
            }
            this.object.dataChanged();
        }else if(index >= 10 && this.getSlot(0).isItemValid(this.getSlot(index).getStack()))
            this.getSlot(index).putStack(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    private IItemHandlerModifiable itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                return ItemTrashCanContainer.this.validateObjectOrClose() ? ItemTrashCanContainer.this.object.itemFilter.get(slot) : ItemStack.EMPTY;
            }
        };
    }
}
