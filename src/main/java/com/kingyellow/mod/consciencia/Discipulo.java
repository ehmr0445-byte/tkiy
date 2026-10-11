package com.kingyellow.mod.consciencia;

import com.kingyellow.mod.ModItems;
import com.kingyellow.mod.ModSons;
import com.kingyellow.mod.item.ArmaduraAmarelaItem;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** O jogador que olhou o Rei de Amarelo: Forca II permanente e a Armadura Amarela. */
public final class Discipulo {
    private Discipulo() {
    }

    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    /** Primeira vez que o jogador olha o Rei. */
    public static void tornar(ServerPlayer jogador) {
        ConscienciaData.definirDiscipulo(jogador, true);
        garantirForca(jogador);
        entregarArmadura(jogador, true);
        jogador.playNotifySound(ModSons.SUSSURROS.get(), SoundSource.MASTER, 1.0F, 0.8F);
        jogador.sendSystemMessage(Component.literal(
                "Ele o viu, e você, enfim, o viu. O Manto Pálido agora lhe pertence.")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }

    /** Forca II sem fim; reaplicada se tiver sido removida (leite, morte...). */
    public static void garantirForca(ServerPlayer jogador) {
        MobEffectInstance atual = jogador.getEffect(MobEffects.DAMAGE_BOOST);
        if (atual == null || atual.getAmplifier() < 1 || !atual.isInfiniteDuration()) {
            jogador.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                    MobEffectInstance.INFINITE_DURATION, 1, false, false, true));
        }
    }

    /**
     * Equipa as 4 pecas. Se forcar for falso, so entrega as pecas que o jogador nao tem mais
     * (usado ao renascer).
     */
    public static void entregarArmadura(ServerPlayer jogador, boolean forcar) {
        for (int i = 0; i < SLOTS.length; i++) {
            Item peca = ModItems.ARMADURA.get(i).get();
            ItemStack atual = jogador.getItemBySlot(SLOTS[i]);
            if (atual.getItem() instanceof ArmaduraAmarelaItem) {
                continue;
            }
            if (!forcar && jogador.getInventory().hasAnyOf(Set.of(peca))) {
                continue;
            }
            if (!atual.isEmpty()) {
                ItemStack antiga = atual.copy();
                if (!jogador.getInventory().add(antiga)) {
                    jogador.drop(antiga, false);
                }
            }
            jogador.setItemSlot(SLOTS[i], peca.getDefaultInstance());
        }
    }
}
