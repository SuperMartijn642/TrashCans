package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketChangeLiquidFilter extends TrashCanPacket {
    private int filterSlot;
    private ItemFilter filter;

    public PacketChangeLiquidFilter(BlockPos pos, int filterSlot, ItemFilter filter) {
        super(pos);
        this.filterSlot = filterSlot;
        this.filter = filter;
    }

    public PacketChangeLiquidFilter(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        super.encode(buffer);
        buffer.writeInt(filterSlot);
        buffer.writeCompoundTag(LiquidTrashCanFilters.write(filter));
    }

    @Override
    protected void decodeBuffer(PacketBuffer buffer) {
        super.decodeBuffer(buffer);
        filterSlot = buffer.readInt();
        filter = LiquidTrashCanFilters.read(buffer.readCompoundTag());
    }

    public static PacketChangeLiquidFilter decode(PacketBuffer buffer) {
        return new PacketChangeLiquidFilter(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, TrashCanTile tile) {
        if (tile.liquids) {
            tile.liquidFilter.set(filterSlot, filter);
            tile.dataChanged();
        }
    }
}
