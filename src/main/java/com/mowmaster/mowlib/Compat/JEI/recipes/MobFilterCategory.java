package com.mowmaster.mowlib.Compat.JEI.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mowmaster.mowlib.Compat.JEI.EntityDrawable;
import com.mowmaster.mowlib.Compat.JEI.JEIRecipeTypes;
import com.mowmaster.mowlib.Recipes.BaseBlockEntityFilter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class MobFilterCategory implements IRecipeCategory<BaseBlockEntityFilter>
{
    private final IDrawable background;
    private final Component localizedName;
    //private final IDrawable overlay;
    private final IDrawable icon;
    private final ItemStack renderStack = new ItemStack(Items.SKELETON_SKULL).setHoverName(Component.translatable(MODID + ".jei.mob_filter_icon"));

    public MobFilterCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(
                new ResourceLocation(MODID, "textures/gui/jei/mob_filter.png"), 0, 0, 176, 32);
        this.localizedName = Component.translatable(MODID + ".jei.mob_filter");
        //this.overlay =
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, this.renderStack);
        //this.renderStack.getOrCreateTag().putBoolean("RenderFull", true);
    }

    @Override
    public RecipeType<BaseBlockEntityFilter> getRecipeType() {
        return JEIRecipeTypes.MOB_FILTER;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaseBlockEntityFilter recipe, IFocusGroup focuses) {

        //Input Color
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 8)
                .addIngredients(recipe.getIngredients().get(0));

        //Result
        //https://github.com/Kaupenjoe/Resource-Slimes/blob/b6b46acba76b64687b8c64a94d84db0f0c03fdff/src/main/java/net/kaupenjoe/resourceslimes/integration/SlimeIncubationStationRecipeCategory.java#L17
        //entityIn.getClassification(false).equals(MobCategory.byName(getProcessResultFilterBlock(filter)))
        //EntityType.byString(getProcessResultFilterBlock(filter)
        /*builder.addSlot(RecipeIngredientRole.OUTPUT, 43, 24)
                .setOverlay(new EntityDrawable(32,32,),0,0);*/
    }

    @Override
    public void draw(BaseBlockEntityFilter recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        Font fontRenderer = Minecraft.getInstance().font;

        int mobType = recipe.getResultMobType();


        MutableComponent mobTypeText = Component.translatable(MODID + ".mob_filter_type");
        if(mobType ==1) mobTypeText = Component.translatable(MODID + ".mob_filter_entity");
        mobTypeText.withStyle(ChatFormatting.BLACK);
        fontRenderer.draw(stack,mobTypeText,45,7,0xffffffff);

        MutableComponent mobName = (mobType ==0)?(Component.translatable(MODID + ".mob_filter." + MobCategory.byName(recipe.getEntityString()).getName())):(EntityType.byString(recipe.getEntityString()).get().getDescription().copy());
        if(recipe.getResultBaby()) mobName.append(Component.translatable(MODID + ".mob_filter_baby"));
        mobName.withStyle(ChatFormatting.BLACK);
        fontRenderer.draw(stack,mobName,45,19,0xffffffff);
        //this.overlay.draw(stack, 48, 0);
    }
}
