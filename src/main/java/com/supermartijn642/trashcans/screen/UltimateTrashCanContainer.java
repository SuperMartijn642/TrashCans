package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanContainer extends TrashCanContainer {

    public UltimateTrashCanContainer(EntityPlayer player, BlockPos pos){
        super(player, pos, 202, 247);
    }

    @Override
    protected void addSlots(TrashCanTile tile, EntityPlayer player){
        this.addSlotToContainer(new SlotItemHandler(tile.ITEM_HANDLER, 0, 63, 25));
        this.addSlotToContainer(new SlotItemHandler(tile.LIQUID_ITEM_HANDLER, 0, 93, 25));
        this.addSlotToContainer(new SlotItemHandler(tile.ENERGY_ITEM_HANDLER, 0, 123, 25));

        // item filter
        for(int column = 0; column < 9; column++)
            this.addSlotToContainer(new SlotItemHandler(this.itemFilterHandler(), column, 8 + column * 18, 64) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn){
                    return false;
                }
            });

        // liquid filter
        for(int column = 0; column < 9; column++)
            this.addSlotToContainer(new SlotItemHandler(this.liquidFilterHandler(), column, 8 + column * 18, 94) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn){
                    return false;
                }
            });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(slotId >= 3 && slotId <= 11){
            TrashCanTile tile = this.getTileOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.itemFilter.set(slotId - 3, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getItemStack().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(slotId - 3, stack);
                }
                tile.dataChanged();
            }
            return ItemStack.EMPTY;
        }else if(slotId >= 12 && slotId <= 20){
            TrashCanTile tile = this.getTileOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.liquidFilter.set(slotId - 12, null);
                else{
                    FluidStack fluid = LiquidTrashCanContainer.getFluid(player.inventory.getItemStack());
                    if(fluid != null)
                        tile.liquidFilter.set(slotId - 12, fluid.copy());
                }
                tile.dataChanged();
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        if(index == 1 || index == 2){
            if(this.mergeItemStack(this.getSlot(index).getStack(), 21, this.inventorySlots.size(), true))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }else if(index >= 3 && index <= 11){
            TrashCanTile tile = this.getTileOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.itemFilter.set(index - 3, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getItemStack().copy();
                    stack.setCount(1);
                    tile.itemFilter.set(index - 3, stack);
                }
                tile.dataChanged();
            }
        }else if(index >= 12 && index <= 20){
            TrashCanTile tile = this.getTileOrClose();
            if(tile != null){
                if(player.inventory.getItemStack().isEmpty())
                    tile.liquidFilter.set(index - 12, null);
                else{
                    FluidStack fluid = LiquidTrashCanContainer.getFluid(player.inventory.getItemStack());
                    if(fluid != null)
                        tile.liquidFilter.set(index - 12, fluid.copy());
                }
                tile.dataChanged();
            }
        }else if(index >= 21 && !this.getSlot(index).getStack().isEmpty()){
            ItemStack stack = this.getSlot(index).getStack();
            if(hasFluidHandler(stack)){
                if(this.getSlot(1).getStack().isEmpty() && this.getSlot(1).isItemValid(stack)){
                    TrashCanTile tile = this.getTileOrClose();
                    if(tile != null){
                        this.getSlot(1).putStack(stack);
                        this.getSlot(index).putStack(ItemStack.EMPTY);
                        tile.dataChanged();
                    }
                }
            }else if(hasEnergyHandler(stack)){
                if(this.getSlot(2).getStack().isEmpty() && this.getSlot(2).isItemValid(stack)){
                    TrashCanTile tile = this.getTileOrClose();
                    if(tile != null){
                        this.getSlot(2).putStack(stack);
                        this.getSlot(index).putStack(ItemStack.EMPTY);
                        tile.dataChanged();
                    }
                }
            }else if(this.getSlot(0).isItemValid(stack))
                this.getSlot(index).putStack(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    private static boolean hasFluidHandler(ItemStack stack){
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    private static boolean hasEnergyHandler(ItemStack stack){
        return stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    private IItemHandlerModifiable liquidFilterHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanTile tile = UltimateTrashCanContainer.this.getTileOrClose();
                return tile == null ? ItemStack.EMPTY : FluidUtil.getFilledBucket(tile.liquidFilter.get(slot));
            }
        };
    }

    private IItemHandlerModifiable itemFilterHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                TrashCanTile tile = UltimateTrashCanContainer.this.getTileOrClose();
                return tile == null ? ItemStack.EMPTY : tile.itemFilter.get(slot);
            }
        };
    }
}
