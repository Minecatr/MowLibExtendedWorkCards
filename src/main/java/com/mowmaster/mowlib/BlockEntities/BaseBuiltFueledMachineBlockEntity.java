package com.mowmaster.mowlib.BlockEntities;

import com.mowmaster.mowlib.MowLibUtils.MowLibContainerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mowmaster.mowlib.MowLibUtils.MowLibItemUtils.spawnItemStack;

public class BaseBuiltFueledMachineBlockEntity extends BaseBuiltMachineBlockEntity
{
    private LazyOptional<IItemHandler> allowedInputsHandler = LazyOptional.of(this::createAllowedInputsHandler);
    private List<ItemStack> stacksListAllowedInputsHandler = new ArrayList<>();
    private int burnTime = 0;
    private int currentCookTime = 0;
    private int maxCookTime = 0;
    private float xpForOutput = 0F;
    private ItemStack outputItemStack = ItemStack.EMPTY;
    private boolean isLit = false;

    public BaseBuiltFueledMachineBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    /*============================================================================
    ==============================================================================
    =====================   BASE CLASS OVERRIDES - START   =======================
    ==============================================================================
    ============================================================================*/

    @Override
    public void update()
    {
        BlockState state = level.getBlockState(getPos());
        this.level.sendBlockUpdated(getPos(), state, state, 3);
        this.setChanged();
    }

    /*============================================================================
    ==============================================================================
    =====================    BASE CLASS OVERRIDES - END    =======================
    ==============================================================================
    ============================================================================*/

    public RecipeType getRecipeTypeForBlock()
    {
        return RecipeType.SMELTING;
    }


