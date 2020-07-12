package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleLiquidWhitelist extends TrashCanPacket {

    public PacketToggleLiquidWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleLiquidWhitelist(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketToggleLiquidWhitelist decode(PacketBuffer buffer){
        return new PacketToggleLiquidWhitelist(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, TrashCanTile tile){
        if(tile.liquids){
            tile.liquidFilterWhitelist = !tile.liquidFilterWhitelist;
            tile.dataChanged();
        }
    }
}
