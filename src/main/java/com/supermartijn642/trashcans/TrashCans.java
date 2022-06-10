package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.data.TrashCansAdvancementProvider;
import com.supermartijn642.trashcans.filter.FluidFilterManager;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.packet.*;
import com.supermartijn642.trashcans.screen.EnergyTrashCanContainer;
import com.supermartijn642.trashcans.screen.ItemTrashCanContainer;
import com.supermartijn642.trashcans.screen.LiquidTrashCanContainer;
import com.supermartijn642.trashcans.screen.UltimateTrashCanContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Objects;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("trashcans")
public class TrashCans {

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("trashcans", "main"), () -> "1", "1"::equals, "1"::equals);

    @ObjectHolder(value = "trashcans:item_trash_can", registryName = "minecraft:block")
    public static Block item_trash_can;
    @ObjectHolder(value = "trashcans:liquid_trash_can", registryName = "minecraft:block")
    public static Block liquid_trash_can;
    @ObjectHolder(value = "trashcans:energy_trash_can", registryName = "minecraft:block")
    public static Block energy_trash_can;
    @ObjectHolder(value = "trashcans:ultimate_trash_can", registryName = "minecraft:block")
    public static Block ultimate_trash_can;

    @ObjectHolder(value = "trashcans:item_trash_can_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<TrashCanTile> item_trash_can_tile;
    @ObjectHolder(value = "trashcans:liquid_trash_can_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<TrashCanTile> liquid_trash_can_tile;
    @ObjectHolder(value = "trashcans:energy_trash_can_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<TrashCanTile> energy_trash_can_tile;
    @ObjectHolder(value = "trashcans:ultimate_trash_can_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<TrashCanTile> ultimate_trash_can_tile;

    @ObjectHolder(value = "trashcans:item_trash_can_container", registryName = "minecraft:menu")
    public static MenuType<ItemTrashCanContainer> item_trash_can_container;
    @ObjectHolder(value = "trashcans:liquid_trash_can_container", registryName = "minecraft:menu")
    public static MenuType<LiquidTrashCanContainer> liquid_trash_can_container;
    @ObjectHolder(value = "trashcans:energy_trash_can_container", registryName = "minecraft:menu")
    public static MenuType<EnergyTrashCanContainer> energy_trash_can_container;
    @ObjectHolder(value = "trashcans:ultimate_trash_can_container", registryName = "minecraft:menu")
    public static MenuType<UltimateTrashCanContainer> ultimate_trash_can_container;

    public TrashCans(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        CHANNEL.registerMessage(0, PacketToggleItemWhitelist.class, PacketToggleItemWhitelist::encode, PacketToggleItemWhitelist::decode, PacketToggleItemWhitelist::handle);
        CHANNEL.registerMessage(1, PacketToggleLiquidWhitelist.class, PacketToggleLiquidWhitelist::encode, PacketToggleLiquidWhitelist::decode, PacketToggleLiquidWhitelist::handle);
        CHANNEL.registerMessage(2, PacketToggleEnergyLimit.class, PacketToggleEnergyLimit::encode, PacketToggleEnergyLimit::decode, PacketToggleEnergyLimit::handle);
        CHANNEL.registerMessage(3, PacketChangeEnergyLimit.class, PacketChangeEnergyLimit::encode, PacketChangeEnergyLimit::decode, PacketChangeEnergyLimit::handle);
        CHANNEL.registerMessage(4, PacketChangeItemFilter.class, PacketChangeItemFilter::encode, PacketChangeItemFilter::decode, PacketChangeItemFilter::handle);
        CHANNEL.registerMessage(5, PacketChangeLiquidFilter.class, PacketChangeLiquidFilter::encode, PacketChangeLiquidFilter::decode, PacketChangeLiquidFilter::handle);
    }

    public void init(FMLCommonSetupEvent e){
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientProxy::registerScreen);
        LiquidTrashCanFilters.register(new FluidFilterManager(), "fluid");
        Compatibility.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent e){
            if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS))
                onBlockRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
                onTileRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                onItemRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.CONTAINER_TYPES))
                onContainerRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onBlockRegistry(IForgeRegistry<Block> registry){
            registry.register("item_trash_can", new TrashCanBlock("item_trash_can", (pos, state) -> new TrashCanTile(item_trash_can_tile, pos, state, true, false, false), ItemTrashCanContainer::new));
            registry.register("liquid_trash_can", new TrashCanBlock("liquid_trash_can", (pos, state) -> new TrashCanTile(liquid_trash_can_tile, pos, state, false, true, false), LiquidTrashCanContainer::new));
            registry.register("energy_trash_can", new TrashCanBlock("energy_trash_can", (pos, state) -> new TrashCanTile(energy_trash_can_tile, pos, state, false, false, true), EnergyTrashCanContainer::new));
            registry.register("ultimate_trash_can", new TrashCanBlock("ultimate_trash_can", (pos, state) -> new TrashCanTile(ultimate_trash_can_tile, pos, state, true, true, true), UltimateTrashCanContainer::new));
        }

        public static void onTileRegistry(IForgeRegistry<BlockEntityType<?>> registry){
            registry.register("item_trash_can_tile", BlockEntityType.Builder.of((pos, state) -> new TrashCanTile(item_trash_can_tile, pos, state, true, false, false), item_trash_can).build(null));
            registry.register("liquid_trash_can_tile", BlockEntityType.Builder.of((pos, state) -> new TrashCanTile(liquid_trash_can_tile, pos, state, false, true, false), liquid_trash_can).build(null));
            registry.register("energy_trash_can_tile", BlockEntityType.Builder.of((pos, state) -> new TrashCanTile(energy_trash_can_tile, pos, state, false, false, true), energy_trash_can).build(null));
            registry.register("ultimate_trash_can_tile", BlockEntityType.Builder.of((pos, state) -> new TrashCanTile(ultimate_trash_can_tile, pos, state, true, true, true), ultimate_trash_can).build(null));
        }

        public static void onItemRegistry(IForgeRegistry<Item> registry){
            registry.register("item_trash_can", new BlockItem(item_trash_can, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
            registry.register("liquid_trash_can", new BlockItem(liquid_trash_can, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
            registry.register("energy_trash_can", new BlockItem(energy_trash_can, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
            registry.register("ultimate_trash_can", new BlockItem(ultimate_trash_can, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
        }

        public static void onContainerRegistry(IForgeRegistry<MenuType<?>> registry){
            registry.register("item_trash_can_container", IForgeMenuType.create((windowId, inv, data) -> new ItemTrashCanContainer(windowId, inv.player, data.readBlockPos())));
            registry.register("liquid_trash_can_container", IForgeMenuType.create((windowId, inv, data) -> new LiquidTrashCanContainer(windowId, inv.player, data.readBlockPos())));
            registry.register("energy_trash_can_container", IForgeMenuType.create((windowId, inv, data) -> new EnergyTrashCanContainer(windowId, inv.player, data.readBlockPos())));
            registry.register("ultimate_trash_can_container", IForgeMenuType.create((windowId, inv, data) -> new UltimateTrashCanContainer(windowId, inv.player, data.readBlockPos())));
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent e){
            e.getGenerator().addProvider(e.includeServer(), new TrashCansAdvancementProvider(e));
        }
    }

}
