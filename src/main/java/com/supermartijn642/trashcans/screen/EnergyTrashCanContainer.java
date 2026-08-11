package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCanResourceHandlers;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanContainer extends TrashCanContainer {

    public EnergyTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(TrashCans.energy_trash_can_container, player, pos, 202, 187);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanBlockEntity entity){
        this.addSlot(
            CustomSlot.builder()
                .position(93, 25)
                .getter(() -> this.object.getEnergyItem())
                .setter(s -> this.object.setEnergyItem(s))
                .filter(TrashCanResourceHandlers::doesItemContainEnergy)
                .inserter(s -> {
                    int inserted = Math.min(s.getCount(), s.getMaxStackSize());
                    s = s.copy();
                    s.setCount(inserted);
                    this.object.setEnergyItem(s);
                    return inserted;
                })
                .extractor(amount -> {
                    ItemStack stack = this.object.getEnergyItem();
                    amount = Math.min(amount, stack.getCount());
                    if(amount <= 0)
                        return ItemStack.EMPTY;
                    ItemStack extractedStack = stack.copy();
                    extractedStack.setCount(amount);
                    stack = stack.copy();
                    stack.shrink(amount);
                    this.object.setEnergyItem(stack);
                    return extractedStack;
                })
                .build().getVanillaSlot()
        );
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 0){
            ItemStack stack = this.object.getEnergyItem().copy();
            if(this.mergeItemStack(stack, 1, this.inventorySlots.size(), true))
                this.object.setEnergyItem(stack);
        }else{
            Slot slot = this.getSlot(index);
            ItemStack stack = slot.getStack().copy();
            if(this.mergeItemStack(stack, 0, 1, false))
                slot.putStack(stack);
        }
        return ItemStack.EMPTY;
    }
}
