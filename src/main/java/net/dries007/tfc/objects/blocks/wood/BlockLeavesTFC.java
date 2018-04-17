package net.dries007.tfc.objects.blocks.wood;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public class BlockLeavesTFC extends BlockLeaves
{
    private static final EnumMap<Wood, BlockLeavesTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockLeavesTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockLeavesTFC(Wood wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setDefaultState(blockState.getBaseState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
        leavesFancy = true; // there doesn't seem to be an even for catching changing this, so lets not bother
        OreDictionaryHelper.register(this, "tree", "leaves");
        OreDictionaryHelper.register(this, "tree", "leaves", wood);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DECAYABLE, (meta & 0b01) == 0b01).withProperty(CHECK_DECAY, (meta & 0b10) == 0b10);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(DECAYABLE) ? 0b01 : 0) | (state.getValue(CHECK_DECAY) ? 0b10 : 0);
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta)
    {
        return null;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return ImmutableList.of(new ItemStack(this));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(BlockSaplingTFC.get(wood));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        //Player will take damage when falling through leaves if fall is over 9 blocks, fall damage is then set to 0. 
        entityIn.fall((entityIn.fallDistance - 6), 1.0F); // TODO: 17/4/18 Balance fall distance reduction. 
        entityIn.fallDistance = 0;
        //Entity motion is reduced by leaves.
        entityIn.motionX *= 0.1D;
        entityIn.motionY *= 0.1D;
        entityIn.motionZ *= 0.1D;
    }
}
