package com.supermartijn642.trashcans.packet;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanPacket {

    protected BlockPos pos;

    public TrashCanPacket(BlockPos pos){
        this.pos = pos;
    }

    public TrashCanPacket(FriendlyByteBuf buffer){
        this.decodeBuffer(buffer);
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeBlockPos(this.pos);
    }

    protected void decodeBuffer(FriendlyByteBuf buffer){
        this.pos = buffer.readBlockPos();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        Player player = contextSupplier.get().getSender();
        if(player == null || player.position().distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > 32 * 32)
            return;
        Level world = player.level;
        if(world == null)
            return;
        BlockEntity tile = world.getBlockEntity(this.pos);
        if(tile instanceof TrashCanTile)
            contextSupplier.get().enqueueWork(() -> this.handle(player, world, (TrashCanTile)tile));
    }

    protected abstract void handle(Player player, Level world, TrashCanTile tile);
}
