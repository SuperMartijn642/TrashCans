package com.supermartijn642.trashcans.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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

    public static CompoundNBT write(ItemFilter filter){
        CompoundNBT compound = new CompoundNBT();
        compound.putString("id", filter.getId());
        compound.put("filter", filter.write());
        return compound;
    }

    public static ItemFilter read(CompoundNBT compound){
        String id = compound.getString("id");
        if(managers.containsKey(id)){
            ItemFilter filter = managers.get(id).readFilter(compound.getCompound("filter"));
            filter.setId(id);
            return filter.isValid() ? filter : null;
        }
        return null;
    }

}
