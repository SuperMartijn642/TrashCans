package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.ItemInfoGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;

/**
 * Created 23/12/2024 by SuperMartijn642
 */
public class TrashCansItemInfoGenerator extends ItemInfoGenerator {

    public TrashCansItemInfoGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.simpleInfo(TrashCans.item_trash_can, "item_trash_can");
        this.simpleInfo(TrashCans.liquid_trash_can, "liquid_trash_can");
        this.simpleInfo(TrashCans.energy_trash_can, "energy_trash_can");
        this.simpleInfo(TrashCans.ultimate_trash_can, "ultimate_trash_can");
    }
}
