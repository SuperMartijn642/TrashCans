package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.LootTableGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansLootTableGenerator extends LootTableGenerator {

    public TrashCansLootTableGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.dropSelf(TrashCans.item_trash_can);
        this.dropSelf(TrashCans.liquid_trash_can);
        this.dropSelf(TrashCans.energy_trash_can);
        this.dropSelf(TrashCans.ultimate_trash_can);
    }
}
