package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansRecipeGenerator extends RecipeGenerator {

    public TrashCansRecipeGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.shaped(TrashCans.item_trash_can)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ConventionalItemTags.STONES)
            .input('B', ConventionalItemTags.COBBLESTONES)
            .input('C', ConventionalItemTags.WOODEN_CHESTS)
            .unlockedBy(ConventionalItemTags.WOODEN_CHESTS);
        this.shaped(TrashCans.liquid_trash_can)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ConventionalItemTags.STONES)
            .input('B', ConventionalItemTags.COBBLESTONES)
            .input('C', ConventionalItemTags.EMPTY_BUCKETS)
            .unlockedBy(ConventionalItemTags.EMPTY_BUCKETS);
        this.shaped(TrashCans.energy_trash_can)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ConventionalItemTags.STONES)
            .input('B', ConventionalItemTags.COBBLESTONES)
            .input('C', ConventionalItemTags.REDSTONE_DUSTS)
            .unlockedBy(ConventionalItemTags.REDSTONE_DUSTS);
        this.shapeless(TrashCans.ultimate_trash_can)
            .input(TrashCans.item_trash_can)
            .input(TrashCans.liquid_trash_can)
            .input(TrashCans.energy_trash_can)
            .unlockedBy(TrashCans.item_trash_can);
    }
}
