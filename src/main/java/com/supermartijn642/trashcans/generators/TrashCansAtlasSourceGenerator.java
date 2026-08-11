package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.AtlasSourceGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.screen.EnergyTrashCanScreen;
import com.supermartijn642.trashcans.screen.ItemTrashCanScreen;
import com.supermartijn642.trashcans.screen.LiquidTrashCanScreen;
import com.supermartijn642.trashcans.screen.UltimateTrashCanScreen;
import com.supermartijn642.trashcans.screen.components.ArrowButton;
import com.supermartijn642.trashcans.screen.components.CheckBox;
import com.supermartijn642.trashcans.screen.components.DeletedItemsList;
import com.supermartijn642.trashcans.screen.components.WhitelistButton;

/**
 * Created 30/06/2025 by SuperMartijn642
 */
public class TrashCansAtlasSourceGenerator extends AtlasSourceGenerator {

    public TrashCansAtlasSourceGenerator(ResourceCache cache){
        super(TrashCans.MODID, cache);
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
            .texture(WhitelistButton.BUTTONS)
            .texture(DeletedItemsList.TOGGLE_TAB);
    }
}
