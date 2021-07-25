package com.supermartijn642.trashcans.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

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

    public abstract CompoundTag write();

    public abstract boolean isValid();

}
