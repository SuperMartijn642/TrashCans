package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.PacketChangeItemFilter;
import com.supermartijn642.trashcans.packet.PacketChangeLiquidFilter;
import com.supermartijn642.trashcans.screen.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GhostIngredientHandler implements IGhostIngredientHandler<TrashCanWidgetContainerScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(TrashCanWidgetContainerScreen screen, ITypedIngredient<I> ingredient, boolean doStart){
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
        if(ingredient.getType() == VanillaTypes.ITEM_STACK){
            for(int i = 0; i < itemFilterSlots.size(); i++){
                int index = i;
                Slot slot = itemFilterSlots.get(i);
                Rect2i bounds = new Rect2i(screen.getWidget().left() + slot.x, screen.getWidget().top() + slot.y, 17, 17);

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
        if(ingredient.getType() == VanillaTypes.ITEM_STACK)
            ingredientStack = ingredient.getIngredient(VanillaTypes.ITEM_STACK).get();
        else if(ingredient.getType() == ForgeTypes.FLUID_STACK)
            ingredientStack = ingredient.getIngredient(ForgeTypes.FLUID_STACK).get().getFluid().getBucket().getDefaultInstance();
        else if(Compatibility.MEKANISM.isGasStack(ingredient.getIngredient()))
            ingredientStack = Compatibility.MEKANISM.getChemicalTankForGasStack(ingredient.getIngredient());

        // Check whether the ingredient is applicable to a fluid filter
        ItemFilter filter = LiquidTrashCanFilters.createFilter(ingredientStack);
        if(filter != null){
            // Add a target for each fluid filter slot
            for(int i = 0; i < fluidFilterSlots.size(); i++){
                int index = i;
                Slot slot = fluidFilterSlots.get(i);
                Rect2i bounds = new Rect2i(screen.getWidget().left() + slot.x, screen.getWidget().top() + slot.y, 17, 17);

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

    private static <I> Target<I> createTarget(Rect2i bounds, Consumer<I> acceptor){
        return new Target<>() {
            @Override
            public Rect2i getArea(){
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
