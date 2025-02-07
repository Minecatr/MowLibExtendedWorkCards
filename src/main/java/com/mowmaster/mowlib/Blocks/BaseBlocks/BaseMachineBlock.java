package com.mowmaster.mowlib.Blocks.BaseBlocks;

import com.mowmaster.mowlib.Items.ColorApplicator;
import com.mowmaster.mowlib.MowLibUtils.MowLibColorReference;
import com.mowmaster.mowlib.MowLibUtils.MowLibContainerUtils;
import com.mowmaster.mowlib.Recipes.MachineBaseTypeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class BaseMachineBlock extends BaseColoredBlock
{
    public BaseMachineBlock(Properties p_152915_) {
        super(p_152915_);
    }

    @Nullable
    protected MachineBaseTypeRecipe getRecipe(Level level, ItemStack stackIn) {
        Container cont = MowLibContainerUtils.getContainer(1);
        cont.setItem(-1,stackIn);
        List<MachineBaseTypeRecipe> recipes = level.getRecipeManager().getRecipesFor(MachineBaseTypeRecipe.Type.INSTANCE,cont,level);
        return recipes.size() > 0 ? level.getRecipeManager().getRecipesFor(MachineBaseTypeRecipe.Type.INSTANCE,cont,level).get(0) : null;
    }

    protected Collection<ItemStack> getProcessResults(Level level, MachineBaseTypeRecipe recipe) {
        return (recipe == null)?(Arrays.asList(ItemStack.EMPTY)):(Collections.singleton(recipe.getResultItem(level.registryAccess())));
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {

        if(!p_60504_.isClientSide())
        {
            ItemStack itemInHand = p_60506_.getItemInHand(p_60507_);
            ItemStack itemInMainHand = p_60506_.getMainHandItem();
            ItemStack itemInOffHand = p_60506_.getOffhandItem();
            int getColor = MowLibColorReference.getColorFromStateInt(p_60503_);

            if(itemInMainHand.getItem() instanceof ColorApplicator)
            {
                getColor = MowLibColorReference.getColorFromItemStackInt(itemInMainHand);
                BlockState newState = MowLibColorReference.addColorToBlockState(p_60503_,getColor);
                p_60504_.setBlock(p_60505_,newState,3);
                return InteractionResult.SUCCESS;
            }
            else if(itemInOffHand.getItem() instanceof ColorApplicator)
            {
                getColor = MowLibColorReference.getColorFromItemStackInt(itemInOffHand);
                BlockState newState = MowLibColorReference.addColorToBlockState(p_60503_,getColor);
                p_60504_.setBlock(p_60505_,newState,3);
                return InteractionResult.SUCCESS;
            }
            else
            {
                Collection<ItemStack> jsonResults = getProcessResults(p_60504_, getRecipe(p_60504_,itemInHand));
                ItemStack returnedRecipe = ItemStack.EMPTY;
                returnedRecipe = (jsonResults.iterator().next().isEmpty())?(ItemStack.EMPTY):(jsonResults.iterator().next());
                if(!jsonResults.iterator().next().isEmpty())
                {
                    Block block = Blocks.AIR;
                    BlockState blockstate = block.defaultBlockState();

                    if(returnedRecipe.getItem() instanceof BlockItem)
                    {
                        block = Block.byItem(returnedRecipe.getItem());
                        blockstate = p_60504_.getBlockState(p_60505_);

                        BlockState blockstate1 = MowLibColorReference.addColorToBlockState(block.defaultBlockState(),getColor);
                        p_60504_.setBlockAndUpdate(p_60505_, blockstate1);
                        itemInHand.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                    else if (!returnedRecipe.isEmpty())
                    {
                        //if(returnedRecipe.getItem().getRegistryName().getNamespace().equals(MODID))
                        if(ForgeRegistries.ITEMS.getKey(returnedRecipe.getItem()).getNamespace().equals(MODID))
                        {
                            MowLibColorReference.addColorToItemStack(returnedRecipe,getColor);
                        }
                        ItemEntity itemEn = new ItemEntity(p_60504_,p_60505_.getX()+0.5,p_60505_.getY()+0.5,p_60505_.getZ()+0.5,returnedRecipe);
                        itemEn.setInvulnerable(true);
                        itemEn.setUnlimitedLifetime();
                        p_60504_.addFreshEntity(itemEn);
                        itemInHand.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

        }
        return InteractionResult.SUCCESS;
    }
}
