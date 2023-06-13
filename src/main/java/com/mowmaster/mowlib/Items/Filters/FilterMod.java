package com.mowmaster.mowlib.Items.Filters;

import com.mowmaster.mowlib.BlockEntities.MowLibBaseBlockEntity;
import com.mowmaster.mowlib.MowLibUtils.MowLibFluidUtils;
import com.mowmaster.mowlib.MowLibUtils.MowLibMessageUtils;
import com.mowmaster.mowlib.Registry.DeferredRegisterItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.IntStream;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class FilterMod extends BaseFilter{
    public FilterMod(Properties p_41383_) {
        super(p_41383_, FilterDirection.INSERT);
    }

    @Override
    public boolean canModeUseInventoryAsFilter(ItemTransferMode mode) {
        switch (mode)
        {
            case ITEMS:         return true;
            case FLUIDS:        return true;
            case ENERGY:        return false;
            case EXPERIENCE:    return false;
            case DUST:          return false;
            default:            return false;
        }
    }

    @Override
    public boolean canAcceptItems(ItemStack filter, ItemStack incomingStack) {
        boolean filterBool = super.canAcceptItems(filter, incomingStack);

        List<ItemStack> stackCurrent = readFilterQueueFromNBT(filter, ItemTransferMode.ITEMS);
        int range = stackCurrent.size();

        ItemStack itemFromInv = ItemStack.EMPTY;
        itemFromInv = IntStream.range(0,range)//Int Range
                .mapToObj((stackCurrent)::get)//Function being applied to each interval
                .filter(itemStack -> ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getNamespace()==ForgeRegistries.ITEMS.getKey(incomingStack.getItem()).getNamespace())
                .findFirst().orElse(ItemStack.EMPTY);

        if(!itemFromInv.isEmpty())
        {
            return filterBool;
        }
        else return !filterBool;

    }

    @Override
    public boolean canAcceptFluids(ItemStack filter, FluidStack incomingFluidStack) {
        boolean filterBool = super.canAcceptFluids(filter, incomingFluidStack);

        List<ItemStack> stackCurrent = readFilterQueueFromNBT(filter, ItemTransferMode.FLUIDS);
        int range = stackCurrent.size();

        ItemStack itemFromInv = ItemStack.EMPTY;
        itemFromInv = IntStream.range(0,range)//Int Range
                .mapToObj((stackCurrent)::get)//Function being applied to each interval
                .filter(itemStack -> ForgeRegistries.FLUIDS.getKey(MowLibFluidUtils.getFluidStackFromItemStack(itemStack).getFluid()).getNamespace()==ForgeRegistries.FLUIDS.getKey(incomingFluidStack.getFluid()).getNamespace())
                .findFirst().orElse(ItemStack.EMPTY);

        if(!itemFromInv.isEmpty())
        {
            return filterBool;
        }
        else return !filterBool;
    }


    @Override
    public void chatDetails(Player player, MowLibBaseBlockEntity pedestal, ItemStack filterStack) {
        if(!filterStack.getItem().equals(DeferredRegisterItems.FILTER_BASE.get()))
        {
            MowLibMessageUtils.messagePlayerChatText(player,ChatFormatting.GOLD,filterStack.getDisplayName().getString());

            //For each Mode
            for (ItemTransferMode mode: ItemTransferMode.values())
            {
                List<ItemStack> filterQueue = readFilterQueueFromNBT(filterStack,mode);
                if(filterQueue.size()>0)
                {
                    MowLibMessageUtils.messagePlayerChat(player,ChatFormatting.LIGHT_PURPLE,MODID + ".filters.tooltip_filterlist");

                    for(int j=0;j<filterQueue.size();j++) {

                        if(!filterQueue.get(j).isEmpty())
                        {
                            MutableComponent enchants = Component.literal(ForgeRegistries.ITEMS.getKey(filterQueue.get(j).getItem()).getNamespace());
                            enchants.withStyle(ChatFormatting.GRAY);
                            player.displayClientMessage(enchants, false);
                        }
                    }
                }
            }
            /*for(int i=0;i<4;i++)
            {
                List<ItemStack> filterQueue = readFilterQueueFromNBT(filterStack,i);
                if(filterQueue.size()>0)
                {
                    MowLibMessageUtils.messagePlayerChat(player,ChatFormatting.LIGHT_PURPLE,MODID + ".filters.tooltip_filterlist");

                    for(int j=0;j<filterQueue.size();j++) {

                        if(!filterQueue.get(j).isEmpty())
                        {
                            MutableComponent enchants = Component.literal(ForgeRegistries.ITEMS.getKey(filterQueue.get(j).getItem()).getNamespace());
                            enchants.withStyle(ChatFormatting.GRAY);
                            player.displayClientMessage(enchants, false);
                        }
                    }
                }
            }*/
        }
    }
}
