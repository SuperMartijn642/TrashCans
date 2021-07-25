package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PacketChangeLiquidFilter extends TrashCanPacket {
    private int filterSlot;
    private ItemFilter filter;

    public PacketChangeLiquidFilter(BlockPos pos, int filterSlot, ItemFilter filter){
        super(pos);
        this.filterSlot = filterSlot;
        this.filter = filter;
    }

    public PacketChangeLiquidFilter(FriendlyByteBuf buffer){
        super(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer){
        super.encode(buffer);
        buffer.writeInt(this.filterSlot);
        buffer.writeNbt(LiquidTrashCanFilters.write(this.filter));
    }

    @Override
    protected void decodeBuffer(FriendlyByteBuf buffer){
        super.decodeBuffer(buffer);
        this.filterSlot = buffer.readInt();
        this.filter = LiquidTrashCanFilters.read(buffer.readNbt());
    }

    public static PacketChangeLiquidFilter decode(FriendlyByteBuf buffer){
        return new PacketChangeLiquidFilter(buffer);
    }

    @Override
    protected void handle(Player player, Level world, TrashCanTile tile){
        if(tile.liquids){
            tile.liquidFilter.set(this.filterSlot, this.filter);
            tile.dataChanged();
        }
    }
}
