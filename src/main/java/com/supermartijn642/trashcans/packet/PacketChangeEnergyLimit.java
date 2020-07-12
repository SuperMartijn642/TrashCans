package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketChangeEnergyLimit extends TrashCanPacket {

    private int amount;

    public PacketChangeEnergyLimit(BlockPos pos, int amount){
        super(pos);
        this.amount = amount;
    }

    public PacketChangeEnergyLimit(PacketBuffer buffer){
        super(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer){
        super.encode(buffer);
        buffer.writeInt(this.amount);
    }

    @Override
    protected void decodeBuffer(PacketBuffer buffer){
        super.decodeBuffer(buffer);
        this.amount = buffer.readInt();
    }

    public static PacketChangeEnergyLimit decode(PacketBuffer buffer){
        return new PacketChangeEnergyLimit(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, TrashCanTile tile){
        if(tile.energy && Math.abs(this.amount) >= 100 && Math.abs(this.amount) <= 100000 && this.amount % 10 == 0){
            int limit = Math.min(Math.max(tile.energyLimit + this.amount, TrashCanTile.MIN_ENERGY_LIMIT), TrashCanTile.MAX_ENERGY_LIMIT);
            if(tile.energyLimit != limit){
                tile.energyLimit = limit;
                tile.dataChanged();
            }
        }
    }
}
