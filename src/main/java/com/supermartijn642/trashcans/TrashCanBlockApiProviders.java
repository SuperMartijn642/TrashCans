package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Consumer;

/**
 * Created 27/01/2023 by SuperMartijn642
 */
public class TrashCanBlockApiProviders {

    public static void register(){
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<RegisterCapabilitiesEvent>)event -> {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TrashCans.item_trash_can_tile, (entity, side) -> entity.itemHandler);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.itemHandler);
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TrashCans.liquid_trash_can_tile, (entity, side) -> entity.fluidHandler);
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.fluidHandler);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TrashCans.energy_trash_can_tile, (entity, side) -> entity.energyHandler);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.energyHandler);
            if(Compatibility.MEKANISM.isInstalled()){
                event.registerBlockEntity(Compatibility.MEKANISM.getGasHandlerCapability(), TrashCans.liquid_trash_can_tile, (entity, side) -> entity.gasHandler);
                event.registerBlockEntity(Compatibility.MEKANISM.getGasHandlerCapability(), TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.gasHandler);
            }
        });
    }
}
