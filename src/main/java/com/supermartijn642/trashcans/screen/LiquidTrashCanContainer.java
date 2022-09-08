package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
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
public class LiquidTrashCanContainer extends TrashCanContainer {

    public LiquidTrashCanContainer(Player player, BlockPos pos){
        super(TrashCans.liquid_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(Player player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.LIQUID_ITEM_HANDLER, 0, 93, 25));

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
        if(!this.validateObjectOrClose())
            return;

        if(slotId >= 1 && slotId <= 9){
            if(this.getCarried().isEmpty())
                this.object.liquidFilter.set(slotId - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.getCarried());
                if(filter != null)
                    this.object.liquidFilter.set(slotId - 1, filter);
            }
            this.object.dataChanged();
            return;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 0){
            if(this.moveItemStackTo(this.getSlot(index).getItem(), 10, this.slots.size(), true))
                this.getSlot(index).set(ItemStack.EMPTY);
        }else if(index >= 1 && index <= 9){
            if(this.getCarried().isEmpty())
                this.object.liquidFilter.set(index - 1, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.getCarried());
                if(filter != null)
                    this.object.liquidFilter.set(index - 1, filter);
            }
            this.object.dataChanged();
        }else if(index >= 10 && !this.getSlot(index).getItem().isEmpty() && this.getSlot(0).getItem().isEmpty() && this.getSlot(0).mayPlace(this.getSlot(index).getItem())){
            this.getSlot(0).set(this.getSlot(index).getItem());
            this.getSlot(index).set(ItemStack.EMPTY);
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
