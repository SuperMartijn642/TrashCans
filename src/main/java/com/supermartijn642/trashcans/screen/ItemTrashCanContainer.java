package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanContainer extends TrashCanContainer {

    public ItemTrashCanContainer(Player player, BlockPos pos){
        super(TrashCans.item_trash_can_container, player, pos, 202, 180);
    }

    @Override
    protected void addSlots(Player player, TrashCanBlockEntity entity){
        this.addSlot(new DummySlot(0, 93, 25) {
            @Override
            public boolean mayPlace(ItemStack stack){
                return ItemTrashCanContainer.this.validateObjectOrClose() && ItemTrashCanContainer.this.object.isRegularItemValid(stack);
            }
        });

        // item filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(new DummySlot(slotIndex, 8 + column * 18, 64) {
                @Override
                public boolean mayPickup(Player playerIn){
                    return false;
                }

                @Override
                public ItemStack getItem(){
                    return ItemTrashCanContainer.this.validateObjectOrClose() ? ItemTrashCanContainer.this.object.itemFilter.get(slotIndex) : ItemStack.EMPTY;
                }
            });
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player){
        if(!this.validateObjectOrClose())
            return;

        if(slotId >= 1 && slotId <= 9){
            if(this.getCarried().isEmpty())
                this.object.itemFilter.set(slotId - 1, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.itemFilter.set(slotId - 1, stack);
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

        if(index >= 1 && index <= 9){
            if(this.getCarried().isEmpty())
                this.object.itemFilter.set(index - 1, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.itemFilter.set(index - 1, stack);
            }
            this.object.dataChanged();
        }else if(index >= 10 && this.getSlot(0).mayPlace(this.getSlot(index).getItem()))
            this.getSlot(index).set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }
}
