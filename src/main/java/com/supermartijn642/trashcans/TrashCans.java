package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.FluidFilterManager;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = TrashCans.MODID, name = TrashCans.NAME, version = TrashCans.VERSION, dependencies = TrashCans.DEPENDENCIES)
public class TrashCans {

    public static final String MODID = "trashcans";
    public static final String NAME = "Trash Cans";
    public static final String VERSION = "1.0.11";
    public static final String DEPENDENCIES = "required-after:supermartijn642corelib@[1.0.3,1.1.0)";

    @Mod.Instance
    public static TrashCans instance;

    public static SimpleNetworkWrapper channel;

    @GameRegistry.ObjectHolder("trashcans:item_trash_can")
    public static Block item_trash_can;
    @GameRegistry.ObjectHolder("trashcans:liquid_trash_can")
    public static Block liquid_trash_can;
    @GameRegistry.ObjectHolder("trashcans:energy_trash_can")
    public static Block energy_trash_can;
    @GameRegistry.ObjectHolder("trashcans:ultimate_trash_can")
    public static Block ultimate_trash_can;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        channel.registerMessage(PacketToggleItemWhitelist.class, PacketToggleItemWhitelist.class, 0, Side.SERVER);
        channel.registerMessage(PacketToggleLiquidWhitelist.class, PacketToggleLiquidWhitelist.class, 1, Side.SERVER);
        channel.registerMessage(PacketToggleEnergyLimit.class, PacketToggleEnergyLimit.class, 2, Side.SERVER);
        channel.registerMessage(PacketChangeEnergyLimit.class, PacketChangeEnergyLimit.class, 3, Side.SERVER);
        channel.registerMessage(PacketChangeItemFilter.class, PacketChangeItemFilter.class, 4, Side.SERVER);
        channel.registerMessage(PacketChangeLiquidFilter.class, PacketChangeLiquidFilter.class, 5, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        LiquidTrashCanFilters.register(new FluidFilterManager(), "fluid");
        Compatibility.init();
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new TrashCanBlock("item_trash_can", TrashCanTile.ItemTrashCanTile::new, 0));
            e.getRegistry().register(new TrashCanBlock("liquid_trash_can", TrashCanTile.LiquidTrashCanTile::new, 1));
            e.getRegistry().register(new TrashCanBlock("energy_trash_can", TrashCanTile.EnergyTrashCanTile::new, 2));
            e.getRegistry().register(new TrashCanBlock("ultimate_trash_can", TrashCanTile.UltimateTrashCanTile::new, 3));
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
