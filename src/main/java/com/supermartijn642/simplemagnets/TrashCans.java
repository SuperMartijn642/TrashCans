package com.supermartijn642.simplemagnets;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("trashcans")
public class TrashCans {

    @ObjectHolder("trashcans:item_trash_can")
    public static Block item_trash_can;
    @ObjectHolder("trashcans:liquid_trash_can")
    public static Block liquid_trash_can;
    @ObjectHolder("trashcans:energy_trash_can")
    public static Block energy_trash_can;
    @ObjectHolder("trashcans:ultimate_trash_can")
    public static Block ultimate_trash_can;

    @ObjectHolder("trashcans:item_trash_can_tile")
    public static TileEntityType<TrashCanTile> item_trash_can_tile;
    @ObjectHolder("trashcans:liquid_trash_can_tile")
    public static TileEntityType<TrashCanTile> liquid_trash_can_tile;
    @ObjectHolder("trashcans:energy_trash_can_tile")
    public static TileEntityType<TrashCanTile> energy_trash_can_tile;
    @ObjectHolder("trashcans:ultimate_trash_can_tile")
    public static TileEntityType<TrashCanTile> ultimate_trash_can_tile;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new TrashCanBlock("item_trash_can",() -> new TrashCanTile(item_trash_can_tile,true,false,false)));
            e.getRegistry().register(new TrashCanBlock("liquid_trash_can",() -> new TrashCanTile(liquid_trash_can_tile,false,true,false)));
            e.getRegistry().register(new TrashCanBlock("energy_trash_can",() -> new TrashCanTile(energy_trash_can_tile,false,false,true)));
            e.getRegistry().register(new TrashCanBlock("ultimate_trash_can",() -> new TrashCanTile(ultimate_trash_can_tile,true,true,true)));
        }

        @SubscribeEvent
        public static void onTileRegistry(final RegistryEvent.Register<TileEntityType<?>> e){
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(item_trash_can_tile,true,false,false), item_trash_can).build(null).setRegistryName("item_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(liquid_trash_can_tile,false,true,false), liquid_trash_can).build(null).setRegistryName("liquid_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(energy_trash_can_tile,false,false,true), energy_trash_can).build(null).setRegistryName("energy_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(ultimate_trash_can_tile,true,true,true), ultimate_trash_can).build(null).setRegistryName("ultimate_trash_can_tile"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new BlockItem(item_trash_can,new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("trashcans:item_trash_can"));
            e.getRegistry().register(new BlockItem(liquid_trash_can,new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("trashcans:liquid_trash_can"));
            e.getRegistry().register(new BlockItem(energy_trash_can,new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("trashcans:energy_trash_can"));
            e.getRegistry().register(new BlockItem(ultimate_trash_can,new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("trashcans:ultimate_trash_can"));
        }
    }

}
