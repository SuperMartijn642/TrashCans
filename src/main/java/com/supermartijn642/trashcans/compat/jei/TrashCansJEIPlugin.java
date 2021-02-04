package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.screen.TrashCanScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class TrashCansJEIPlugin implements IModPlugin {

    @SuppressWarnings("unchecked")
    @Override
    public void register(IModRegistry registry){
        registry.addGhostIngredientHandler((Class)TrashCanScreen.class, new GhostIngredientHandler());
    }
}
