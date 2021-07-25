package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketChangeItemFilter extends TrashCanPacket {
    private int filterSlot;
    private ItemStack stack;

    public PacketChangeItemFilter(BlockPos pos, int filterSlot, ItemStack stack){
        super(pos);
        this.filterSlot = filterSlot;
        this.stack = stack;
    }

    public PacketChangeItemFilter(PacketBuffer buffer){
        super(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer){
        super.encode(buffer);
        buffer.writeInt(this.filterSlot);
        buffer.writeItem(this.stack);
    }

    @Override
    protected void decodeBuffer(PacketBuffer buffer){
        super.decodeBuffer(buffer);
        this.filterSlot = buffer.readInt();
        this.stack = buffer.readItem();
    }

    public static PacketChangeItemFilter decode(PacketBuffer buffer){
        return new PacketChangeItemFilter(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, TrashCanTile tile){
        if(tile.items){
            tile.itemFilter.set(this.filterSlot, this.stack);
            tile.dataChanged();
        }
    }
}
