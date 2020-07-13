package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleEnergyLimit extends TrashCanPacket<PacketToggleEnergyLimit> {

    public PacketToggleEnergyLimit(BlockPos pos){
        super(pos);
    }

    public PacketToggleEnergyLimit(){
    }

    @Override
    protected void handle(PacketToggleEnergyLimit message, EntityPlayer player, World world, TrashCanTile tile){
        if(tile.energy){
            tile.useEnergyLimit = !tile.useEnergyLimit;
            tile.dataChanged();
        }
    }
}
