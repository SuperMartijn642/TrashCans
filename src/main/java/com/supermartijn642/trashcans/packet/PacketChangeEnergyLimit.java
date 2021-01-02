package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketChangeEnergyLimit extends TrashCanPacket<PacketChangeEnergyLimit> {

    private int amount;

    public PacketChangeEnergyLimit(BlockPos pos, int amount){
        super(pos);
        this.amount = amount;
    }

    public PacketChangeEnergyLimit(){
    }

    @Override
    public void encode(ByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.amount);
    }

    @Override
    protected void decodeBuffer(ByteBuf buffer){
        super.decodeBuffer(buffer);
        this.amount = buffer.readInt();
    }

    @Override
    protected void handle(PacketChangeEnergyLimit message, EntityPlayer player, World world, TrashCanTile tile){
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
