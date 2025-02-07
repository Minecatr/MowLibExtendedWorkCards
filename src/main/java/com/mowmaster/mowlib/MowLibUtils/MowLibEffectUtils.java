package com.mowmaster.mowlib.MowLibUtils;

import com.mowmaster.mowlib.Recipes.MobEffectColorRecipe;
import com.mowmaster.mowlib.Recipes.MobEffectColorRecipeCorrupted;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MowLibEffectUtils
{
    @Nullable
    protected static MobEffectColorRecipe getRecipeMobEffectColor(Level level, ItemStack stackIn) {
        Container container = MowLibContainerUtils.getContainer(1);
        container.setItem(-1,stackIn);
        List<MobEffectColorRecipe> recipes = level.getRecipeManager().getRecipesFor(MobEffectColorRecipe.Type.INSTANCE,container,level);
        return recipes.size() > 0 ? level.getRecipeManager().getRecipesFor(MobEffectColorRecipe.Type.INSTANCE,container,level).get(0) : null;
    }

    protected static String getProcessResultMobEffectColorRecipe(MobEffectColorRecipe recipe) {
        return (recipe == null)?(""):(recipe.getResultEffectName());
    }

    protected static int getProcessResultEffectColorRecipe(MobEffectColorRecipe recipe) {
        return (recipe == null)?(MowLibColorReference.DEFAULTCOLOR):(recipe.getResultEffectColor());
    }

    protected static int getProcessResultEffectInstantDurationRecipe(MobEffectColorRecipe recipe) {
        return (recipe == null)?(1):(recipe.getResultInstantTickDuration());
    }

    @Nullable
    protected static MobEffectColorRecipeCorrupted getRecipeMobEffectColorCorrupted(Level level, ItemStack stackIn) {
        Container container = MowLibContainerUtils.getContainer(1);
        container.setItem(-1,stackIn);
        List<MobEffectColorRecipeCorrupted> recipes = level.getRecipeManager().getRecipesFor(MobEffectColorRecipeCorrupted.Type.INSTANCE,container,level);
        return recipes.size() > 0 ? level.getRecipeManager().getRecipesFor(MobEffectColorRecipeCorrupted.Type.INSTANCE,container,level).get(0) : null;
    }

    protected static String getProcessResultMobEffectColorRecipeCorrupted(MobEffectColorRecipeCorrupted recipe) {
        return (recipe == null)?(""):(recipe.getResultEffectName());
    }

    protected static int getProcessResultEffectColorRecipeCorrupted(MobEffectColorRecipeCorrupted recipe) {
        return (recipe == null)?(MowLibColorReference.DEFAULTCOLOR):(recipe.getResultEffectColor());
    }

    protected static int getProcessResultEffectInstantDurationRecipeCorrupted(MobEffectColorRecipeCorrupted recipe) {
        return (recipe == null)?(1):(recipe.getResultInstantTickDuration());
    }

    public static MobEffect getEffectForColor(Level level, ItemStack colorableCrystal, boolean corruption, int currentColor)
    {
        if(corruption)
        {
            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            ResourceLocation location = new ResourceLocation(getProcessResultMobEffectColorRecipeCorrupted(getRecipeMobEffectColorCorrupted(level,stack)));
            if(ForgeRegistries.MOB_EFFECTS.containsKey(location))return ForgeRegistries.MOB_EFFECTS.getValue(location);
        }
        else if (!corruption)
        {
            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            ResourceLocation location = new ResourceLocation(getProcessResultMobEffectColorRecipe(getRecipeMobEffectColor(level,stack)));
            if(ForgeRegistries.MOB_EFFECTS.containsKey(location))return ForgeRegistries.MOB_EFFECTS.getValue(location);
        }

        return getRandomNegativeEffect();
    }

    public static int getColorForEffect(Level level, ItemStack colorableCrystal, boolean corruption, int currentColor)
    {
        if(corruption)
        {
            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            return getProcessResultEffectColorRecipeCorrupted(getRecipeMobEffectColorCorrupted(level,stack));
        }
        else if (!corruption)
        {

            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            return getProcessResultEffectColorRecipe(getRecipeMobEffectColor(level,stack));
        }

        return MowLibColorReference.DEFAULTCOLOR;
    }

    public static int getInstantDuration(Level level, ItemStack colorableCrystal, boolean corruption, int currentColor)
    {
        if(corruption)
        {
            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            return getProcessResultEffectInstantDurationRecipeCorrupted(getRecipeMobEffectColorCorrupted(level,stack));
        }
        else if (!corruption)
        {
            ItemStack stack = MowLibColorReference.addColorToItemStack(colorableCrystal,currentColor);
            return getProcessResultEffectInstantDurationRecipe(getRecipeMobEffectColor(level,stack));
        }

        return MowLibColorReference.DEFAULTCOLOR;
    }

    public static MobEffect getRandomNegativeEffect()
    {
        Random rand = new Random();
        Map<Integer, MobEffect> NEGEFFECT = Map.ofEntries(
                Map.entry(0, MobEffects.BAD_OMEN),
                Map.entry(1,MobEffects.BLINDNESS),
                Map.entry(2,MobEffects.GLOWING),
                Map.entry(3,MobEffects.HUNGER),
                Map.entry(4,MobEffects.HARM),
                Map.entry(5,MobEffects.LEVITATION),
                Map.entry(6,MobEffects.DIG_SLOWDOWN),
                Map.entry(7,MobEffects.CONFUSION),
                Map.entry(8,MobEffects.POISON),
                Map.entry(9,MobEffects.MOVEMENT_SLOWDOWN),
                Map.entry(10,MobEffects.UNLUCK),
                Map.entry(11,MobEffects.WEAKNESS),
                Map.entry(12,MobEffects.WITHER)
        );

        return NEGEFFECT.getOrDefault(rand.nextInt(13),MobEffects.HUNGER);
    }
}
