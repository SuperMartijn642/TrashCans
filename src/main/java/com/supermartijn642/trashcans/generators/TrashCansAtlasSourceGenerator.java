package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.AtlasSourceGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.screen.*;

/**
 * Created 30/06/2025 by SuperMartijn642
 */
public class TrashCansAtlasSourceGenerator extends AtlasSourceGenerator {

    public TrashCansAtlasSourceGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.guiAtlas()
            .texture(EnergyTrashCanScreen.BACKGROUND)
            .texture(ItemTrashCanScreen.BACKGROUND)
            .texture(LiquidTrashCanScreen.BACKGROUND)
            .texture(UltimateTrashCanScreen.BACKGROUND)
            .texture(ArrowButton.BUTTONS)
            .texture(CheckBox.BUTTONS)
            .texture(WhitelistButton.BUTTONS);
    }
}
