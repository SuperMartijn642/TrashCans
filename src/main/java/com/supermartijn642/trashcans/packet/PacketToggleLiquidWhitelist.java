package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleLiquidWhitelist extends TrashCanPacket {

    public PacketToggleLiquidWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleLiquidWhitelist(FriendlyByteBuf buffer){
        super(buffer);
    }

    public static PacketToggleLiquidWhitelist decode(FriendlyByteBuf buffer){
        return new PacketToggleLiquidWhitelist(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.liquids){
            tile.liquidFilterWhitelist = !tile.liquidFilterWhitelist;
            tile.dataChanged();
        }
    }
}
