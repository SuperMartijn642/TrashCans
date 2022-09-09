package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleLiquidWhitelist extends BlockEntityBasePacket<TrashCanBlockEntity> {

    public PacketToggleLiquidWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleLiquidWhitelist(){
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.liquids){
            entity.liquidFilterWhitelist = !entity.liquidFilterWhitelist;
            entity.dataChanged();
        }
    }
}