    private IItemHandler createAllowedInputsHandler() {
        /*
         * CONFIG IS USED TO SET THIS, USERS MUST DEFINE RECIPES AND CHANGE CONFIG TO MAKE CHANGES TO MACHINES NOW
         */
        int slots = 2;
        return new ItemStackHandler(slots) {

            @Override
            protected void onLoad() {

                if(getSlots()<slots)
                {
                    for(int i = 0; i < getSlots(); ++i) {
                        stacksListAllowedInputsHandler.add(i,getStackInSlot(i));
                    }
                    setSize(getRepairListStacks().size());
                    for(int j = 0;j<stacksListAllowedInputsHandler.size();j++) {
                        setStackInSlot(j, stacksListAllowedInputsHandler.get(j));
                    }
                }

                super.onLoad();
            }

            @Override
            protected int getStackLimit(int slot, @NotNull ItemStack stack) {
                if(slot == 0)return 1;
                else if(slot == 1)return 4;
                else return 0;
            }

            @Override
            protected void onContentsChanged(int slot) {
                if(!(stacksListAllowedInputsHandler.size()>0))
                {
                    update();
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isAllowedInputItemForMachineSlot(slot,stack);
            }
        };
    }

    @Nullable
    public Recipe<Container> getRecipe(Level world, ItemStack stackIn) {
        if (world == null) return null;

        Container inv = MowLibContainerUtils.getContainer(1);
        inv.setItem(-1,stackIn);

        RecipeManager recipeManager = world.getRecipeManager();
        if(getRecipeTypeForBlock() == RecipeType.BLASTING)
        {
            List<BlastingRecipe> optional = recipeManager.getRecipesFor(RecipeType.BLASTING, inv, world);
            return getLevel() != null ? (optional.size() > 0)?(optional.stream().findFirst().get()):(null) : null;
        }
        else if(getRecipeTypeForBlock() == RecipeType.SMOKING)
        {
            List<SmokingRecipe> optional2 = recipeManager.getRecipesFor(RecipeType.SMOKING, inv, world);
            return getLevel() != null ? (optional2.size() > 0)?(optional2.stream().findFirst().get()):(null) : null;
        }
        else if(getRecipeTypeForBlock() == RecipeType.SMELTING)
        {
            List<SmeltingRecipe> optional1 = recipeManager.getRecipesFor(RecipeType.SMELTING, inv, world);
            return getLevel() != null ? (optional1.size() > 0)?(optional1.stream().findFirst().get()):(null) : null;
        }
        else return null;
    }

    public ItemStack getProcessResults(Recipe<Container> recipe) {
        return (recipe == null)?(ItemStack.EMPTY):(recipe.getResultItem(level.registryAccess()));
    }

    public float getProcessResultsXP(Recipe<Container> recipe) {
        if(recipe instanceof AbstractCookingRecipe recipeCook)
        {
            return (recipe == null)?(0.0f):(recipeCook.getExperience());
        }
        else return 0.0f;
    }

    public int getProcessCookTime(Recipe<Container> recipe) {
        if(recipe instanceof AbstractCookingRecipe recipeCook)
        {
            return (recipe == null)?(0):(recipeCook.getCookingTime());
        }
        else return 200;
    }

    public boolean isAllowedInputItemForMachineSlot(int slot, ItemStack stack)
    {
        if(slot == 0 && !stack.getItem().equals(Items.LAVA_BUCKET) && ForgeHooks.getBurnTime(stack, getRecipeTypeForBlock())>0 && getInputItemInSlot(0).getCount()<1)return true;
        else if(slot == 1 && getRecipe(getLevel(), stack) !=null)return true;
        else return false;
    }

    public int getAllowedInputSlotForMachine(ItemStack stack)
    {
        if(!stack.getItem().equals(Items.LAVA_BUCKET) && ForgeHooks.getBurnTime(stack, getRecipeTypeForBlock())>0 && getInputItemInSlot(0).getCount()<1)return 0;
        else if(getRecipe(getLevel(), stack) !=null)return 1;
        else return -1;
    }

    /*============================================================================
    ==============================================================================
    ===========================  INPUT ITEM START    =============================
    ==============================================================================
    ============================================================================*/

    /*
    Returns:
            The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
            May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
            The returned ItemStack can be safely modified after.
     */
    public ItemStack addInputItem(ItemStack inputItemFromBlock, boolean simulate)
    {
        IItemHandler aih = allowedInputsHandler.orElse(null);
        ItemStack insertedItem = inputItemFromBlock.copy();
        if(aih!=null)
        {
            int slot = getAllowedInputSlotForMachine(insertedItem);
            if(slot==-1)return inputItemFromBlock;
            if(aih.isItemValid(slot,insertedItem))
            {
                if(slot<aih.getSlots())
                {
                    if(aih.insertItem(slot,insertedItem,true).getCount() != inputItemFromBlock.getCount())
                    {
                        if(!simulate)
                        {
                            if(slot==1 && getInputItemInSlot(slot).isEmpty())
                            {
                                Recipe<Container> recipe = getRecipe(getLevel(),insertedItem);
                                this.outputItemStack = getProcessResults(recipe);
                                this.xpForOutput = getProcessResultsXP(recipe);
                                this.maxCookTime = getProcessCookTime(recipe);
                            }
                            return aih.insertItem(slot,insertedItem,false);
                        }
                        return aih.insertItem(slot,insertedItem,true);
                    }
                }
            }
        }

        return inputItemFromBlock;
    }

    public ItemStack getInputItemInSlot(int slot)
    {
        ItemStack returner = ItemStack.EMPTY;
        IItemHandler aih = allowedInputsHandler.orElse(null);
        if(aih!=null)
        {
            returner = aih.getStackInSlot(slot);
        }

        return returner;
    }

    public ItemStack removeItemFromSlot(int slot, int amount, boolean simulate)
    {
        ItemStack returner = ItemStack.EMPTY;
        IItemHandler aih = allowedInputsHandler.orElse(null);
        if(aih!=null)
        {
            //Returns:
            //ItemStack extracted from the slot, must be empty if nothing can be extracted.
            //The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
            returner = aih.extractItem(slot,amount,true);
            if(!returner.isEmpty())
            {
                if(!simulate)returner = aih.extractItem(slot,amount,simulate);
                if(slot==1 && getInputItemInSlot(slot).isEmpty())
                {
                    this.outputItemStack = ItemStack.EMPTY;
                    this.xpForOutput = 0F;
                    this.maxCookTime = 0;
                }
            }
        }

        return returner;
    }

    /*============================================================================
    ==============================================================================
    ===========================    INPUT ITEM END    =============================
    ==============================================================================
    ============================================================================*/

    public boolean isFurnaceLit()
    {
        return this.isLit;
    }

    public int getBurnTime(){ return this.burnTime; }

    public int calcFuelBurnTime()
    {
        ItemStack burnSlot = getInputItemInSlot(0);
        if(!burnSlot.isEmpty())
        {
            return ForgeHooks.getBurnTime(burnSlot,getRecipeTypeForBlock());
        }
        return 0;
    }

    public void addToBurnTime()
    {
        ItemStack burnSlot = getInputItemInSlot(0);
        if(!burnSlot.isEmpty())
        {
            if(!removeItemFromSlot(0,1,true).isEmpty())
            {
                changeBurnTime(calcFuelBurnTime(), true);
                setLit(true);
                removeItemFromSlot(0,1,false);
            }
        }
    }

    public void setLit(boolean isLitValue)
    {
        this.isLit = isLitValue;
    }

    public void setBurnTime(int setAmount)
    {
        this.burnTime = setAmount;
    }

    public void changeBurnTime(int amount, boolean increase)
    {
        if(increase)
        {
            this.burnTime += amount;
        }
        else this.burnTime -= amount;

    }

    public ItemStack getOutputItem()
    {
        return this.outputItemStack;
    }

    public float getXPOutput()
    {
        return this.xpForOutput;
    }

    public int getMaxCookTime()
    {
        return this.maxCookTime;
    }

    public int getCurrentCookTime()
    {
        return this.currentCookTime;
    }

    public void setCurrentCookTime(int setAmount)
    {
        this.currentCookTime = setAmount;
    }

    public void changeCurrentCookTime(int amount, boolean increase)
    {
        if(increase)
        {
            this.currentCookTime += amount;
        }
        else this.currentCookTime -= amount;
    }


    public void spawnItemStackOutput(Level worldIn, double x, double y, double z, ItemStack stack) {
        Random RANDOM = new Random();
        double d0 = (double) EntityType.ITEM.getWidth();
        double d1 = 1.0D - d0;
        double d2 = d0 / 2.0D;
        double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
        double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
        double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

        while(!stack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(RANDOM.nextInt(21) + 10));
            float f = 0.05F;
            itementity.lerpMotion(0.1D*RANDOM.ints(-5, 5 + 1).findFirst().getAsInt(),0.1D*RANDOM.nextInt( 5 ),0.1D*RANDOM.ints(-5, 5 + 1).findFirst().getAsInt());
            //itementity.lerpMotion(RANDOM.nextGaussian() * 0.05000000074505806D, RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, RANDOM.nextGaussian() * 0.05000000074505806D);
            worldIn.addFreshEntity(itementity);
        }
    }


