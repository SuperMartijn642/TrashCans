package com.supermartijn642.trashcans;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created 27/01/2023 by SuperMartijn642
 */
public class TrashCanBlockApiProviders {

    public static void register(){
        ItemStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.itemHandler, TrashCans.item_trash_can_tile);
        ItemStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.itemHandler, TrashCans.ultimate_trash_can_tile);
        FluidStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.fluidHandler, TrashCans.liquid_trash_can_tile);
        FluidStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.fluidHandler, TrashCans.ultimate_trash_can_tile);
        EnergyStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.energyHandler, TrashCans.energy_trash_can_tile);
        EnergyStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.energyHandler, TrashCans.ultimate_trash_can_tile);
    }
}
