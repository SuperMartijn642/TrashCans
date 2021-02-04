package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketChangeItemFilter extends TrashCanPacket<PacketChangeItemFilter> {
    private int filterSlot;
    private ItemStack stack;

    public PacketChangeItemFilter(BlockPos pos, int filterSlot, ItemStack stack){
        super(pos);
        this.filterSlot = filterSlot;
        this.stack = stack;
    }

    public PacketChangeItemFilter(){
    }

    @Override
    public void encode(ByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.filterSlot);
        ByteBufUtils.writeItemStack(buffer, this.stack);
    }

    @Override
    protected void decodeBuffer(ByteBuf buffer){
        super.decodeBuffer(buffer);
        this.filterSlot = buffer.readInt();
        this.stack = ByteBufUtils.readItemStack(buffer);
    }

    @Override
    protected void handle(PacketChangeItemFilter message, EntityPlayer player, World world, TrashCanTile tile){
        if(tile.items){
            tile.itemFilter.set(this.filterSlot, this.stack);
            tile.dataChanged();
        }
    }
}
