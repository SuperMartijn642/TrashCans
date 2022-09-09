package com.supermartijn642.trashcans.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.trashcans.TrashCans;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCansLanguageGenerator extends LanguageGenerator {

    public TrashCansLanguageGenerator(ResourceCache cache){
        super("trashcans", cache, "en_us");
    }

    @Override
    public void generate(){
        // Blocks
        this.block(TrashCans.item_trash_can, "Item Trash Can");
        this.block(TrashCans.liquid_trash_can, "Fluid Trash Can");
        this.block(TrashCans.energy_trash_can, "Energy Trash Can");
        this.block(TrashCans.ultimate_trash_can, "Ultimate Trash Can");

        // Block tooltips
        this.translation("trashcans.item_trash_can.info", "Can void items, also contains a filter for up to 9 items");
        this.translation("trashcans.liquid_trash_can.info", "Can void liquids and gasses, also contains a filter for up to 9 fluids");
        this.translation("trashcans.energy_trash_can.info", "Can void energy, can also limit the energy transfer rate");
        this.translation("trashcans.ultimate_trash_can.info", "Can void items, fluids, and energy");

        // Advancements
        this.translation("trashcans.advancement.trash_can.title", "Don't forget to recycle");
        this.translation("trashcans.advancement.trash_can.description", "Craft a trash can");
        this.translation("trashcans.advancement.ultimate_trash_can.title", "The Ultimate Trash Can");
        this.translation("trashcans.advancement.ultimate_trash_can.description", "Craft an ultimate trash can");

        // Screen
        this.translation("trashcans.gui.item_trash_can.title", "Item Trash Can");
        this.translation("trashcans.gui.item_trash_can.filter", "Filter");
        this.translation("trashcans.gui.liquid_trash_can.title", "Liquid Trash Can");
        this.translation("trashcans.gui.liquid_trash_can.filter", "Filter");
        this.translation("trashcans.gui.energy_trash_can.title", "Energy Trash Can");
        this.translation("trashcans.gui.energy_trash_can.limit", "Transfer Limit");
        this.translation("trashcans.gui.energy_trash_can.limit.change1", "SHIFT %s");
        this.translation("trashcans.gui.energy_trash_can.limit.change2", "CTRL %s");
        this.translation("trashcans.gui.energy_trash_can.limit.change3", "CTRL & SHIFT %s");
        this.translation("trashcans.gui.energy_trash_can.check.on", "Limited");
        this.translation("trashcans.gui.energy_trash_can.check.off", "Unlimited");
        this.translation("trashcans.gui.energy_trash_can.value", "Max $number$ FE/t");
        this.translation("trashcans.gui.ultimate_trash_can.title", "Ultimate Trash Can");
        this.translation("trashcans.gui.ultimate_trash_can.item_filter", "Item Filter");
        this.translation("trashcans.gui.ultimate_trash_can.liquid_filter", "Liquid Filter");
        this.translation("trashcans.gui.ultimate_trash_can.energy_limit", "Energy Transfer Limit");
        this.translation("trashcans.gui.whitelist.on", "Whitelist");
        this.translation("trashcans.gui.whitelist.off", "Blacklist");
        this.translation("trashcans.gui.arrow.left", "Decrease energy limit");
        this.translation("trashcans.gui.arrow.right", "Increase energy limit");
    }
}
