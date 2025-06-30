package com.supermartijn642.trashcans.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public abstract class ItemFilter {

    private String id;

    ItemFilter setId(String id){
        this.id = id;
        return this;
    }

    public String getId(){
        return this.id;
    }

    public abstract boolean matches(Object stack);

    public abstract ItemStack getRepresentingItem();

    public abstract void write(ValueOutput output);

    public abstract boolean isValid();

}
