package com.supermartijn642.simplemagnets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlock extends Block {

    private static final VoxelShape SHAPE =
        VoxelShapes.or(VoxelShapes.create(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d),
        VoxelShapes.create(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d),
        VoxelShapes.create(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d));

    private final Supplier<? extends TileEntity> provider;

    public TrashCanBlock(String registryName, Supplier<? extends TileEntity> provider){
        super(Properties.create(Material.IRON, MaterialColor.GRAY).hardnessAndResistance(1.5f, 6).harvestLevel(1).harvestTool(ToolType.PICKAXE));
        this.setRegistryName(registryName);
        this.provider = provider;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return this.provider.get();
    }
}
