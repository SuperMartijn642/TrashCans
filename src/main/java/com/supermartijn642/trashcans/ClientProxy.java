package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.screen.*;
import net.minecraft.client.gui.ScreenManager;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ClientProxy {

    public static void registerScreen(){
        ScreenManager.register(TrashCans.item_trash_can_container, (ScreenManager.IScreenFactory<ItemTrashCanContainer,ItemTrashCanScreen>)((container, player, title) -> new ItemTrashCanScreen(container)));
        ScreenManager.register(TrashCans.liquid_trash_can_container, (ScreenManager.IScreenFactory<LiquidTrashCanContainer,LiquidTrashCanScreen>)((container, player, title) -> new LiquidTrashCanScreen(container)));
        ScreenManager.register(TrashCans.energy_trash_can_container, (ScreenManager.IScreenFactory<EnergyTrashCanContainer,EnergyTrashCanScreen>)((container, player, title) -> new EnergyTrashCanScreen(container)));
        ScreenManager.register(TrashCans.ultimate_trash_can_container, (ScreenManager.IScreenFactory<UltimateTrashCanContainer,UltimateTrashCanScreen>)((container, player, title) -> new UltimateTrashCanScreen(container)));
    }

}
