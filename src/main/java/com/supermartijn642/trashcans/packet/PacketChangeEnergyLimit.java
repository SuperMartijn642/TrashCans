package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketChangeEnergyLimit extends TrashCanPacket {

    private int amount;

    public PacketChangeEnergyLimit(BlockPos pos, int amount){
        super(pos);
        this.amount = amount;
    }

    public PacketChangeEnergyLimit(FriendlyByteBuf buffer){
        super(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.amount);
    }

    @Override
    protected void decodeBuffer(FriendlyByteBuf buffer){
        super.decodeBuffer(buffer);
        this.amount = buffer.readInt();
    }

    public static PacketChangeEnergyLimit decode(FriendlyByteBuf buffer){
        return new PacketChangeEnergyLimit(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.energy && Math.abs(this.amount) >= 1 && Math.abs(this.amount) <= 10000){
            int amount = tile.energyLimit == 1 && this.amount > 1 ? this.amount : tile.energyLimit + this.amount;
            int limit = Math.min(Math.max(amount, TrashCanTile.MIN_ENERGY_LIMIT), TrashCanTile.MAX_ENERGY_LIMIT);
            if(tile.energyLimit != limit){
                tile.energyLimit = limit;
                tile.dataChanged();
            }
        }
    }
}
