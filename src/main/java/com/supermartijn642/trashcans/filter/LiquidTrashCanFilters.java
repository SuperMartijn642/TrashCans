package com.supermartijn642.trashcans.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

    public static NBTTagCompound write(ItemFilter filter){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("id", filter.getId());
        compound.setTag("filter", filter.write());
        return compound;
    }

    public static ItemFilter read(NBTTagCompound compound){
        String id = compound.getString("id");
        if(managers.containsKey(id)){
            ItemFilter filter = managers.get(id).readFilter(compound.getCompoundTag("filter"));
            filter.setId(id);
            return filter.isValid() ? filter : null;
        }
        return null;
    }

}
