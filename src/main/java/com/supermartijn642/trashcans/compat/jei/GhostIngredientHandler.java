package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GhostIngredientHandler implements IGhostIngredientHandler<TrashCanWidgetContainerScreen> {

    @Override
    public <I> List<Target<I>> getTargets(TrashCanWidgetContainerScreen screen, I ingredient, boolean doStart){
        // Get the widget and container from the screen
        TrashCanScreen<?> widget = screen.getWidget();
        TrashCanContainer container = screen.getContainer();

        // Get the slots for the item and fluid filters
        List<Slot> itemFilterSlots = new ArrayList<>(), fluidFilterSlots = new ArrayList<>();
        if(widget instanceof ItemTrashCanScreen){
            for(int slot = 1; slot <= 9; slot++)
                itemFilterSlots.add(container.getSlot(slot));
        }else if(widget instanceof LiquidTrashCanScreen){
            for(int slot = 1; slot <= 9; slot++)
                fluidFilterSlots.add(container.getSlot(slot));
        }else if(widget instanceof UltimateTrashCanScreen){
            for(int slot = 3; slot <= 11; slot++)
                itemFilterSlots.add(container.getSlot(slot));
            for(int slot = 12; slot <= 20; slot++)
                fluidFilterSlots.add(container.getSlot(slot));
        }

        // Now create all the targets
        List<Target<I>> targets = new ArrayList<>();

        // Add a target for each item filter slot
        if(ingredient instanceof ItemStack){
            for(int i = 0; i < itemFilterSlots.size(); i++){
                int index = i;
                Slot slot = itemFilterSlots.get(i);
                Rectangle bounds = new Rectangle(screen.getGuiLeft() + slot.xPos, screen.getGuiTop() + slot.yPos, 17, 17);

                // Create the target
                Target<I> target = createTarget(bounds, input -> {
                    TrashCanBlockEntity entity = container.getBlockEntity();
                    if(entity != null){
                        entity.itemFilter.set(index, (ItemStack)input);
                        TrashCans.CHANNEL.sendToServer(new PacketChangeItemFilter(container.getBlockEntityPos(), index, (ItemStack)input));
                    }
                });
                targets.add(target);
            }
        }

        // Get an item stack as representation for the ingredient
        ItemStack ingredientStack = ItemStack.EMPTY;
        if(ingredient instanceof ItemStack)
            ingredientStack = (ItemStack)ingredient;
        else if(ingredient instanceof FluidStack)
            ingredientStack = FluidUtil.getFilledBucket((FluidStack)ingredient);
        else if(Compatibility.MEKANISM.isGasStack(ingredient))
            ingredientStack = Compatibility.MEKANISM.getChemicalTankForGasStack(ingredient);

        // Check whether the ingredient is applicable to a fluid filter
        ItemFilter filter = LiquidTrashCanFilters.createFilter(ingredientStack);
        if(filter != null){
            // Add a target for each fluid filter slot
            for(int i = 0; i < fluidFilterSlots.size(); i++){
                int index = i;
                Slot slot = fluidFilterSlots.get(i);
                Rectangle bounds = new Rectangle(screen.getGuiLeft() + slot.xPos, screen.getGuiTop() + slot.yPos, 17, 17);

                // Create the target
                Target<I> target = createTarget(bounds, input -> {
                    TrashCanBlockEntity entity = container.getBlockEntity();
                    if(entity != null){
                        entity.liquidFilter.set(index, filter);
                        TrashCans.CHANNEL.sendToServer(new PacketChangeLiquidFilter(container.getBlockEntityPos(), index, filter));
                    }
                });
                targets.add(target);
            }
        }

        // Finally, return all created targets
        return targets;
    }

    private static <I> Target<I> createTarget(Rectangle bounds, Consumer<I> acceptor){
        return new Target<I>() {
            @Override
            public Rectangle getArea(){
                return bounds;
            }

            @Override
            public void accept(I ingredient){
                acceptor.accept(ingredient);
            }
        };
    }

    @Override
    public void onComplete(){
    }
}
