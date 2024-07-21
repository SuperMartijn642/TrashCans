package com.supermartijn642.trashcans;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.EntityHoldingBlock;
import com.supermartijn642.core.registry.Registries;
import com.supermartijn642.trashcans.screen.TrashCanContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlock extends BaseBlock implements EntityHoldingBlock, SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE =
        Shapes.or(Shapes.box(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d),
            Shapes.box(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d),
            Shapes.box(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d));

    private final Supplier<BaseBlockEntityType<?>> blockEntityType;
    private final BiFunction<Player,BlockPos,TrashCanContainer> containerProvider;

    public TrashCanBlock(Supplier<BaseBlockEntityType<?>> blockEntityType, BiFunction<Player,BlockPos,TrashCanContainer> containerProvider){
        super(false, BlockProperties.create().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).destroyTime(1.5f).explosionResistance(6).requiresCorrectTool());
        this.blockEntityType = blockEntityType;
        this.containerProvider = containerProvider;

        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected InteractionFeedback interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, Direction hitSide, Vec3 hitLocation){
        if(!level.isClientSide)
            CommonUtils.openContainer(this.containerProvider.apply(player, pos));
        return InteractionFeedback.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
        return SHAPE;
    }

    @Override
    public BlockEntity createNewBlockEntity(BlockPos pos, BlockState state){
        return this.blockEntityType.get().create(pos, state);
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos){
        if(stateIn.getValue(WATERLOGGED))
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("trashcans." + Registries.BLOCKS.getIdentifier(this).getPath() + ".info").color(ChatFormatting.GRAY).get());
    }
}
