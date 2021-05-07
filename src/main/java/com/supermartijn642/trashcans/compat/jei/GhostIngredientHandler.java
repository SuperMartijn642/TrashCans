package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.PacketChangeItemFilter;
import com.supermartijn642.trashcans.packet.PacketChangeLiquidFilter;
import com.supermartijn642.trashcans.screen.*;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler implements IGhostIngredientHandler<TrashCanScreen<?>> {

    @Override
    public <I> List<Target<I>> getTargets(TrashCanScreen<?> screen, I ingredient, boolean doStart){
        List<Target<I>> targets = new ArrayList<>();
        for(Slot slot : screen.inventorySlots.inventorySlots){
            Rectangle bounds = new Rectangle(screen.getGuiLeft() + slot.xPos, screen.getGuiTop() + slot.yPos, 17, 17);

            boolean isItemFilterSlot = (screen instanceof ItemTrashCanScreen && slot.slotNumber >= 1 && slot.slotNumber <= 9) || (screen instanceof UltimateTrashCanScreen && slot.slotNumber >= 3 && slot.slotNumber <= 11);
            boolean isFluidFilterSlot = (screen instanceof LiquidTrashCanScreen && slot.slotNumber >= 1 && slot.slotNumber <= 9) || (screen instanceof UltimateTrashCanScreen && slot.slotNumber >= 12 && slot.slotNumber <= 20);

            if(isItemFilterSlot && ingredient instanceof ItemStack){
                int filterSlotNum = screen instanceof ItemTrashCanScreen ? slot.slotNumber - 1 : slot.slotNumber - 3;
                targets.add(new Target<I>() {
                    @Override
                    public Rectangle getArea(){
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient){
                        TrashCanTile tile = ((TrashCanContainer)screen.inventorySlots).getObjectOrClose();
                        if(tile != null){
                            tile.itemFilter.set(filterSlotNum, (ItemStack)ingredient);
                            TrashCans.channel.sendToServer(new PacketChangeItemFilter(tile.getPos(), filterSlotNum, (ItemStack)ingredient));
                        }
                    }
                });
            }else if(isFluidFilterSlot){
                ItemStack repitem = ItemStack.EMPTY;
                if(ingredient instanceof ItemStack){
                    repitem = (ItemStack)ingredient;
                }else if(ingredient instanceof FluidStack){
                    repitem = FluidUtil.getFilledBucket((FluidStack)ingredient);
                }else if(Compatibility.MEKANISM.isGasStack(ingredient)){
                    repitem = Compatibility.MEKANISM.getChemicalTankForGasStack(ingredient);
                }
                if(!repitem.isEmpty()){
                    ItemStack finalrepitem = repitem;
                    int filterSlotNum = screen instanceof LiquidTrashCanScreen ? slot.slotNumber - 1 : slot.slotNumber - 12;
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle getArea(){
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient){
                            ItemFilter filter = LiquidTrashCanFilters.createFilter(finalrepitem);
                            if(filter != null){
                                TrashCanTile tile = ((TrashCanContainer)screen.inventorySlots).getObjectOrClose();
                                if(tile != null){
                                    tile.liquidFilter.set(filterSlotNum, filter);
                                    TrashCans.channel.sendToServer(new PacketChangeLiquidFilter(tile.getPos(), filterSlotNum, filter));
                                }
                            }
                        }
                    });
                }
            }
        }
        return targets;
    }

    @Override
    public void onComplete(){
    } // NO-OP

    private boolean isValidFluidItem(ItemStack stack){
        if(Compatibility.MEKANISM.doesItemHaveGasStored(stack)) return true;
        return getFluid(stack) != null;
    }

    private static FluidStack getFluid(ItemStack stack){
        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return null;
        IFluidHandler fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return fluidHandler == null || fluidHandler.getTankProperties() == null || fluidHandler.getTankProperties().length != 1 ||
            fluidHandler.getTankProperties()[0].getContents() == null || fluidHandler.getTankProperties()[0].getContents().amount == 0
            ? null : fluidHandler.getTankProperties()[0].getContents();
    }
}
