package com.kingyellow.mod.client;

import com.kingyellow.mod.KingYellowMod;
import com.kingyellow.mod.ModEntities;
import com.kingyellow.mod.entity.ReiAmareloModel;
import com.kingyellow.mod.entity.ReiAmareloRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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

    @SubscribeEvent
    public static void registrarCamadas(EntityRenderersEvent.RegisterLayerDefinitions evento) {
        evento.registerLayerDefinition(ReiAmareloModel.CAMADA, ReiAmareloModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registrarRenderizadores(EntityRenderersEvent.RegisterRenderers evento) {
        evento.registerEntityRenderer(ModEntities.REI_AMARELO.get(), ReiAmareloRenderer::new);
    }
}
