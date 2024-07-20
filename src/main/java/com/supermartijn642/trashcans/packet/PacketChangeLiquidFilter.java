package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PacketChangeLiquidFilter extends BlockEntityBasePacket<TrashCanBlockEntity> {
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
    public void write(FriendlyByteBuf buffer){
        super.write(buffer);
        buffer.writeInt(this.filterSlot);
        buffer.writeNbt(LiquidTrashCanFilters.write(this.filter, ((RegistryFriendlyByteBuf)buffer).registryAccess()));
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        super.read(buffer);
        this.filterSlot = buffer.readInt();
        this.filter = LiquidTrashCanFilters.read(buffer.readNbt(), ((RegistryFriendlyByteBuf)buffer).registryAccess());
    }

    @Override
    public boolean verify(PacketContext context){
        return this.filterSlot >= 0 && this.filterSlot < 9;
    }

    @Override
    protected void handle(TrashCanBlockEntity entity, PacketContext context){
        if(entity.liquids){
            entity.liquidFilter.set(this.filterSlot, this.filter);
            entity.dataChanged();
        }
    }
}
