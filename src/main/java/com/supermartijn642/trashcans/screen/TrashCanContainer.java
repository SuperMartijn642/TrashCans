package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.TileEntityBaseContainer;
import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanContainer extends TileEntityBaseContainer<TrashCanTile> {

    public final int width, height;

    public TrashCanContainer(EntityPlayer player, BlockPos pos, int width, int height){
        super(player, player.world, pos);
        this.width = width;
        this.height = height;

        this.addSlots();
        this.addPlayerSlots();
    }

    private void addPlayerSlots(){
        // player
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                this.addSlotToContainer(new Slot(this.player.inventory, row * 9 + column + 9, 21 + 18 * column, this.height - 82 + 18 * row));
            }
        }

        // hot bar
        for(int column = 0; column < 9; column++)
            this.addSlotToContainer(new Slot(this.player.inventory, column, 21 + 18 * column, this.height - 24));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

    @Override
    public TrashCanTile getObjectOrClose(){
        return super.getObjectOrClose();
    }

    public BlockPos getTilePos(){
        return this.tilePos;
    }
}
