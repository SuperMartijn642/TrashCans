package com.supermartijn642.trashcans.filter;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class LiquidTrashCanFilters {

    private static final Map<String,IFilterManager> managers = new HashMap<>();

    public static void register(IFilterManager manager, String id){
        managers.put(id, manager);
    }

    public static ItemFilter createFilter(ItemStack stack){
        for(Map.Entry<String,IFilterManager> entry : managers.entrySet()){
            ItemFilter filter = entry.getValue().createFilter(stack);
            if(filter != null && filter.isValid()){
                filter.setId(entry.getKey());
                return filter;
            }
        }
        return null;
    }

    public static CompoundTag write(ItemFilter filter, HolderLookup.Provider provider){
        CompoundTag compound = new CompoundTag();
        compound.putString("id", filter.getId());
        compound.put("filter", filter.write(provider));
        return compound;
    }

    public static ItemFilter read(CompoundTag compound, HolderLookup.Provider provider){
        String id = compound.getString("id");
        if(managers.containsKey(id)){
            ItemFilter filter = managers.get(id).readFilter(compound.getCompound("filter"), provider);
            filter.setId(id);
            return filter.isValid() ? filter : null;
        }
        return null;
    }

}
