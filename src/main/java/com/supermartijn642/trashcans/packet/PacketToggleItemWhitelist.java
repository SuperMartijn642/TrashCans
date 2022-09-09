package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleItemWhitelist extends BlockEntityBasePacket<TrashCanBlockEntity> {

    public PacketToggleItemWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleItemWhitelist(){
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.items){
            entity.itemFilterWhitelist = !entity.itemFilterWhitelist;
            entity.dataChanged();
        }
    }
}
