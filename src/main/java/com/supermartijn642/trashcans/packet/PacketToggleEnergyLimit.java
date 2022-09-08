package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.core.BlockPos;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleEnergyLimit extends BlockEntityBasePacket<TrashCanBlockEntity> {

    public PacketToggleEnergyLimit(BlockPos pos){
        super(pos);
    }

    public PacketToggleEnergyLimit(){
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.energy){
            entity.useEnergyLimit = !entity.useEnergyLimit;
            entity.dataChanged();
        }
    }
}
