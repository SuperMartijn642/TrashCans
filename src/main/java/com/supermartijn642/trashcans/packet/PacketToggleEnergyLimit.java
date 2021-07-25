package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleEnergyLimit extends TrashCanPacket {

    public PacketToggleEnergyLimit(BlockPos pos){
        super(pos);
    }

    public PacketToggleEnergyLimit(FriendlyByteBuf buffer){
        super(buffer);
    }

    public static PacketToggleEnergyLimit decode(FriendlyByteBuf buffer){
        return new PacketToggleEnergyLimit(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.energy){
            tile.useEnergyLimit = !tile.useEnergyLimit;
            tile.dataChanged();
        }
    }
}
