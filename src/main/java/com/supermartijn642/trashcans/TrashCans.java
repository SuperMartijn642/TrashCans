package com.supermartijn642.trashcans;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = TrashCans.MODID, name = TrashCans.NAME, version = TrashCans.VERSION)
public class TrashCans {

    public static final String MODID = "trashcans";
    public static final String NAME = "Trash Cans";
    public static final String VERSION = "1.0.0";

    @GameRegistry.ObjectHolder("trashcans:item_trash_can")
    public static Block item_trash_can;
    @GameRegistry.ObjectHolder("trashcans:liquid_trash_can")
    public static Block liquid_trash_can;
    @GameRegistry.ObjectHolder("trashcans:energy_trash_can")
    public static Block energy_trash_can;
    @GameRegistry.ObjectHolder("trashcans:ultimate_trash_can")
    public static Block ultimate_trash_can;

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new TrashCanBlock("item_trash_can", TrashCanTile.ItemTrashCanTile::new));
            e.getRegistry().register(new TrashCanBlock("liquid_trash_can", TrashCanTile.LiquidTrashCanTile::new));
            e.getRegistry().register(new TrashCanBlock("energy_trash_can", TrashCanTile.EnergyTrashCanTile::new));
            e.getRegistry().register(new TrashCanBlock("ultimate_trash_can", TrashCanTile.UltimateTrashCanTile::new));
            GameRegistry.registerTileEntity(TrashCanTile.ItemTrashCanTile.class, new ResourceLocation(MODID, "itemtrashcan"));
            GameRegistry.registerTileEntity(TrashCanTile.LiquidTrashCanTile.class, new ResourceLocation(MODID, "liquidtrashcan"));
            GameRegistry.registerTileEntity(TrashCanTile.EnergyTrashCanTile.class, new ResourceLocation(MODID, "energytrashcan"));
            GameRegistry.registerTileEntity(TrashCanTile.UltimateTrashCanTile.class, new ResourceLocation(MODID, "ultimatetrashcan"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new ItemBlock(item_trash_can).setRegistryName("item_trash_can"));
            e.getRegistry().register(new ItemBlock(liquid_trash_can).setRegistryName("liquid_trash_can"));
            e.getRegistry().register(new ItemBlock(energy_trash_can).setRegistryName("energy_trash_can"));
            e.getRegistry().register(new ItemBlock(ultimate_trash_can).setRegistryName("ultimate_trash_can"));
        }
    }

}
