package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.PacketChangeItemFilter;
import com.supermartijn642.trashcans.packet.PacketChangeLiquidFilter;
import com.supermartijn642.trashcans.screen.ItemTrashCanScreen;
import com.supermartijn642.trashcans.screen.LiquidTrashCanScreen;
import com.supermartijn642.trashcans.screen.TrashCanContainer;
import com.supermartijn642.trashcans.screen.TrashCanScreen;
import com.supermartijn642.trashcans.screen.UltimateTrashCanScreen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class GhostIngredientHandler implements IGhostIngredientHandler<TrashCanScreen> {

    @Override
    public <I> List<Target<I>> getTargets(TrashCanScreen screen, I ingredient, boolean doStart) {
        // Sanity check. Doing this the proper way with generics seems like too much extra work.
        if (!(screen.getContainer() instanceof TrashCanContainer)) {
            return Collections.emptyList();
        }
        List<Target<I>> targets = new ArrayList<>();
        for (Slot slot : screen.getContainer().inventorySlots) {
            Rectangle2d bounds = new Rectangle2d(screen.getGuiLeft() + slot.xPos, screen.getGuiTop() + slot.yPos, 17, 17);

            boolean isItemFilterSlot = (screen instanceof ItemTrashCanScreen && slot.slotNumber >= 1 && slot.slotNumber <= 9) || (screen instanceof UltimateTrashCanScreen && slot.slotNumber >= 3 && slot.slotNumber <= 11);
            boolean isFluidFilterSlot = (screen instanceof LiquidTrashCanScreen && slot.slotNumber >= 1 && slot.slotNumber <= 9) || (screen instanceof UltimateTrashCanScreen && slot.slotNumber >= 12 && slot.slotNumber <= 20);

            if (isItemFilterSlot && ingredient instanceof ItemStack) {
                int filterSlotNum = screen instanceof ItemTrashCanScreen ? slot.slotNumber - 1 : slot.slotNumber - 3;
                targets.add(new Target<I>() {
                    @Override
                    public Rectangle2d getArea() {
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {
                        TrashCanTile tile = ((TrashCanContainer)screen.getContainer()).getTileOrClose();
                        if (tile != null) {
                            tile.itemFilter.set(filterSlotNum, (ItemStack)ingredient);
                            TrashCans.CHANNEL.sendToServer(new PacketChangeItemFilter(tile.getPos(), filterSlotNum, (ItemStack)ingredient));
                        }
                    }
                });
            } else if (isFluidFilterSlot) {
                ItemStack repitem = ItemStack.EMPTY;
                if (ingredient instanceof ItemStack && isValidFluidItem((ItemStack)ingredient)) {
                    repitem = (ItemStack)ingredient;
                } else if (ingredient instanceof FluidStack) {
                    repitem = new ItemStack(((FluidStack)ingredient).getFluid().getFilledBucket());
                } else if (Compatibility.MEKANISM.isGasStack(ingredient)) {
                    repitem = Compatibility.MEKANISM.getChemicalTankForGasStack(ingredient);
                }
                if (!repitem.isEmpty()) {
                    ItemStack finalrepitem = repitem;
                    int filterSlotNum = screen instanceof LiquidTrashCanScreen ? slot.slotNumber - 1 : slot.slotNumber - 12;
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle2d getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            ItemFilter filter = LiquidTrashCanFilters.createFilter(finalrepitem);
                            if (filter != null) {
                                TrashCanTile tile = ((TrashCanContainer)screen.getContainer()).getTileOrClose();
                                if (tile != null) {
                                    tile.liquidFilter.set(filterSlotNum, filter);
                                    TrashCans.CHANNEL.sendToServer(new PacketChangeLiquidFilter(tile.getPos(), filterSlotNum, filter));
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
    public void onComplete() {} // NO-OP

    private boolean isValidFluidItem(ItemStack stack) {
        if (Compatibility.MEKANISM.doesItemHaveGasStored(stack)) return true;
        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
        return fluidHandler != null && fluidHandler.getTanks() == 1 && !fluidHandler.getFluidInTank(0).isEmpty();
    }
}
