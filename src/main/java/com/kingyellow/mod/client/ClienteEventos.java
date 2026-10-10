package com.kingyellow.mod.client;

import com.kingyellow.mod.KingYellowMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos exclusivos do cliente (so carregados no jogo, nunca em servidor dedicado). */
@Mod.EventBusSubscriber(modid = KingYellowMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClienteEventos {

    @SubscribeEvent
    public static void registrarEfeitos(RegisterDimensionSpecialEffectsEvent evento) {
        evento.register(new ResourceLocation(KingYellowMod.MOD_ID, "carcosa"), new CarcosaEfeitos());
    }
}
