package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
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
public class LiquidTrashCanContainer extends TrashCanContainer {

    public LiquidTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(TrashCans.liquid_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.LIQUID_ITEM_HANDLER, 0, 93, 25));

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
                this.object.liquidFilter.set(slotId - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.player.inventory.getItemStack());
                if(filter != null)
                    this.object.liquidFilter.set(slotId - 1, filter);
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

        if(index == 0){
            if(this.mergeItemStack(this.getSlot(index).getStack(), 10, this.inventorySlots.size(), true))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }else if(index >= 1 && index <= 9){
            if(this.player.inventory.getItemStack().isEmpty())
                this.object.liquidFilter.set(index - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.player.inventory.getItemStack());
                if(filter != null)
                    this.object.liquidFilter.set(index - 1, filter);
            }
            this.object.dataChanged();
        }else if(index >= 10 && !this.getSlot(index).getStack().isEmpty() && this.getSlot(0).getStack().isEmpty() && this.getSlot(0).isItemValid(this.getSlot(index).getStack())){
            this.getSlot(0).putStack(this.getSlot(index).getStack());
            this.getSlot(index).putStack(ItemStack.EMPTY);
            this.object.dataChanged();
        }
        return ItemStack.EMPTY;
    }

    private IItemHandlerModifiable itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanBlockEntity entity = LiquidTrashCanContainer.this.object;
                return entity == null || entity.liquidFilter.get(slot) == null ? ItemStack.EMPTY : entity.liquidFilter.get(slot).getRepresentingItem();
            }
        };
    }
}
