package com.kingyellow.mod.consciencia;

import com.kingyellow.mod.KingYellowMod;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * Dados do jogador ligados ao Rei de Amarelo: quais livros leu e o nivel de consciencia.
 * Ficam no NBT "PlayerPersisted" do Forge, que sobrevive a morte do jogador.
 */
public final class ConscienciaData {
    private ConscienciaData() {
    }

    private static CompoundTag raiz(Player p) {
        CompoundTag persistido = p.getPersistentData();
        if (!persistido.contains(Player.PERSISTED_NBT_TAG)) {
            persistido.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        CompoundTag pd = persistido.getCompound(Player.PERSISTED_NBT_TAG);
        if (!pd.contains(KingYellowMod.MOD_ID)) {
            pd.put(KingYellowMod.MOD_ID, new CompoundTag());
        }
        return pd.getCompound(KingYellowMod.MOD_ID);
    }

    /** Quantidade de livros diferentes ja lidos. */
    public static int livrosLidos(Player p) {
        return Integer.bitCount(raiz(p).getInt("livros"));
    }

    /** Nivel de consciencia atual (por enquanto = livros lidos; as mecanicas virao depois). */
    public static int consciencia(Player p) {
        return raiz(p).getInt("consciencia");
    }

    /** O jogador ja leu o suficiente para descobrir as verdades da existencia (5 livros). */
    public static boolean verdadesDescobertas(Player p) {
        return livrosLidos(p) >= 5;
    }

    /** O jogador pediu a Carcosa no chat (falta deitar em uma cama). */
    public static boolean pediuCarcosa(Player p) {
        return raiz(p).getBoolean("pediuCarcosa");
    }

    public static void definirPediuCarcosa(Player p, boolean valor) {
        raiz(p).putBoolean("pediuCarcosa", valor);
    }

    /** Chamado no servidor quando o jogador abre um Livro do Rei. */
    public static void lerLivro(ServerPlayer sp, int idLivro) {
        CompoundTag r = raiz(sp);
        int mascara = r.getInt("livros");
        int bit = 1 << (idLivro - 1);
        if ((mascara & bit) != 0) {
            return; // livro ja lido: nao conta de novo
        }
        mascara |= bit;
        r.putInt("livros", mascara);
        int lidos = Integer.bitCount(mascara);
        r.putInt("consciencia", lidos);

        sp.playNotifySound(SoundEvents.ENDERMAN_AMBIENT, SoundSource.AMBIENT, 0.6F, 0.5F);
        if (lidos == 5) {
            sp.sendSystemMessage(Component.literal(
                    "As páginas se calam. Em algum lugar, um nome é sussurrado, e é o seu. "
                            + "Peça a Carcosa, e deite-se.")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
        } else {
            sp.displayClientMessage(Component.literal("Algo dentro de você se move.")
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC), true);
        }
    }
}
