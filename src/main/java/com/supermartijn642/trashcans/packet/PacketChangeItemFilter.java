package com.supermartijn642.trashcans.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
    public void write(PacketBuffer buffer){
        super.write(buffer);
        buffer.writeInt(this.filterSlot);
        ByteBufUtils.writeItemStack(buffer, this.stack);
    }

    @Override
    public void read(PacketBuffer buffer){
        super.read(buffer);
        this.filterSlot = buffer.readInt();
        this.stack = ByteBufUtils.readItemStack(buffer);
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
