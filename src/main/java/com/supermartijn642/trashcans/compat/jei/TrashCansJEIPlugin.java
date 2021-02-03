package com.supermartijn642.trashcans.compat.jei;

import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.screen.TrashCanScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class TrashCansJEIPlugin implements IModPlugin {

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(TrashCanScreen.class, new GhostIngredientHandler());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TrashCans.MODID, "jei_plugin");
    }
}
