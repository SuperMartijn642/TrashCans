package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.*;
import com.supermartijn642.core.registry.Registries;
import com.supermartijn642.trashcans.screen.TrashCanContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlock extends BaseBlock implements EntityHoldingBlock {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.create(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d),
        BlockShape.create(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d),
        BlockShape.create(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d)
    );

    private final Supplier<BaseBlockEntityType<?>> blockEntityType;
    private final BiFunction<EntityPlayer,BlockPos,TrashCanContainer> containerProvider;

    public TrashCanBlock(Supplier<BaseBlockEntityType<?>> blockEntityType, BiFunction<EntityPlayer,BlockPos,TrashCanContainer> containerProvider){
        super(false, BlockProperties.create(Material.IRON, MapColor.GRAY).destroyTime(1.5f).explosionResistance(6).requiresCorrectTool());
        this.blockEntityType = blockEntityType;
        this.containerProvider = containerProvider;
    }

    @Override
    protected InteractionFeedback interact(IBlockState state, World level, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation){
        if(!level.isRemote)
            CommonUtils.openContainer(this.containerProvider.apply(player, pos));
        return InteractionFeedback.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return SHAPE.simplify();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
        SHAPE.forEachBox(box -> addCollisionBoxToList(pos, entityBox, collidingBoxes, box));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.blockEntityType.get().createBlockEntity();
    }

    @Override
    protected void appendItemInformation(ItemStack stack, IBlockAccess level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("trashcans." + Registries.BLOCKS.getIdentifier(this).getResourcePath() + ".info").color(TextFormatting.GRAY).get());
    }
}
