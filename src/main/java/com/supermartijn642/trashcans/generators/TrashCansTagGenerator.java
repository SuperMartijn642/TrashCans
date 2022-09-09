package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.trashcans.TrashCans;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansTagGenerator extends TagGenerator {

    public TrashCansTagGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.blockMineableWithPickaxe()
            .add(TrashCans.item_trash_can)
            .add(TrashCans.liquid_trash_can)
            .add(TrashCans.energy_trash_can)
            .add(TrashCans.ultimate_trash_can);
    }
}
