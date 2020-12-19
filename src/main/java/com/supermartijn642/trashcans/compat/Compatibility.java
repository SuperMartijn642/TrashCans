package com.supermartijn642.trashcans.compat;

import com.supermartijn642.trashcans.compat.mekanism.MekanismCompatOff;
import com.supermartijn642.trashcans.compat.mekanism.MekanismCompatOn;
import net.minecraftforge.fml.ModList;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class Compatibility {

    public static MekanismCompatOff MEKANISM;

    public static void init(){
        MEKANISM = ModList.get().isLoaded("mekanism") ?
            new MekanismCompatOn() : new MekanismCompatOff();
    }

}
