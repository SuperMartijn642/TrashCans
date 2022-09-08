package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.AdvancementGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansAdvancementGenerator extends AdvancementGenerator {

    public TrashCansAdvancementGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.advancement("trash_can")
            .icon(TrashCans.item_trash_can)
            .background("minecraft", "block/cobblestone")
            .hasItemsCriterion("has_item_trash_can", TrashCans.item_trash_can)
            .hasItemsCriterion("has_liquid_trash_can", TrashCans.liquid_trash_can)
            .hasItemsCriterion("has_energy_trash_can", TrashCans.energy_trash_can)
            .requirementGroup("has_item_trash_can", "has_liquid_trash_can", "has_energy_trash_can");
        this.advancement("ultimate_trash_can")
            .parent("trash_can")
            .icon(TrashCans.ultimate_trash_can)
            .hasItemsCriterion("has_ultimate_trash_can", TrashCans.ultimate_trash_can);
    }
}
