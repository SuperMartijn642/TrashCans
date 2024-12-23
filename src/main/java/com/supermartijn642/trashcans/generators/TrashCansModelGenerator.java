package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansModelGenerator extends ModelGenerator {

    public TrashCansModelGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.model("item_trash_can").parent("trash_can").texture("all", "trash_can_items");
        this.model("liquid_trash_can").parent("trash_can").texture("all", "trash_can_liquids");
        this.model("energy_trash_can").parent("trash_can").texture("all", "trash_can_energy");
        this.model("ultimate_trash_can").parent("trash_can").texture("all", "trash_can_ultimate");
    }
}
