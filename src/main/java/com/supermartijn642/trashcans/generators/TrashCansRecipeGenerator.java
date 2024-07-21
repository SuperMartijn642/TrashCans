package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;
import net.neoforged.neoforge.common.Tags;

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
            .input('A', Tags.Items.STONES)
            .input('B', Tags.Items.COBBLESTONES)
            .input('C', Tags.Items.CHESTS_WOODEN)
            .unlockedBy(Tags.Items.CHESTS_WOODEN);
        this.shaped(TrashCans.liquid_trash_can)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', Tags.Items.STONES)
            .input('B', Tags.Items.COBBLESTONES)
            .input('C', Tags.Items.BUCKETS_EMPTY)
            .unlockedBy(Tags.Items.BUCKETS_EMPTY);
        this.shaped(TrashCans.energy_trash_can)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', Tags.Items.STONES)
            .input('B', Tags.Items.COBBLESTONES)
            .input('C', Tags.Items.DUSTS_REDSTONE)
            .unlockedBy(Tags.Items.DUSTS_REDSTONE);
        this.shapeless(TrashCans.ultimate_trash_can)
            .input(TrashCans.item_trash_can)
            .input(TrashCans.liquid_trash_can)
            .input(TrashCans.energy_trash_can)
            .unlockedBy(TrashCans.item_trash_can);
    }
}
