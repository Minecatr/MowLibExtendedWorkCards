package com.mowmaster.mowlib.Items.Filters;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FilterEnchanted extends BaseFilter{
    public FilterEnchanted(Properties p_41383_) {
        super(p_41383_, FilterDirection.INSERT);
    }

    @Override
    public boolean canModeUseInventoryAsFilter(ItemTransferMode mode) {
        return false;
    }

    @Override
    public boolean canAcceptItems(ItemStack filter, ItemStack incomingStack) {
        boolean filterBool = super.canAcceptItems(filter, incomingStack);

        if(incomingStack.isEmpty())return !filterBool;
        if(incomingStack.isEnchanted() || incomingStack.getItem().equals(Items.ENCHANTED_BOOK))
        {
            return filterBool;
        }
        else return !filterBool;
    }

}
