package com.mowmaster.mowlib.Compat.JEI.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mowmaster.mowlib.Compat.JEI.JEIRecipeTypes;
import com.mowmaster.mowlib.Recipes.MachineBaseTypeRecipe;
import com.mowmaster.mowlib.Registry.DeferredRegisterItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class MachineBaseCraftingRecipeCategory implements IRecipeCategory<MachineBaseTypeRecipe>
{
    private final IDrawable background;
    private final Component localizedName;
    //private final IDrawable overlay;
    private final IDrawable icon;
    private final ItemStack renderStack = new ItemStack(DeferredRegisterItems.ICON_MACHINEBASE.get());

    public MachineBaseCraftingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(
                new ResourceLocation(MODID, "textures/gui/jei/machine_workstation.png"), 0, 0, 128, 128);
        this.localizedName = Component.translatable(MODID + ".jei.machine_crafting");
        //this.overlay =
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, this.renderStack);
        //this.renderStack.getOrCreateTag().putBoolean("RenderFull", true);
    }

    @Override
    public RecipeType<MachineBaseTypeRecipe> getRecipeType() {
        return JEIRecipeTypes.MACHINEBASE_CRAFING;
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
    public void setRecipe(IRecipeLayoutBuilder builder, MachineBaseTypeRecipe recipe, IFocusGroup focuses) {

        //Mainhand
        builder.addSlot(RecipeIngredientRole.INPUT, 76, 33)
                .addIngredients(recipe.getIngredients().get(0));
        //Block Clicked On
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 63)
                .addItemStack(new ItemStack(DeferredRegisterItems.ICON_MACHINEBASE.get()));
        //Result
        builder.addSlot(RecipeIngredientRole.OUTPUT, 22, 95)
                .addItemStack(recipe.getResultItemJEI());
    }

    @Override
    public void draw(MachineBaseTypeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
    }
}
