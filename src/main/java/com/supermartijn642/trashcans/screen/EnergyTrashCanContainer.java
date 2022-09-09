package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanContainer extends TrashCanContainer {

    public EnergyTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(TrashCans.energy_trash_can_container, player, pos, 202, 187);
    }

    @Override
    protected void addSlots(EntityPlayer player, TrashCanBlockEntity entity){
        this.addSlot(new SlotItemHandler(entity.ENERGY_ITEM_HANDLER, 0, 93, 25));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        if(!this.validateObjectOrClose())
            return ItemStack.EMPTY;

        if(index == 0){
            if(this.mergeItemStack(this.getSlot(index).getStack(), 1, this.inventorySlots.size(), true))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }else if(!this.getSlot(index).getStack().isEmpty() && this.getSlot(0).getStack().isEmpty() && this.getSlot(0).isItemValid(this.getSlot(index).getStack())){
            this.getSlot(0).putStack(this.getSlot(index).getStack());
            this.getSlot(index).putStack(ItemStack.EMPTY);
            this.object.dataChanged();
        }
        return ItemStack.EMPTY;
    }
}
