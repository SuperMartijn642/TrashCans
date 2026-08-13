package com.supermartijn642.trashcans;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 02/03/2023 by SuperMartijn642
 */
public class TrashCansConfig {

    public static final Supplier<Boolean> retrieveDeletedItems;

    static{
        IConfigBuilder builder = ConfigBuilders.newTomlConfig(TrashCans.MODID, null, false);

        retrieveDeletedItems = builder.comment("Whether to allow retrieving the last couple deleted items from the trash can screen").define("retrieveDeletedItems", true);

        builder.build();
    }

    public static void init(){
        // just to cause this class to load
    }
}
