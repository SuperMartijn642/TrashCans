package com.supermartijn642.trashcans.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    public static void write(ItemFilter filter, ValueOutput output){
        output.putString("id", filter.getId());
        filter.write(output.child("filter"));
    }

    public static ItemFilter read(ValueInput input){
        String id = input.getStringOr("id", "");
        if(managers.containsKey(id)){
            ItemFilter filter = managers.get(id).readFilter(input.childOrEmpty("filter"));
            filter.setId(id);
            return filter.isValid() ? filter : null;
        }
        return null;
    }

}