    @Override
    public void tick()
    {
        super.tick();
        if(!getLevel().isClientSide())
        {
            if(getLevel().getGameTime()%20 == 0)
            {
                if(getBurnTime()>0)
                {
                    changeBurnTime(20, false);
                    if(getBurnTime()<=5)addToBurnTime();
                    if(getBurnTime()<=0)
                    {
                        setLit(false);
                        update();
                    }

                    if(!removeItemFromSlot(1,1,true).isEmpty())
                    {
                        changeCurrentCookTime(20, true);
                        if(getCurrentCookTime() >= getMaxCookTime())
                        {
                            setCurrentCookTime(0);
                            spawnItemStackOutput(getLevel(), getPos().getX(), getPos().getY()+1, getPos().getZ(), getOutputItem().copy());
                            removeItemFromSlot(1,1,false);
                            if(getXPOutput()>0f)
                            {
                                Random rand = new Random();
                                ExperienceOrb xpEntity = new ExperienceOrb(level,getPos().getX(), getPos().getY()+1, getPos().getZ(),(int)getXPOutput());
                                xpEntity.lerpMotion(0.1D*rand.nextInt(5),0.1D*rand.nextInt(5),0.1D*rand.nextInt(5));
                                getLevel().addFreshEntity(xpEntity);
                            }
                        }
                    }
                }
                else
                {
                    if(!getInputItemInSlot(0).isEmpty())addToBurnTime();
                    else if(getCurrentCookTime()>0)changeCurrentCookTime(20, false);
                }
            }

            if(getLevel().getGameTime()%69 == 0)
            {
                if(isFurnaceLit())getLevel().playSound(null,getPos(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,1.0F, 1.0F);
            }
        }
    }

    public void dropInputsItems(Level worldIn, BlockPos pos) {
        IItemHandler h = allowedInputsHandler.orElse(null);
        for(int i = 0; i < h.getSlots(); ++i) {
            spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i));
        }
    }

    @Override
    public void load(CompoundTag p_155245_) {
        super.load(p_155245_);

        CompoundTag allowedInputTag = p_155245_.getCompound("allowed_inputs");
        allowedInputsHandler.ifPresent(h -> ((INBTSerializable<CompoundTag>) h).deserializeNBT(allowedInputTag));

        if(p_155245_.contains("output_itemstack"))
        {
            CompoundTag invTag = p_155245_.getCompound("output_itemstack");
            ItemStackHandler handler = new ItemStackHandler();
            ((INBTSerializable<CompoundTag>) handler).deserializeNBT(invTag);
            this.outputItemStack = handler.getStackInSlot(0);
        }

        this.burnTime = p_155245_.getInt("burnTime");
        this.currentCookTime = p_155245_.getInt("currentCookTime");
        this.maxCookTime = p_155245_.getInt("maxCookTime");
        this.xpForOutput = p_155245_.getFloat("xpForOutput");
        this.isLit = p_155245_.getBoolean("isLit");
    }

    @Override
    public CompoundTag save(CompoundTag p_58888_) {
        super.save(p_58888_);

        allowedInputsHandler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            p_58888_.put("allowed_inputs", compound);
        });

        CompoundTag compoundStorage = new CompoundTag();
        ItemStackHandler handler = new ItemStackHandler();
        handler.setSize(1);
        handler.setStackInSlot(0,outputItemStack);
        compoundStorage = handler.serializeNBT();
        p_58888_.put("output_itemstack",compoundStorage);

        p_58888_.putInt("burnTime",burnTime);
        p_58888_.putInt("currentCookTime",currentCookTime);
        p_58888_.putInt("maxCookTime",maxCookTime);
        p_58888_.putFloat("xpForOutput",xpForOutput);
        p_58888_.putBoolean("isLit",isLit);

        return p_58888_;
    }
}
