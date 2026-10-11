package com.kingyellow.mod;

import com.kingyellow.mod.consciencia.ConscienciaData;
import com.kingyellow.mod.consciencia.Discipulo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Mantem os poderes de discipulo (Forca II e armadura) ao longo do tempo e apos a morte. */
@Mod.EventBusSubscriber(modid = KingYellowMod.MOD_ID)
public class JogadorEventos {

    @SubscribeEvent
    public static void aoTick(TickEvent.PlayerTickEvent evento) {
        if (evento.phase != TickEvent.Phase.END || evento.player.level().isClientSide) {
            return;
        }
        if (evento.player.tickCount % 40 != 0) {
            return;
        }
        if (evento.player instanceof ServerPlayer jogador && ConscienciaData.discipulo(jogador)) {
            Discipulo.garantirForca(jogador);
        }
    }

    @SubscribeEvent
    public static void aoRenascer(PlayerEvent.PlayerRespawnEvent evento) {
        if (evento.getEntity() instanceof ServerPlayer jogador && ConscienciaData.discipulo(jogador)) {
            Discipulo.garantirForca(jogador);
            Discipulo.entregarArmadura(jogador, false);
        }
    }
}
