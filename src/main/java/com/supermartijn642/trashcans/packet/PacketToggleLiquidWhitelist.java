package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleLiquidWhitelist extends TrashCanPacket<PacketToggleLiquidWhitelist> {

    public PacketToggleLiquidWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleLiquidWhitelist(){
    }

    @Override
    protected void handle(PacketToggleLiquidWhitelist message, EntityPlayer player, World world, TrashCanTile tile){
        if(tile.liquids){
            tile.liquidFilterWhitelist = !tile.liquidFilterWhitelist;
            tile.dataChanged();
        }
    }
}
