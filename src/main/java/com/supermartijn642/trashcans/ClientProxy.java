package com.supermartijn642.trashcans;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCans.item_trash_can), 0, new ModelResourceLocation(TrashCans.item_trash_can.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCans.liquid_trash_can), 0, new ModelResourceLocation(TrashCans.liquid_trash_can.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCans.energy_trash_can), 0, new ModelResourceLocation(TrashCans.energy_trash_can.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCans.ultimate_trash_can), 0, new ModelResourceLocation(TrashCans.ultimate_trash_can.getRegistryName(), "inventory"));
    }

}
