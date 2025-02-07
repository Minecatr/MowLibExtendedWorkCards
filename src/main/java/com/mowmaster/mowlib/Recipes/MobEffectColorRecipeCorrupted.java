package com.mowmaster.mowlib.Recipes;

import com.google.gson.JsonObject;
import com.mowmaster.mowlib.MowLibUtils.MowLibColorReference;
import com.mowmaster.mowlib.Registry.DeferredRegisterItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class MobEffectColorRecipeCorrupted implements Recipe<Container>
{
    @ObjectHolder(registryName = "forge:recipe_serializer", value = MODID + ":mobeffectcorrupted_color")

    private final String group;
    private final ResourceLocation id;
    @Nullable
    private final Ingredient input;
    private final int color;
    private final String mobEffect;
    private final int instaTickDuration;

    public MobEffectColorRecipeCorrupted(ResourceLocation id, String group, @Nullable Ingredient input, int color, String mobEffect, int instaTickDuration)
    {
        this.group = group;
        this.id = id;
        this.input = input;
        this.color = color;
        this.mobEffect = mobEffect;
        this.instaTickDuration = instaTickDuration;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static Collection<MobEffectColorRecipeCorrupted> getAllRecipes(Level world)
    {
        return world.getRecipeManager().getAllRecipesFor(Type.INSTANCE);
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return new ItemStack(Items.BARRIER);
    }

    public ItemStack getResultItemJEI()
    {
        return new ItemStack(Items.BARRIER);
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> allIngredients = NonNullList.create();
        allIngredients.add(input != null ? input : Ingredient.EMPTY);
        return allIngredients;
    }

    @Override
    public boolean matches(Container inv, Level worldIn)
    {
        ItemStack inputStack = inv.getItem(0);
        int count = 0;
        if(inputStack.hasTag())
        {
            if(inputStack.getTag().contains(MODID+"_color"))
            {
                count = inputStack.getTag().getInt(MODID+"_color");
            }
        }
        return input.test(inputStack) && count == color;
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_)
    {
        return getResultItem(p_267165_).copy();
    }

    public int getResultEffectColor() { return getEffectColor(); }

    public int getResultInstantTickDuration() { return getInstantTickDuration(); }

    public String getResultEffectName()
    {
        return getEffectName();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MobEffectColorRecipeCorrupted> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "mobeffectcorrupted_color";
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(DeferredRegisterItems.COLOR_APPLICATOR.get());
    }


    public int getEffectColor()
    {
        return color;
    }

    public String getEffectName()
    {
        return mobEffect;
    }

    public int getInstantTickDuration()
    {
        return instaTickDuration;
    }

    public Ingredient getPattern()
    {
        return input != null ? input : Ingredient.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<MobEffectColorRecipeCorrupted>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(MODID,"mobeffectcorrupted_color");

        protected MobEffectColorRecipeCorrupted createRecipe(ResourceLocation recipeId, String group, @Nullable Ingredient input, int color, String mobEffect, int instaTickDuration)
        {
            return new MobEffectColorRecipeCorrupted(recipeId, group, input, color, mobEffect, instaTickDuration);
        }

        @Override
        public MobEffectColorRecipeCorrupted fromJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient input = json.has("input") ? CraftingHelper.getIngredient(json.get("input"),false) : null;
            int color = json.has("color") ? GsonHelper.getAsInt(json,"color") : (MowLibColorReference.DEFAULTCOLOR);
            String mobEffect = json.has("mobEffect") ? GsonHelper.getAsString(json,"mobEffect") : "";
            int instaTickDuration = json.has("instaTickDuration") ? GsonHelper.getAsInt(json,"instaTickDuration") : 1;
            return createRecipe(recipeId, group, input, color, mobEffect, instaTickDuration);
        }

        @Override
        public MobEffectColorRecipeCorrupted fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf(32767);
            boolean hasInput = buffer.readBoolean();
            Ingredient input = hasInput ? Ingredient.fromNetwork(buffer) : null;
            int color = buffer.readInt();
            String mobEffect = buffer.readUtf(32767);
            int instaTickDuration = buffer.readInt();
            return createRecipe(recipeId, group,  input, color, mobEffect, instaTickDuration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MobEffectColorRecipeCorrupted recipe)
        {
            buffer.writeUtf(recipe.group);
            boolean hasInput = recipe.input != null;
            buffer.writeBoolean(hasInput);
            if (hasInput) recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.color);
            buffer.writeUtf(recipe.mobEffect);
            buffer.writeInt(recipe.instaTickDuration);
        }


        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        public ResourceLocation getRegistryName() {
            return ID;
        }

        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
