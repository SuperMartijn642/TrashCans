package com.supermartijn642.trashcans;

import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.item.BaseBlockItem;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.trashcans.compat.Compatibility;
import com.supermartijn642.trashcans.filter.FluidFilterManager;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import com.supermartijn642.trashcans.generators.*;
import com.supermartijn642.trashcans.packet.*;
import com.supermartijn642.trashcans.screen.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("trashcans")
public class TrashCans {

    public static final PacketChannel CHANNEL = PacketChannel.create("trashcans");

    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "item_trash_can", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock item_trash_can;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "liquid_trash_can", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock liquid_trash_can;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "energy_trash_can", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock energy_trash_can;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "ultimate_trash_can", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock ultimate_trash_can;

    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "item_trash_can_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<TrashCanBlockEntity> item_trash_can_tile;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "liquid_trash_can_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<TrashCanBlockEntity> liquid_trash_can_tile;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "energy_trash_can_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<TrashCanBlockEntity> energy_trash_can_tile;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "ultimate_trash_can_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<TrashCanBlockEntity> ultimate_trash_can_tile;

    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "item_trash_can_container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<TrashCanContainer> item_trash_can_container;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "liquid_trash_can_container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<TrashCanContainer> liquid_trash_can_container;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "energy_trash_can_container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<TrashCanContainer> energy_trash_can_container;
    @RegistryEntryAcceptor(namespace = "trashcans", identifier = "ultimate_trash_can_container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<TrashCanContainer> ultimate_trash_can_container;

    public TrashCans(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        CHANNEL.registerMessage(PacketToggleItemWhitelist.class, PacketToggleItemWhitelist::new, true);
        CHANNEL.registerMessage(PacketToggleLiquidWhitelist.class, PacketToggleLiquidWhitelist::new, true);
        CHANNEL.registerMessage(PacketToggleEnergyLimit.class, PacketToggleEnergyLimit::new, true);
        CHANNEL.registerMessage(PacketChangeEnergyLimit.class, PacketChangeEnergyLimit::new, true);
        CHANNEL.registerMessage(PacketChangeItemFilter.class, PacketChangeItemFilter::new, true);
        CHANNEL.registerMessage(PacketChangeLiquidFilter.class, PacketChangeLiquidFilter::new, true);

        register();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> TrashCansClient::registerScreens);
        registerGenerators();
    }

    public void init(FMLCommonSetupEvent e){
        LiquidTrashCanFilters.register(new FluidFilterManager(), "fluid");
        Compatibility.init();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("trashcans");

        // Blocks
        handler.registerBlock("item_trash_can", () -> new TrashCanBlock(() -> item_trash_can_tile, ItemTrashCanContainer::new));
        handler.registerBlock("liquid_trash_can", () -> new TrashCanBlock(() -> liquid_trash_can_tile, LiquidTrashCanContainer::new));
        handler.registerBlock("energy_trash_can", () -> new TrashCanBlock(() -> energy_trash_can_tile, EnergyTrashCanContainer::new));
        handler.registerBlock("ultimate_trash_can", () -> new TrashCanBlock(() -> ultimate_trash_can_tile, UltimateTrashCanContainer::new));

        // Block entities
        handler.registerBlockEntityType("item_trash_can_tile", () -> BaseBlockEntityType.create(() -> new TrashCanBlockEntity(item_trash_can_tile, true, false, false), item_trash_can));
        handler.registerBlockEntityType("liquid_trash_can_tile", () -> BaseBlockEntityType.create(() -> new TrashCanBlockEntity(liquid_trash_can_tile, false, true, false), liquid_trash_can));
        handler.registerBlockEntityType("energy_trash_can_tile", () -> BaseBlockEntityType.create(() -> new TrashCanBlockEntity(energy_trash_can_tile, false, false, true), energy_trash_can));
        handler.registerBlockEntityType("ultimate_trash_can_tile", () -> BaseBlockEntityType.create(() -> new TrashCanBlockEntity(ultimate_trash_can_tile, true, true, true), ultimate_trash_can));

        // Items
        handler.registerItem("item_trash_can", () -> new BaseBlockItem(item_trash_can, ItemProperties.create().group(CreativeItemGroup.getDecoration())));
        handler.registerItem("liquid_trash_can", () -> new BaseBlockItem(liquid_trash_can, ItemProperties.create().group(CreativeItemGroup.getDecoration())));
        handler.registerItem("energy_trash_can", () -> new BaseBlockItem(energy_trash_can, ItemProperties.create().group(CreativeItemGroup.getDecoration())));
        handler.registerItem("ultimate_trash_can", () -> new BaseBlockItem(ultimate_trash_can, ItemProperties.create().group(CreativeItemGroup.getDecoration())));

        // Container types
        handler.registerMenuType("item_trash_can_container", BaseContainerType.create((container, buffer) -> buffer.writeBlockPos(container.getBlockEntityPos()), (player, buffer) -> new ItemTrashCanContainer(player, buffer.readBlockPos())));
        handler.registerMenuType("liquid_trash_can_container", BaseContainerType.create((container, buffer) -> buffer.writeBlockPos(container.getBlockEntityPos()), (player, buffer) -> new LiquidTrashCanContainer(player, buffer.readBlockPos())));
        handler.registerMenuType("energy_trash_can_container", BaseContainerType.create((container, buffer) -> buffer.writeBlockPos(container.getBlockEntityPos()), (player, buffer) -> new EnergyTrashCanContainer(player, buffer.readBlockPos())));
        handler.registerMenuType("ultimate_trash_can_container", BaseContainerType.create((container, buffer) -> buffer.writeBlockPos(container.getBlockEntityPos()), (player, buffer) -> new UltimateTrashCanContainer(player, buffer.readBlockPos())));
    }

    private static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("trashcans");
        handler.addGenerator(TrashCansAdvancementGenerator::new);
        handler.addGenerator(TrashCansModelGenerator::new);
        handler.addGenerator(TrashCansBlockStateGenerator::new);
        handler.addGenerator(TrashCansLanguageGenerator::new);
        handler.addGenerator(TrashCansLootTableGenerator::new);
        handler.addGenerator(TrashCansRecipeGenerator::new);
        handler.addGenerator(TrashCansTagGenerator::new);
    }
}
