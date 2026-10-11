package com.kingyellow.mod.client;

import com.kingyellow.mod.Carcosa;
import com.kingyellow.mod.KingYellowMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Muda a cor do ceu/horizonte para azul cobalto quando o jogador esta perto do Lago de Hali. */
@Mod.EventBusSubscriber(modid = KingYellowMod.MOD_ID, value = Dist.CLIENT)
public class ClienteForgeEventos {
    // azul cobalto (0, 71, 171)
    private static final float R = 0.0F;
    private static final float G = 71.0F / 255.0F;
    private static final float B = 171.0F / 255.0F;

    @SubscribeEvent
    public static void corDaNevoa(ViewportEvent.ComputeFogColor evento) {
        if (!evento.getCamera().getEntity().level().dimension().equals(Carcosa.DIMENSAO)) {
            return;
        }
        float t = CarcosaEfeitos.fatorLago(evento.getCamera().getPosition());
        if (t <= 0.0F) {
            return;
        }
        evento.setRed(evento.getRed() + (R - evento.getRed()) * t);
        evento.setGreen(evento.getGreen() + (G - evento.getGreen()) * t);
        evento.setBlue(evento.getBlue() + (B - evento.getBlue()) * t);
    }
}
