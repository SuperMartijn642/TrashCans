package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansRecipeGenerator extends RecipeGenerator {

    public TrashCansRecipeGenerator(ResourceCache cache){
        super("trashcans", cache);
    }

    @Override
    public void generate(){
        this.shaped(ItemBlock.getItemFromBlock(TrashCans.item_trash_can))
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ItemBlock.getItemFromBlock(Blocks.STONE))
            .input('B', "cobblestone")
            .input('C', "chestWood")
            .unlockedByOreDict("chestWood");
        this.shaped(ItemBlock.getItemFromBlock(TrashCans.liquid_trash_can))
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ItemBlock.getItemFromBlock(Blocks.STONE))
            .input('B', "cobblestone")
            .input('C', Items.BUCKET)
            .unlockedBy(Items.BUCKET);
        this.shaped(ItemBlock.getItemFromBlock(TrashCans.energy_trash_can))
            .pattern("AAA")
            .pattern("BCB")
            .pattern("BBB")
            .input('A', ItemBlock.getItemFromBlock(Blocks.STONE))
            .input('B', "cobblestone")
            .input('C', "dustRedstone")
            .unlockedByOreDict("dustRedstone");
        this.shapeless(ItemBlock.getItemFromBlock(TrashCans.ultimate_trash_can))
            .input(ItemBlock.getItemFromBlock(TrashCans.item_trash_can))
            .input(ItemBlock.getItemFromBlock(TrashCans.liquid_trash_can))
            .input(ItemBlock.getItemFromBlock(TrashCans.energy_trash_can))
            .unlockedBy(ItemBlock.getItemFromBlock(TrashCans.item_trash_can));
    }
}
