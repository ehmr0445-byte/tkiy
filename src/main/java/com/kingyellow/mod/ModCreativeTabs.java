package com.kingyellow.mod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Aba criativa do mod. */
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KingYellowMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> PRINCIPAL = TABS.register("principal",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.kingyellow"))
                    .icon(() -> new ItemStack(ModItems.PAGINA_AMARELA.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.PAGINA_AMARELA.get());
                        output.accept(ModItems.OVO_REI.get());
                        for (RegistryObject<Item> livro : ModItems.LIVROS) {
                            output.accept(livro.get());
                        }
                        for (RegistryObject<Item> peca : ModItems.ARMADURA) {
                            output.accept(peca.get().getDefaultInstance());
                        }
                    })
                    .build());
}
