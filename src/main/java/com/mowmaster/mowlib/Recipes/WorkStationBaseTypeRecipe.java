package com.mowmaster.mowlib.Recipes;

import com.google.gson.JsonObject;
import com.mowmaster.mowlib.Registry.DeferredRegisterItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
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

public class WorkStationBaseTypeRecipe implements Recipe<Container>
{

    @ObjectHolder(registryName = "forge:recipe_serializer", value = MODID + ":workstationbase")

    private final String group;
    private final ResourceLocation id;
    @Nullable
    private final Ingredient input;
    private final ItemStack output;

    public WorkStationBaseTypeRecipe(ResourceLocation id, String group, @Nullable Ingredient input, ItemStack output)
    {
        this.group = group;
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static Collection<WorkStationBaseTypeRecipe> getAllRecipes(Level world)
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
        return input.test(inputStack);
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_)
    {
        return getResultItem(p_267165_).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_)
    {
        return output;
    }

    public ItemStack getResultItemJEI()
    {
        return output;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WorkStationBaseTypeRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return WorkStationBaseTypeRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<WorkStationBaseTypeRecipe> {
        private Type() { }
        public static final WorkStationBaseTypeRecipe.Type INSTANCE = new WorkStationBaseTypeRecipe.Type();
        public static final String ID = "workstationbase";
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(DeferredRegisterItems.ICON_WORKSTATIONBASE.get());
    }


    public Ingredient getPattern()
    {
        return input != null ? input : Ingredient.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<WorkStationBaseTypeRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(MODID,"workstationbase");


        protected WorkStationBaseTypeRecipe createRecipe(ResourceLocation recipeId, String group, Ingredient input, ItemStack result)
        {
            return new WorkStationBaseTypeRecipe(recipeId, group, input, result);
        }

        @Override
        public WorkStationBaseTypeRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient input = json.has("input") ? CraftingHelper.getIngredient(json.get("input"),false) : null;
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return createRecipe(recipeId, group, input, result);
        }

        @Override
        public WorkStationBaseTypeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf(32767);
            boolean hasInput = buffer.readBoolean();
            Ingredient input = hasInput ? Ingredient.fromNetwork(buffer) : null;
            ItemStack result = buffer.readItem();
            return createRecipe(recipeId, group,  input, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WorkStationBaseTypeRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            boolean hasInput = recipe.input != null;
            buffer.writeBoolean(hasInput);
            if (hasInput) recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
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
