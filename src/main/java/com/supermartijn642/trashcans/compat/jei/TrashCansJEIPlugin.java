package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.screen.TrashCanWidgetContainerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class TrashCansJEIPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registration){
        registration.addGhostIngredientHandler(TrashCanWidgetContainerScreen.class, new GhostIngredientHandler());
    }
}
