package com.kingyellow.mod.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

/** Peca da Armadura Amarela: inquebravel e ja encantada com Protecao IV. */
public class ArmaduraAmarelaItem extends ArmorItem {

    public ArmaduraAmarelaItem(ArmorItem.Type tipo, Item.Properties props) {
        super(ArmaduraAmarelaMaterial.INSTANCE, tipo, props);
    }

    /** Nunca perde durabilidade. */
    @Override
    public boolean canBeDepleted() {
        return false;
    }

    /** Toda copia criada pelo jogo (aba criativa, /give) ja vem com Protecao IV. */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack s = new ItemStack(this);
        s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
        s.getOrCreateTag().putBoolean("Unbreakable", true);
        return s;
    }
}
