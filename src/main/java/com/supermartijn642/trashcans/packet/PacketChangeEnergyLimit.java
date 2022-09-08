package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketChangeEnergyLimit extends BlockEntityBasePacket<TrashCanBlockEntity> {

    private int amount;

    public PacketChangeEnergyLimit(BlockPos pos, int amount){
        super(pos);
        this.amount = amount;
    }

    public PacketChangeEnergyLimit(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        super.write(buffer);
        buffer.writeInt(this.amount);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        super.read(buffer);
        this.amount = buffer.readInt();
    }

    @Override
    public boolean verify(PacketContext context){
        return Math.abs(this.amount) >= 1 && Math.abs(this.amount) <= 1000;
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.energy && Math.abs(this.amount) >= 1 && Math.abs(this.amount) <= 10000){
            int amount = entity.energyLimit == 1 && this.amount > 1 ? this.amount : entity.energyLimit + this.amount;
            int limit = Math.min(Math.max(amount, TrashCanBlockEntity.MIN_ENERGY_LIMIT), TrashCanBlockEntity.MAX_ENERGY_LIMIT);
            if(entity.energyLimit != limit){
                entity.energyLimit = limit;
                entity.dataChanged();
            }
        }
    }
}
