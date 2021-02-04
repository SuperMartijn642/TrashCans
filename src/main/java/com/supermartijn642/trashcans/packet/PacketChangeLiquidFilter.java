package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketChangeLiquidFilter extends TrashCanPacket<PacketChangeLiquidFilter> {
    private int filterSlot;
    private ItemFilter filter;

    public PacketChangeLiquidFilter(BlockPos pos, int filterSlot, ItemFilter filter){
        super(pos);
        this.filterSlot = filterSlot;
        this.filter = filter;
    }

    public PacketChangeLiquidFilter(){
    }

    @Override
    public void encode(ByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.filterSlot);
        ByteBufUtils.writeTag(buffer, LiquidTrashCanFilters.write(this.filter));
    }

    @Override
    protected void decodeBuffer(ByteBuf buffer){
        super.decodeBuffer(buffer);
        this.filterSlot = buffer.readInt();
        this.filter = LiquidTrashCanFilters.read(ByteBufUtils.readTag(buffer));
    }

    @Override
    protected void handle(PacketChangeLiquidFilter message, EntityPlayer player, World world, TrashCanTile tile){
        if(tile.liquids){
            tile.liquidFilter.set(this.filterSlot, this.filter);
            tile.dataChanged();
        }
    }
}
