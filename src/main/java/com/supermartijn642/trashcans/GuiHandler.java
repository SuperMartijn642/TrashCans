package com.supermartijn642.trashcans;

import com.supermartijn642.trashcans.screen.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * Created 7/9/2020 by SuperMartijn642
 */
public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == 0)
            return new ItemTrashCanContainer(player, pos);
        if(ID == 1)
            return new LiquidTrashCanContainer(player, pos);
        if(ID == 2)
            return new EnergyTrashCanContainer(player, pos);
        if(ID == 3)
            return new UltimateTrashCanContainer(player, pos);
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == 0)
            return new ItemTrashCanScreen(new ItemTrashCanContainer(player, pos));
        if(ID == 1)
            return new LiquidTrashCanScreen(new LiquidTrashCanContainer(player, pos));
        if(ID == 2)
            return new EnergyTrashCanScreen(new EnergyTrashCanContainer(player, pos));
        if(ID == 3)
            return new UltimateTrashCanScreen(new UltimateTrashCanContainer(player, pos));
        return null;
    }
}
