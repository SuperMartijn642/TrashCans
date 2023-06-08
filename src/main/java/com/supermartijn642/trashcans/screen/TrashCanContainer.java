package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.gui.BlockEntityBaseContainer;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanContainer extends BlockEntityBaseContainer<TrashCanBlockEntity> {

    public final int width, height;

    public TrashCanContainer(BaseContainerType<?> type, Player player, BlockPos pos, int width, int height){
        super(type, player, player.level(), pos);
        this.width = width;
        this.height = height;

        this.addSlots();
        this.addPlayerSlots(21,this.height - 82);
    }

    private void addPlayerSlots(){
        // player
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                this.addSlot(new Slot(this.player.getInventory(), row * 9 + column + 9, 21 + 18 * column, this.height - 82 + 18 * row));
            }
        }

        // hot bar
        for(int column = 0; column < 9; column++)
            this.addSlot(new Slot(this.player.getInventory(), column, 21 + 18 * column, this.height - 24));
    }

    public BlockPos getBlockEntityPos(){
        return this.blockEntityPos;
    }

    public TrashCanBlockEntity getBlockEntity(){
        return this.object;
    }
}
