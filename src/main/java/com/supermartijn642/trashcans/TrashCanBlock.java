package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.*;
import com.supermartijn642.core.registry.Registries;
import com.supermartijn642.trashcans.screen.TrashCanContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlock extends BaseBlock implements EntityHoldingBlock, IWaterLoggable {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.create(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d),
        BlockShape.create(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d),
        BlockShape.create(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d)
    );

    private final Supplier<BaseBlockEntityType<?>> blockEntityType;
    private final BiFunction<PlayerEntity,BlockPos,TrashCanContainer> containerProvider;

    public TrashCanBlock(Supplier<BaseBlockEntityType<?>> blockEntityType, BiFunction<PlayerEntity,BlockPos,TrashCanContainer> containerProvider){
        super(false, BlockProperties.create(Material.METAL, MaterialColor.COLOR_GRAY).destroyTime(1.5f).explosionResistance(6).requiresCorrectTool());
        this.blockEntityType = blockEntityType;
        this.containerProvider = containerProvider;

        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected InteractionFeedback interact(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide, Vector3d hitLocation){
        if(!level.isClientSide)
            CommonUtils.openContainer(this.containerProvider.apply(player, pos));
        return InteractionFeedback.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        return SHAPE.getUnderlying();
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.blockEntityType.get().create();
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos){
        if(stateIn.getValue(WATERLOGGED))
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder){
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, IBlockReader level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("trashcans." + Registries.BLOCKS.getIdentifier(this).getPath() + ".info").color(TextFormatting.GRAY).get());
    }
}
