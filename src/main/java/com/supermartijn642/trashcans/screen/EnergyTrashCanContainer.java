package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanContainer extends TrashCanContainer {

    public EnergyTrashCanContainer(int id, PlayerEntity player, BlockPos pos){
        super(TrashCans.energy_trash_can_container, id, player, pos, 202, 187);
    }

    @Override
    protected void addSlots(TrashCanTile tile, PlayerEntity player){
        this.addSlot(new SlotItemHandler(tile.ENERGY_ITEM_HANDLER, 0, 93, 25));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index){
        if(index == 0){
            if(this.mergeItemStack(this.getSlot(index).getStack(), 1, this.inventorySlots.size(), true))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }else if(!this.getSlot(index).getStack().isEmpty() && this.getSlot(0).getStack().isEmpty() && this.getSlot(0).isItemValid(this.getSlot(index).getStack())){
            TrashCanTile tile = this.getTileOrClose();
            if(tile != null){
                this.getSlot(0).putStack(this.getSlot(index).getStack());
                this.getSlot(index).putStack(ItemStack.EMPTY);
                tile.dataChanged();
            }
        }
        return ItemStack.EMPTY;
    }
}
