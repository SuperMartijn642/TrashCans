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
public class UltimateTrashCanContainer extends TrashCanContainer {

    public UltimateTrashCanContainer(Player player, BlockPos pos){
        super(TrashCans.ultimate_trash_can_container, player, pos, 202, 247);
    }

    @Override
    protected void addSlots(Player player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.ITEM_HANDLER, 0, 63, 25));
        this.addSlot(new SlotItemHandler(entity.LIQUID_ITEM_HANDLER, 0, 93, 25));
        this.addSlot(new SlotItemHandler(entity.ENERGY_ITEM_HANDLER, 0, 123, 25));

        // item filter
        for(int column = 0; column < 9; column++)
            this.addSlot(new SlotItemHandler(this.itemFilterHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean mayPickup(Player playerIn){
                    return false;
                }
            });

        // liquid filter
        for(int column = 0; column < 9; column++)
            this.addSlot(new SlotItemHandler(this.liquidFilterHandler(), column, 8 + column * 18, 94) {
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

        if(slotId >= 3 && slotId <= 11){
            if(this.getCarried().isEmpty())
                this.object.itemFilter.set(slotId - 3, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.itemFilter.set(slotId - 3, stack);
            }
            this.object.dataChanged();
            return;
        }else if(slotId >= 12 && slotId <= 20){
            if(this.getCarried().isEmpty())
                this.object.liquidFilter.set(slotId - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.getCarried());
                if(filter != null)
                    this.object.liquidFilter.set(slotId - 12, filter);
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

        if(index == 1 || index == 2){
            if(this.moveItemStackTo(this.getSlot(index).getItem(), 21, this.slots.size(), true))
                this.getSlot(index).set(ItemStack.EMPTY);
        }else if(index >= 3 && index <= 11){
            if(this.getCarried().isEmpty())
                this.object.itemFilter.set(index - 3, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.itemFilter.set(index - 3, stack);
            }
            this.object.dataChanged();
        }else if(index >= 12 && index <= 20){
            if(this.getCarried().isEmpty())
                this.object.liquidFilter.set(index - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(this.getCarried());
                if(filter != null)
                    this.object.liquidFilter.set(index - 12, filter);
            }
            this.object.dataChanged();
        }else if(index >= 21 && !this.getSlot(index).getItem().isEmpty()){
            ItemStack stack = this.getSlot(index).getItem();
            if(this.getSlot(1).getItem().isEmpty() && this.getSlot(1).mayPlace(stack)){
                this.getSlot(1).set(stack);
                this.getSlot(index).set(ItemStack.EMPTY);
                this.object.dataChanged();
            }else if(this.getSlot(2).getItem().isEmpty() && this.getSlot(2).mayPlace(stack)){
                this.getSlot(2).set(stack);
                this.getSlot(index).set(ItemStack.EMPTY);
                this.object.dataChanged();
            }else if(this.getSlot(0).mayPlace(stack))
                this.getSlot(index).set(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    private IItemHandlerModifiable liquidFilterHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanBlockEntity entity = UltimateTrashCanContainer.this.object;
                return entity == null || entity.liquidFilter.get(slot) == null ? ItemStack.EMPTY : entity.liquidFilter.get(slot).getRepresentingItem();
            }
        };
    }

    private IItemHandlerModifiable itemFilterHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                return UltimateTrashCanContainer.this.validateObjectOrClose() ? UltimateTrashCanContainer.this.object.itemFilter.get(slot) : ItemStack.EMPTY;
            }
        };
    }
}
