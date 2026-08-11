package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.gui.BlockEntityBaseContainer;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanContainer extends BlockEntityBaseContainer<TrashCanBlockEntity> {

    public final int width, height;

    public TrashCanContainer(BaseContainerType<?> type, PlayerEntity player, BlockPos pos, int width, int height){
        super(type, player, player.level, pos);
        this.width = width;
        this.height = height;

        this.addSlots();
        this.addPlayerSlots(21, this.height - 82);
    }

    public BlockPos getBlockEntityPos(){
        return this.blockEntityPos;
    }

    public TrashCanBlockEntity getBlockEntity(){
        return this.object;
    }
}
