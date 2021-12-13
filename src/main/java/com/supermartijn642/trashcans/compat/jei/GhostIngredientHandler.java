package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.PacketChangeItemFilter;
import com.supermartijn642.trashcans.packet.PacketChangeLiquidFilter;
import com.supermartijn642.trashcans.screen.ItemTrashCanScreen;
import com.supermartijn642.trashcans.screen.LiquidTrashCanScreen;
import com.supermartijn642.trashcans.screen.TrashCanScreen;
import com.supermartijn642.trashcans.screen.UltimateTrashCanScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler implements IGhostIngredientHandler<TrashCanScreen<?>> {

    @Override
    public <I> List<Target<I>> getTargets(TrashCanScreen<?> screen, I ingredient, boolean doStart){
        List<Target<I>> targets = new ArrayList<>();
        for(Slot slot : screen.getMenu().slots){
            Rectangle2d bounds = new Rectangle2d(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 17, 17);

            boolean isItemFilterSlot = (screen instanceof ItemTrashCanScreen && slot.index >= 1 && slot.index <= 9) || (screen instanceof UltimateTrashCanScreen && slot.index >= 3 && slot.index <= 11);
            boolean isFluidFilterSlot = (screen instanceof LiquidTrashCanScreen && slot.index >= 1 && slot.index <= 9) || (screen instanceof UltimateTrashCanScreen && slot.index >= 12 && slot.index <= 20);

            if(isItemFilterSlot && ingredient instanceof ItemStack){
                int filterSlotNum = screen instanceof ItemTrashCanScreen ? slot.index - 1 : slot.index - 3;
                targets.add(new Target<I>() {
                    @Override
                    public Rectangle2d getArea(){
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient){
                        TrashCanTile tile = screen.getMenu().getObjectOrClose();
                        if(tile != null){
                            tile.itemFilter.set(filterSlotNum, (ItemStack)ingredient);
                            TrashCans.CHANNEL.sendToServer(new PacketChangeItemFilter(tile.getBlockPos(), filterSlotNum, (ItemStack)ingredient));
                        }
                    }
                });
            }else if(isFluidFilterSlot){
                ItemStack repitem = ItemStack.EMPTY;
                if(ingredient instanceof ItemStack && isValidFluidItem((ItemStack)ingredient)){
                    repitem = (ItemStack)ingredient;
                }else if(ingredient instanceof FluidStack){
                    repitem = new ItemStack(((FluidStack)ingredient).getFluid().getBucket());
                }
                if(!repitem.isEmpty()){
                    ItemStack finalrepitem = repitem;
                    int filterSlotNum = screen instanceof LiquidTrashCanScreen ? slot.index - 1 : slot.index - 12;
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle2d getArea(){
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient){
                            ItemFilter filter = LiquidTrashCanFilters.createFilter(finalrepitem);
                            if(filter != null){
                                TrashCanTile tile = screen.getMenu().getObjectOrClose();
                                if(tile != null){
                                    tile.liquidFilter.set(filterSlotNum, filter);
                                    TrashCans.CHANNEL.sendToServer(new PacketChangeLiquidFilter(tile.getBlockPos(), filterSlotNum, filter));
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
        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
        return fluidHandler != null && fluidHandler.getTanks() == 1 && !fluidHandler.getFluidInTank(0).isEmpty();
    }
}
