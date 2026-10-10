package com.kingyellow.mod;

import com.kingyellow.mod.consciencia.ConscienciaData;
import java.text.Normalizer;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BedBlock;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Entrada em Carcosa:
 *  1) ler 5 Livros do Rei diferentes (verdades da existencia);
 *  2) escrever "Leve-me ate Carcosa" no chat;
 *  3) clicar em qualquer cama.
 * Dentro de Carcosa, clicar em uma cama leva o jogador de volta.
 */
@Mod.EventBusSubscriber(modid = KingYellowMod.MOD_ID)
public class ModEventos {

    private static String normalizar(String texto) {
        String n = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[^a-z\\- ]", " ").replaceAll("\\s+", " ").trim();
    }

    @SubscribeEvent
    public static void aoFalar(ServerChatEvent evento) {
        String frase = normalizar(evento.getRawText());
        if (!frase.equals("leve-me ate carcosa") && !frase.equals("leve me ate carcosa")) {
            return;
        }
        ServerPlayer jogador = evento.getPlayer();
        if (jogador.level().dimension().equals(Carcosa.DIMENSAO)) {
            return;
        }
        if (!ConscienciaData.verdadesDescobertas(jogador)) {
            jogador.displayClientMessage(Component.literal("Você ainda não compreendeu o bastante.")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC), true);
            return;
        }
        ConscienciaData.definirPediuCarcosa(jogador, true);
        jogador.playNotifySound(SoundEvents.ENDERMAN_STARE, SoundSource.AMBIENT, 0.7F, 0.4F);
        jogador.sendSystemMessage(Component.literal("Algo ouviu. Deite-se em qualquer cama.")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }

    @SubscribeEvent
    public static void aoUsarCama(PlayerInteractEvent.RightClickBlock evento) {
        if (evento.getLevel().isClientSide() || evento.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) {
            return;
        }
        if (!(evento.getLevel().getBlockState(evento.getPos()).getBlock() instanceof BedBlock)) {
            return;
        }

        if (jogador.level().dimension().equals(Carcosa.DIMENSAO)) {
            // dentro de Carcosa, a cama leva de volta
            evento.setCanceled(true);
            evento.setCancellationResult(InteractionResult.SUCCESS);
            Carcosa.voltar(jogador);
            return;
        }

        if (ConscienciaData.pediuCarcosa(jogador) && ConscienciaData.verdadesDescobertas(jogador)) {
            evento.setCanceled(true);
            evento.setCancellationResult(InteractionResult.SUCCESS);
            ConscienciaData.definirPediuCarcosa(jogador, false);
            if (Carcosa.entrar(jogador)) {
                jogador.sendSystemMessage(Component.literal("Dois sóis. Nenhum deles o vê chegar.")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
            } else {
                jogador.sendSystemMessage(Component.literal("A dimensão Carcosa não foi encontrada neste mundo.")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }
}
