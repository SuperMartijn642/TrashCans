package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class PacketChangeItemFilter extends BlockEntityBasePacket<TrashCanBlockEntity> {

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
    public void write(FriendlyByteBuf buffer){
        super.write(buffer);
        buffer.writeInt(this.filterSlot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buffer, this.stack);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        super.read(buffer);
        this.filterSlot = buffer.readInt();
        this.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buffer);
    }

    @Override
    public boolean verify(PacketContext context){
        return this.filterSlot >= 0 && this.filterSlot < 9;
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.items){
            entity.itemFilter.set(this.filterSlot, this.stack);
            entity.dataChanged();
        }
    }
}
