package com.kingyellow.mod.client;

import com.kingyellow.mod.Carcosa;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Efeitos visuais da dimensao Carcosa:
 *  - longe do lago: ceu amarelo (cor de neblina do bioma) com dois sois;
 *  - perto do Lago de Hali: ceu azul cobalto com estrelas (a cor vem do evento de neblina,
 *    em ClienteForgeEventos) e os sois somem.
 */
public class CarcosaEfeitos extends DimensionSpecialEffects {
    private static final ResourceLocation SOL = new ResourceLocation("textures/environment/sun.png");

    // Distancias ao centro do lago: cobalto total ate DIST_CHEIA, some em DIST_FIM
    private static final double DIST_CHEIA = 110.0;
    private static final double DIST_FIM = 190.0;

    public CarcosaEfeitos() {
        super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
    }

    /** 0 = longe do lago (ceu amarelo), 1 = junto ao lago (ceu cobalto). */
    public static float fatorLago(Vec3 pos) {
        double cx = Carcosa.LAGO_CHUNK_X * 16 + 8;
        double cz = Carcosa.LAGO_CHUNK_Z * 16 + 8;
        double dist = Math.sqrt((pos.x - cx) * (pos.x - cx) + (pos.z - cz) * (pos.z - cz));
        double t = (DIST_FIM - dist) / (DIST_FIM - DIST_CHEIA);
        return (float) Math.max(0.0, Math.min(1.0, t));
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
        float t = fatorLago(camera.getPosition());
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        if (t > 0.0F) {
            desenharEstrelas(nivel, pilha, t);
        }

        float alfaSois = 1.0F - t;
        if (alfaSois > 0.01F) {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SOL);
            // sol grande, amarelo claro
            RenderSystem.setShaderColor(1.0F, 0.93F, 0.55F, alfaSois);
            desenharSol(pilha, -90.0F, 40.0F, 0.0F, 38.0F);
            // sol menor, alaranjado, em outro ponto do ceu
            RenderSystem.setShaderColor(1.0F, 0.55F, 0.25F, alfaSois);
            desenharSol(pilha, -20.0F, -35.0F, 15.0F, 24.0F);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
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

    /** Campo de estrelas (mesmo algoritmo do ceu noturno do jogo), girando bem devagar. */
    private static void desenharEstrelas(ClientLevel nivel, PoseStack pilha, float alfa) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alfa);
        pilha.pushPose();
        pilha.mulPose(Axis.YP.rotationDegrees((nivel.getGameTime() % 72000L) * 0.004F));
        Matrix4f m = pilha.last().pose();
        BufferBuilder bb = Tesselator.getInstance().getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        RandomSource r = RandomSource.create(10842L);
        for (int i = 0; i < 1500; i++) {
            double d0 = r.nextFloat() * 2.0F - 1.0F;
            double d1 = r.nextFloat() * 2.0F - 1.0F;
            double d2 = r.nextFloat() * 2.0F - 1.0F;
            double tam = 0.15F + r.nextFloat() * 0.1F;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double px = d0 * 100.0D;
                double py = d1 * 100.0D;
                double pz = d2 * 100.0D;
                double a1 = Math.atan2(d0, d2);
                double s1 = Math.sin(a1);
                double c1 = Math.cos(a1);
                double a2 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double s2 = Math.sin(a2);
                double c2 = Math.cos(a2);
                double a3 = r.nextDouble() * Math.PI * 2.0D;
                double s3 = Math.sin(a3);
                double c3 = Math.cos(a3);
                for (int j = 0; j < 4; j++) {
                    double u = ((j & 2) - 1) * tam;
                    double v = (((j + 1) & 2) - 1) * tam;
                    double x1 = u * c3 - v * s3;
                    double y1 = v * c3 + u * s3;
                    double y2 = x1 * s2;
                    double z2 = -x1 * c2;
                    double x3 = z2 * s1 - y1 * c1;
                    double z3 = y1 * s1 + z2 * c1;
                    bb.vertex(m, (float) (px + x3), (float) (py + y2), (float) (pz + z3)).endVertex();
                }
            }
        }
        BufferUploader.drawWithShader(bb.end());
        pilha.popPose();
    }
}
