package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ClientProxy {

    public static void registerScreen(){
        MenuScreens.register(TrashCans.item_trash_can_container, (MenuScreens.ScreenConstructor<ItemTrashCanContainer,ItemTrashCanScreen>)((container, player, title) -> new ItemTrashCanScreen(container)));
        MenuScreens.register(TrashCans.liquid_trash_can_container, (MenuScreens.ScreenConstructor<LiquidTrashCanContainer,LiquidTrashCanScreen>)((container, player, title) -> new LiquidTrashCanScreen(container)));
        MenuScreens.register(TrashCans.energy_trash_can_container, (MenuScreens.ScreenConstructor<EnergyTrashCanContainer,EnergyTrashCanScreen>)((container, player, title) -> new EnergyTrashCanScreen(container)));
        MenuScreens.register(TrashCans.ultimate_trash_can_container, (MenuScreens.ScreenConstructor<UltimateTrashCanContainer,UltimateTrashCanScreen>)((container, player, title) -> new UltimateTrashCanScreen(container)));
    }

}
