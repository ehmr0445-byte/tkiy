package com.kingyellow.mod.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Efeitos visuais da dimensao Carcosa: ceu e horizonte amarelos (cores definidas no bioma)
 * e dois sois fixos no ceu, sem lua nem estrelas.
 */
public class CarcosaEfeitos extends DimensionSpecialEffects {
    private static final ResourceLocation SOL = new ResourceLocation("textures/environment/sun.png");

    public CarcosaEfeitos() {
        super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 cor, float brilho) {
        return cor;
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel nivel, int ticks, float parcial, PoseStack pilha, Camera camera,
                             Matrix4f projecao, boolean nevoa, Runnable preparaNevoa) {
        preparaNevoa.run();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SOL);

        // sol grande, amarelo claro
        RenderSystem.setShaderColor(1.0F, 0.93F, 0.55F, 1.0F);
        desenharSol(pilha, -90.0F, 40.0F, 0.0F, 38.0F);
        // sol menor, alaranjado, em outro ponto do ceu
        RenderSystem.setShaderColor(1.0F, 0.55F, 0.25F, 1.0F);
        desenharSol(pilha, -20.0F, -35.0F, 15.0F, 24.0F);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        return true;
    }

    private static void desenharSol(PoseStack pilha, float giro, float inclinacao, float rolagem, float t) {
        pilha.pushPose();
        pilha.mulPose(Axis.YP.rotationDegrees(giro));
        pilha.mulPose(Axis.XP.rotationDegrees(inclinacao));
        pilha.mulPose(Axis.ZP.rotationDegrees(rolagem));
        Matrix4f m = pilha.last().pose();
        BufferBuilder bb = Tesselator.getInstance().getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bb.vertex(m, -t, 100.0F, -t).uv(0.0F, 0.0F).endVertex();
        bb.vertex(m, t, 100.0F, -t).uv(1.0F, 0.0F).endVertex();
        bb.vertex(m, t, 100.0F, t).uv(1.0F, 1.0F).endVertex();
        bb.vertex(m, -t, 100.0F, t).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bb.end());
        pilha.popPose();
    }
}
