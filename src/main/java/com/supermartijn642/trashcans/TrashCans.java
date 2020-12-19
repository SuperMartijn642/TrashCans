package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.FluidFilterManager;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import com.supermartijn642.trashcans.screen.EnergyTrashCanContainer;
import com.supermartijn642.trashcans.screen.ItemTrashCanContainer;
import com.supermartijn642.trashcans.screen.LiquidTrashCanContainer;
import com.supermartijn642.trashcans.screen.UltimateTrashCanContainer;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("trashcans", "main"), () -> "1", "1"::equals, "1"::equals);

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

    @ObjectHolder("trashcans:item_trash_can_container")
    public static ContainerType<ItemTrashCanContainer> item_trash_can_container;
    @ObjectHolder("trashcans:liquid_trash_can_container")
    public static ContainerType<LiquidTrashCanContainer> liquid_trash_can_container;
    @ObjectHolder("trashcans:energy_trash_can_container")
    public static ContainerType<EnergyTrashCanContainer> energy_trash_can_container;
    @ObjectHolder("trashcans:ultimate_trash_can_container")
    public static ContainerType<UltimateTrashCanContainer> ultimate_trash_can_container;

    public TrashCans(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        CHANNEL.registerMessage(0, PacketToggleItemWhitelist.class, PacketToggleItemWhitelist::encode, PacketToggleItemWhitelist::decode, PacketToggleItemWhitelist::handle);
        CHANNEL.registerMessage(1, PacketToggleLiquidWhitelist.class, PacketToggleLiquidWhitelist::encode, PacketToggleLiquidWhitelist::decode, PacketToggleLiquidWhitelist::handle);
        CHANNEL.registerMessage(2, PacketToggleEnergyLimit.class, PacketToggleEnergyLimit::encode, PacketToggleEnergyLimit::decode, PacketToggleEnergyLimit::handle);
        CHANNEL.registerMessage(3, PacketChangeEnergyLimit.class, PacketChangeEnergyLimit::encode, PacketChangeEnergyLimit::decode, PacketChangeEnergyLimit::handle);
    }

    public void init(FMLCommonSetupEvent e){
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientProxy::registerScreen);
        LiquidTrashCanFilters.register(new FluidFilterManager(), "fluid");
        Compatibility.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new TrashCanBlock("item_trash_can", () -> new TrashCanTile(item_trash_can_tile, true, false, false), ItemTrashCanContainer::new));
            e.getRegistry().register(new TrashCanBlock("liquid_trash_can", () -> new TrashCanTile(liquid_trash_can_tile, false, true, false), LiquidTrashCanContainer::new));
            e.getRegistry().register(new TrashCanBlock("energy_trash_can", () -> new TrashCanTile(energy_trash_can_tile, false, false, true), EnergyTrashCanContainer::new));
            e.getRegistry().register(new TrashCanBlock("ultimate_trash_can", () -> new TrashCanTile(ultimate_trash_can_tile, true, true, true), UltimateTrashCanContainer::new));
        }

        @SubscribeEvent
        public static void onTileRegistry(final RegistryEvent.Register<TileEntityType<?>> e){
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(item_trash_can_tile, true, false, false), item_trash_can).build(null).setRegistryName("item_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(liquid_trash_can_tile, false, true, false), liquid_trash_can).build(null).setRegistryName("liquid_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(energy_trash_can_tile, false, false, true), energy_trash_can).build(null).setRegistryName("energy_trash_can_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(() -> new TrashCanTile(ultimate_trash_can_tile, true, true, true), ultimate_trash_can).build(null).setRegistryName("ultimate_trash_can_tile"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new BlockItem(item_trash_can, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("item_trash_can"));
            e.getRegistry().register(new BlockItem(liquid_trash_can, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("liquid_trash_can"));
            e.getRegistry().register(new BlockItem(energy_trash_can, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("energy_trash_can"));
            e.getRegistry().register(new BlockItem(ultimate_trash_can, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("ultimate_trash_can"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e){
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new ItemTrashCanContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("item_trash_can_container"));
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new LiquidTrashCanContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("liquid_trash_can_container"));
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new EnergyTrashCanContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("energy_trash_can_container"));
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new UltimateTrashCanContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("ultimate_trash_can_container"));
        }
    }

}
