package com.mowmaster.mowlib.Registry;

import com.mojang.serialization.Codec;
import com.mowmaster.mowlib.Blocks.BaseBlocks.BaseWorkStationBlock;
import com.mowmaster.mowlib.Tabs.MowLibTab;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;
import static com.mowmaster.mowlib.MowLibUtils.MowLibReferences.MODID;

public class DeferredRegisterBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,MODID);


    /*public static final RegistryObject<Block> TEST_WORKSTATION = registerBlock("block_workstation_base",
            () -> new BaseWorkStationBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
*/


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        DeferredRegisterItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(MowLibTab.TAB_ITEMS)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    //For Geodes
    private static <P extends PlacementModifier> PlacementModifierType<P> register(String name, Codec<P> codec) {
        return Registry.register(Registry.PLACEMENT_MODIFIERS, name, () -> {
            return codec;
        });
    }
}
