package com.kingyellow.mod.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** Material da Armadura Amarela: 5 pontos de defesa em cada peca (total 20). */
public enum ArmaduraAmarelaMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 1000; // irrelevante: a armadura e inquebravel
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return 5;
    }

    @Override
    public int getEnchantmentValue() {
        return 25;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        // "modid:nome" -> texturas em assets/kingyellow/textures/models/armor/amarelo_layer_1/2.png
        return "kingyellow:amarelo";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
