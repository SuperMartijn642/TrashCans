package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleEnergyLimit extends TrashCanPacket {

    public PacketToggleEnergyLimit(BlockPos pos){
        super(pos);
    }

    public PacketToggleEnergyLimit(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketToggleEnergyLimit decode(PacketBuffer buffer){
        return new PacketToggleEnergyLimit(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, TrashCanTile tile){
        if(tile.energy){
            tile.useEnergyLimit = !tile.useEnergyLimit;
            tile.dataChanged();
        }
    }
}
