package com.kingyellow.mod;

import com.kingyellow.mod.item.ArmaduraAmarelaItem;
import com.kingyellow.mod.item.LivroDoReiItem;
import com.kingyellow.mod.livro.LivrosDoRei;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro de itens do mod. */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KingYellowMod.MOD_ID);

    public static final RegistryObject<Item> PAGINA_AMARELA =
            ITEMS.register("pagina_amarela", () -> new Item(new Item.Properties()));

    /** Livros do Rei (indice 0 = livro 1). */
    public static final List<RegistryObject<Item>> LIVROS = new ArrayList<>();

    /** Armadura Amarela: capacete, peitoral, calcas, botas (nesta ordem). */
    public static final List<RegistryObject<Item>> ARMADURA = new ArrayList<>();

    static {
        for (int i = 1; i <= LivrosDoRei.TOTAL; i++) {
            final int n = i;
            LIVROS.add(ITEMS.register("livro_do_rei_" + n,
                    () -> new LivroDoReiItem(n, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))));
        }

        String[] nomes = {"elmo", "peitoral", "calcas", "botas"};
        ArmorItem.Type[] tipos = {ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE,
                ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS};
        for (int k = 0; k < nomes.length; k++) {
            final ArmorItem.Type tipo = tipos[k];
            ARMADURA.add(ITEMS.register("armadura_amarela_" + nomes[k],
                    () -> new ArmaduraAmarelaItem(tipo, new Item.Properties().rarity(Rarity.EPIC))));
        }
    }
}
