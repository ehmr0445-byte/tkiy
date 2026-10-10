package com.kingyellow.mod;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro de itens do mod. */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KingYellowMod.MOD_ID);

    // Item de teste da etapa 1: confirma que o mod compila e carrega.
    public static final RegistryObject<Item> PAGINA_AMARELA =
            ITEMS.register("pagina_amarela", () -> new Item(new Item.Properties()));
}
