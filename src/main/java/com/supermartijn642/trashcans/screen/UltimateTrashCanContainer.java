package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCanResourceHandlers;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanContainer extends TrashCanContainer {

    public UltimateTrashCanContainer(Player player, BlockPos pos){
        super(TrashCans.ultimate_trash_can_container, player, pos, 202, 247);
    }

    @Override
    protected void addSlots(Player player, TrashCanBlockEntity entity){
        // Items
        this.addSlot(
            CustomSlot.builder()
                .position(63, 25)
                .filter(s -> this.object.matchesItemFilter(s))
                .inserter(ItemStack::getCount)
                .canExtract(false)
                .build().getVanillaSlot()
        );
        // Fluids
        this.addSlot(
            CustomSlot.builder()
                .position(93, 25)
                .getter(() -> this.object.getFluidItem())
                .setter(s -> this.object.setFluidItem(s))
                .filter(s -> this.object.matchesFluidFilter(s) && TrashCanResourceHandlers.doesItemContainFluid(s))
                .inserter(s -> {
                    int inserted = Math.min(s.getCount(), s.getMaxStackSize());
                    this.object.setFluidItem(s.copyWithCount(inserted));
                    return inserted;
                })
                .extractor(amount -> {
                    ItemStack stack = this.object.getFluidItem();
                    amount = Math.min(amount, stack.getCount());
                    if(amount <= 0)
                        return ItemStack.EMPTY;
                    this.object.setFluidItem(stack.copyWithCount(stack.getCount() - amount));
                    return stack.copyWithCount(amount);
                })
                .build().getVanillaSlot()
        );
        // Energy
        this.addSlot(
            CustomSlot.builder()
                .position(123, 25)
                .getter(() -> this.object.getEnergyItem())
                .setter(s -> this.object.setEnergyItem(s))
                .filter(TrashCanResourceHandlers::doesItemContainEnergy)
                .inserter(s -> {
                    int inserted = Math.min(s.getCount(), s.getMaxStackSize());
                    this.object.setEnergyItem(s.copyWithCount(inserted));
                    return inserted;
                })
                .extractor(amount -> {
                    ItemStack stack = this.object.getEnergyItem();
                    amount = Math.min(amount, stack.getCount());
                    if(amount <= 0)
                        return ItemStack.EMPTY;
                    this.object.setEnergyItem(stack.copyWithCount(stack.getCount() - amount));
                    return stack.copyWithCount(amount);
                })
                .build().getVanillaSlot()
        );

        // Item filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(
                CustomSlot.builder()
                    .position(8 + column * 18, 64)
                    .getter(() -> this.object.getItemFilter(slotIndex))
                    .canInsertExtract(false)
                    .build().getVanillaSlot()
            );
        }
        // Fluid filter
        for(int column = 0; column < 9; column++){
            int slotIndex = column;
            this.addSlot(
                CustomSlot.builder()
                    .position(8 + column * 18, 94)
                    .getter(() -> {
                        ItemFilter filter = this.object.getFluidFilter(slotIndex);
                        return filter == null ? ItemStack.EMPTY : filter.getRepresentingItem();
                    })
                    .canInsertExtract(false)
                    .build().getVanillaSlot()
            );
        }
    }

    @Override
    public void clicked(int index, int dragType, ClickType clickType, Player player){
        if(!this.validateObjectOrClose())
            return;

        if(index >= 3 && index <= 11){
            this.object.setItemFilter(index - 3, this.getCarried());
            return;
        }else if(index >= 12 && index <= 20){
            ItemStack carried = this.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 12, filter);
            }
            return;
        }
        super.clicked(index, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 1){
            ItemStack stack = this.object.getFluidItem().copy();
            if(this.moveItemStackTo(stack, 21, this.slots.size(), true))
                this.object.setFluidItem(stack);
        }else if(index == 2){
            ItemStack stack = this.object.getEnergyItem().copy();
            if(this.moveItemStackTo(stack, 21, this.slots.size(), true))
                this.object.setEnergyItem(stack);
        }else if(index >= 3 && index <= 11)
            this.object.setItemFilter(index - 3, this.getCarried());
        else if(index >= 12 && index <= 20){
            ItemStack carried = this.getCarried();
            if(carried.isEmpty())
                this.object.setFluidFilter(index - 12, null);
            else{
                ItemFilter filter = LiquidTrashCanFilters.createFilter(carried);
                if(filter != null)
                    this.object.setFluidFilter(index - 12, filter);
            }
        }else if(index >= 21 && !this.getSlot(index).getItem().isEmpty()){
            Slot slot = this.getSlot(index);
            ItemStack stack = slot.getItem().copy();
            if(TrashCanResourceHandlers.doesItemHaveFluidHandler(stack)){
                if(this.moveItemStackTo(stack, 1, 2, false))
                    slot.set(stack);
            }else if(TrashCanResourceHandlers.doesItemHaveEnergyHandler(stack)){
                if(this.moveItemStackTo(stack, 2, 3, false))
                    slot.set(stack);
            }else{
                if(this.object.matchesItemFilter(stack))
                    slot.set(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }
}
