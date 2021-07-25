package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PacketChangeItemFilter extends TrashCanPacket {
    private int filterSlot;
    private ItemStack stack;

    public PacketChangeItemFilter(BlockPos pos, int filterSlot, ItemStack stack){
        super(pos);
        this.filterSlot = filterSlot;
        this.stack = stack;
    }

    public PacketChangeItemFilter(FriendlyByteBuf buffer){
        super(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.filterSlot);
        buffer.writeItem(this.stack);
    }

    @Override
    protected void decodeBuffer(FriendlyByteBuf buffer){
        super.decodeBuffer(buffer);
        this.filterSlot = buffer.readInt();
        this.stack = buffer.readItem();
    }

    public static PacketChangeItemFilter decode(FriendlyByteBuf buffer){
        return new PacketChangeItemFilter(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.items){
            tile.itemFilter.set(this.filterSlot, this.stack);
            tile.dataChanged();
        }
    }
}
