package com.supermartijn642.trashcans;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created 7/10/2020 by SuperMartijn642
 */
public class TrashCanBlock extends BaseBlock {

    private final Supplier<? extends TileEntity> tileProvider;
    private final int guiId;

    public TrashCanBlock(String registryName, Supplier<? extends TileEntity> tileProvider, int guiId){
        super(registryName, false, Properties.create(Material.IRON, MapColor.GRAY).hardnessAndResistance(1.5f, 6).harvestLevel(1).harvestTool(ToolType.PICKAXE));
        this.tileProvider = tileProvider;
        this.guiId = guiId;

        this.setCreativeTab(CreativeTabs.SEARCH);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(!worldIn.isRemote)
            playerIn.openGui(TrashCans.instance, this.guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return new AxisAlignedBB(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d)
            .union(new AxisAlignedBB(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d))
            .union(new AxisAlignedBB(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
        AxisAlignedBB box = new AxisAlignedBB(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 12 / 16d, 13 / 16d);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        box = new AxisAlignedBB(2 / 16d, 12 / 16d, 2 / 16d, 14 / 16d, 13 / 16d, 14 / 16d);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        box = new AxisAlignedBB(3 / 16d, 12.5 / 16d, 3 / 16d, 13 / 16d, 13.5 / 16d, 13 / 16d);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return this.tileProvider.get();
    }

    @Override
    public BlockRenderLayer getBlockLayer(){
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World level, List<String> text, ITooltipFlag flag){
        text.add(TextComponents.translation("trashcans." + this.getRegistryName().getResourcePath() + ".info").color(TextFormatting.GRAY).format());
        super.addInformation(stack, level, text, flag);
    }
}
