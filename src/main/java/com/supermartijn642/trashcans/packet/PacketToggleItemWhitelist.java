package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleItemWhitelist extends TrashCanPacket<PacketToggleItemWhitelist> {

    public PacketToggleItemWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleItemWhitelist(){
    }

    @Override
    protected void handle(PacketToggleItemWhitelist message, EntityPlayer player, World world, TrashCanTile tile){
        if(tile.items){
            tile.itemFilterWhitelist = !tile.itemFilterWhitelist;
            tile.dataChanged();
        }
    }
}
