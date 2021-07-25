package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleItemWhitelist extends TrashCanPacket {

    public PacketToggleItemWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleItemWhitelist(FriendlyByteBuf buffer){
        super(buffer);
    }

    public static PacketToggleItemWhitelist decode(FriendlyByteBuf buffer){
        return new PacketToggleItemWhitelist(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.items){
            tile.itemFilterWhitelist = !tile.itemFilterWhitelist;
            tile.dataChanged();
        }
    }
}
