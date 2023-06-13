package com.mowmaster.mowlib.Items.Filters;

import com.mowmaster.mowlib.BlockEntities.MowLibBaseBlockEntity;
import com.mowmaster.mowlib.MowLibUtils.MowLibMessageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class FilterEnchantCount extends BaseFilter{
    public FilterEnchantCount(Properties p_41383_) {
        super(p_41383_, FilterDirection.INSERT);
    }

    @Override
    public boolean canModeUseInventoryAsFilter(ItemTransferMode mode) {
        switch (mode)
        {
            case ITEMS:         return true;
            case FLUIDS:        return false;
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

        int count = stackCurrent.stream()
                .filter(itemStack -> itemStack.isEnchanted() || itemStack.getItem().equals(Items.ENCHANTED_BOOK))
                .map(itemStack -> EnchantmentHelper.getEnchantments(itemStack).size())
                .collect(Collectors.toList())
                .stream()
                .reduce(0, (a,b) -> a + b);

        if(count > 0)
        {
            if(incomingStack.isEnchanted() || incomingStack.getItem().equals(Items.ENCHANTED_BOOK))
            {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(incomingStack);
                if(map.size() == count)
                {
                    return filterBool;
                }
            }
        }

        return !filterBool;
    }

    @Override
    public void chatDetails(Player player, MowLibBaseBlockEntity pedestal, ItemStack filterStack) {

        MowLibMessageUtils.messagePlayerChatText(player,ChatFormatting.GOLD,filterStack.getDisplayName().getString());

        MowLibMessageUtils.messagePlayerChat(player,ChatFormatting.LIGHT_PURPLE,MODID + ".filters.tooltip_filterlist_count");

        MutableComponent enchants = Component.literal("1");
        List<ItemStack> filterQueue = readFilterQueueFromNBT(filterStack, ItemTransferMode.ITEMS);
        if(filterQueue.size()>0)
        {
            int count = filterQueue.stream()
                    .filter(itemStack -> itemStack.isEnchanted() || itemStack.getItem().equals(Items.ENCHANTED_BOOK))
                    .map(itemStack -> EnchantmentHelper.getEnchantments(itemStack).size())
                    .collect(Collectors.toList())
                    .stream()
                    .reduce(0, (a,b) -> a + b);

            enchants = Component.literal(""+((count>0)?(count):(1))+"");
        }
        enchants.withStyle();
        player.displayClientMessage(enchants, false);
    }
}
