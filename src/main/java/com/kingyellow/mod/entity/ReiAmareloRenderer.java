package com.kingyellow.mod.entity;

import com.kingyellow.mod.KingYellowMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Desenha o Rei de Amarelo (somente cliente). */
public class ReiAmareloRenderer extends MobRenderer<ReiAmareloEntity, ReiAmareloModel> {
    private static final ResourceLocation TEXTURA =
            new ResourceLocation(KingYellowMod.MOD_ID, "textures/entity/rei_amarelo.png");

    public ReiAmareloRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ReiAmareloModel(ctx.bakeLayer(ReiAmareloModel.CAMADA)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ReiAmareloEntity rei) {
        return TEXTURA;
    }
}
