package com.mowmaster.mowlib.BlockEntities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mowmaster.mowlib.MowLibUtils.MowLibRenderUtils;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BaseBuiltFueledMachineBlockEntityRenderer
{

    public void render(BaseBuiltFueledMachineBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

    }

    public static void renderItemRotating(Level worldIn, PoseStack p_112309_, MultiBufferSource p_112310_, ItemStack itemStack, int p_112311_, int p_112312_)
    {
        if (!itemStack.isEmpty()) {
            p_112309_.pushPose();
            p_112309_.translate(0.5, 1.25, 0.65);
            //p_112309_.translate(0, MathHelper.sin((worldIn.getGameTime()) / 10.0F) * 0.1 + 0.1, 0); BOBBING ITEM
            p_112309_.scale(0.5F, 0.5F, 0.5F);
            long time = System.currentTimeMillis();
            float angle = (time/50) % 360;
            //float angle = (worldIn.getGameTime()) / 20.0F * (180F / (float) Math.PI);
            //p_112309_.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), angle, true));
            p_112309_.mulPose(Axis.YP.rotationDegrees(angle));
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel baked = renderer.getModel(itemStack,worldIn,null,0);
            renderer.render(itemStack, ItemDisplayContext.GROUND,true,p_112309_,p_112310_,p_112311_,p_112312_,baked);

            //Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.GROUND, p_112311_, p_112312_, p_112309_, p_112310_);
            p_112309_.popPose();
        }
    }

    public static BlockState getLitBlockState(Block block, boolean lit)
    {
        if(block.defaultBlockState().hasProperty(BlockStateProperties.LIT))
        {
            return block.defaultBlockState().setValue(BlockStateProperties.LIT,lit);
        }
        else return block.defaultBlockState();
    }

    public static void renderItem(BaseBuiltFueledMachineBlockEntity tile, Level worldIn, PoseStack p_112309_, MultiBufferSource p_112310_, ItemStack itemStack, int p_112311_, int p_112312_, float x, float y, float z, float scaleX, float scaleY, float scaleZ, float angle, int axis, boolean renderAsBlock)
    {
        if (!itemStack.isEmpty()) {
            p_112309_.pushPose();
            p_112309_.translate(x, y, z);
            p_112309_.scale(scaleX, scaleY, scaleZ);
            switch(axis)
            {

                case 1: p_112309_.mulPose(Axis.XP.rotationDegrees(angle));
                    break;
                case 2: p_112309_.mulPose(Axis.ZP.rotationDegrees(angle));
                    break;
                default: p_112309_.mulPose(Axis.YP.rotationDegrees(angle));
                    break;
            }
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BlockRenderDispatcher renderBlock = Minecraft.getInstance().getBlockRenderer();
            BakedModel baked = renderer.getModel(itemStack,worldIn,null,0);
            if(renderAsBlock)
            {
                BlockState block = getLitBlockState(Block.byItem(itemStack.getItem()), tile.isFurnaceLit());
                baked = renderBlock.getBlockModel(block);
            }
            renderer.render(itemStack, ItemDisplayContext.FIXED,true,p_112309_,p_112310_,p_112311_,p_112312_,baked);
            p_112309_.popPose();
        }
    }

    public static void renderItemStacked(BaseBuiltFueledMachineBlockEntity tile, Level worldIn, PoseStack p_112309_, MultiBufferSource p_112310_, ItemStack itemStack, int p_112311_, int p_112312_, float x, float y, float z, float scaleX, float scaleY, float scaleZ, float angle, boolean renderAsBlock)
    {
        if (!itemStack.isEmpty()) {
            int displayLayersByStackSize = itemStack.getCount();
            if(!itemStack.isEmpty() && displayLayersByStackSize==0) displayLayersByStackSize=1;
            p_112309_.pushPose();
            if(renderAsBlock)
            {
                renderItem(tile,worldIn,p_112309_,p_112310_,itemStack,p_112311_,p_112312_, x, y, z, scaleX, scaleY, scaleZ,0.0f,0, renderAsBlock);
            }
            else
            {
                for(int i=0;i<displayLayersByStackSize;i++)
                {
                    p_112309_.translate(0, 0.04, 0);
                    renderItem(tile,worldIn,p_112309_,p_112310_,itemStack,p_112311_,p_112312_, x, y, z, scaleX, scaleY, scaleZ,angle,1, renderAsBlock);
                }
            }
            p_112309_.popPose();
        }
    }

    public boolean shouldRenderOffScreen(BaseBuiltFueledMachineBlockEntity p_112306_) {
        return true;
    }
}
