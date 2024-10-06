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
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TrashCans.item_trash_can_tile, (entity, side) -> entity.ITEM_HANDLER);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.ITEM_HANDLER);
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TrashCans.liquid_trash_can_tile, (entity, side) -> entity.FLUID_HANDLER);
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.FLUID_HANDLER);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TrashCans.energy_trash_can_tile, (entity, side) -> entity.ENERGY_STORAGE);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TrashCans.ultimate_trash_can_tile, (entity, side) -> entity.ENERGY_STORAGE);
            if(Compatibility.MEKANISM.isInstalled()){
                event.registerBlockEntity(Compatibility.MEKANISM.getGasHandlerCapability(), TrashCans.liquid_trash_can_tile, (entity, side) -> Compatibility.MEKANISM.getGasHandler(entity.liquidFilter, () -> entity.liquidFilterWhitelist));
                event.registerBlockEntity(Compatibility.MEKANISM.getGasHandlerCapability(), TrashCans.ultimate_trash_can_tile, (entity, side) -> Compatibility.MEKANISM.getGasHandler(entity.liquidFilter, () -> entity.liquidFilterWhitelist));
            }
        });
    }
}
