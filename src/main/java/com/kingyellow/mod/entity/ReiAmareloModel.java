package com.kingyellow.mod.entity;

import com.kingyellow.mod.KingYellowMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Modelo do Rei de Amarelo: manto amarelo de capuz com o rosto vazio, ombros em ponta,
 * pingente, coroa flutuante e tentaculos na barra do manto.
 * (Gerado junto com a textura rei_amarelo.png; os offsets de textura coincidem.)
 */
public class ReiAmareloModel extends EntityModel<ReiAmareloEntity> {
    public static final ModelLayerLocation CAMADA =
            new ModelLayerLocation(new ResourceLocation(KingYellowMod.MOD_ID, "rei_amarelo"), "main");
    private static final float COROA_Y = 24.0F - 66.0F;

    private final ModelPart raiz;
    private final ModelPart coroa;
    private final ModelPart[] tentB = new ModelPart[8];
    private final ModelPart[] tentC = new ModelPart[8];

    public ReiAmareloModel(ModelPart raiz) {
        this.raiz = raiz;
        this.coroa = raiz.getChild("coroa");
        for (int i = 0; i < 8; i++) {
            ModelPart a = raiz.getChild("t" + i + "a");
            this.tentB[i] = a.getChild("t" + i + "b");
            this.tentC[i] = this.tentB[i].getChild("t" + i + "c");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition malha = new MeshDefinition();
        PartDefinition raiz = malha.getRoot();
        raiz.addOrReplaceChild("saia1", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -12.0F, 24.0F, 14.0F, 24.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("saia2", CubeListBuilder.create().texOffs(0, 38).addBox(-10.0F, -26.0F, -10.0F, 20.0F, 12.0F, 20.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("tronco", CubeListBuilder.create().texOffs(80, 38).addBox(-7.0F, -40.0F, -5.0F, 14.0F, 14.0F, 10.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("colar", CubeListBuilder.create().texOffs(86, 70).addBox(-5.0F, -44.0F, -4.0F, 10.0F, 4.0F, 8.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("capuz", CubeListBuilder.create().texOffs(0, 70).addBox(-5.0F, -55.0F, -5.0F, 10.0F, 11.0F, 10.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("topo", CubeListBuilder.create().texOffs(32, 91).addBox(-4.0F, -58.0F, -4.0F, 8.0F, 3.0F, 8.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("ponta", CubeListBuilder.create().texOffs(64, 91).addBox(-2.0F, -61.0F, -2.0F, 4.0F, 3.0F, 4.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        raiz.addOrReplaceChild("ombroD", CubeListBuilder.create().texOffs(40, 70).addBox(-2.5F, -9.0F, -4.0F, 5.0F, 9.0F, 8.0F), PartPose.offset(9.5F, -14.0F, 0.0F));
        raiz.addOrReplaceChild("ombroE", CubeListBuilder.create().texOffs(40, 70).addBox(-2.5F, -9.0F, -4.0F, 5.0F, 9.0F, 8.0F), PartPose.offset(-9.5F, -14.0F, 0.0F));
        raiz.addOrReplaceChild("pingente", CubeListBuilder.create().texOffs(0, 91).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 1.0F), PartPose.offset(0.0F, -10.0F, 0.0F));
        raiz.addOrReplaceChild("coroa", CubeListBuilder.create().texOffs(0, 91).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 1.0F, 1.0F).addBox(-6.0F, 0.0F, 5.0F, 12.0F, 1.0F, 1.0F).addBox(-6.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).addBox(5.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).addBox(-6.0F, -4.0F, -6.0F, 1.0F, 4.0F, 1.0F).addBox(5.0F, -4.0F, -6.0F, 1.0F, 4.0F, 1.0F).addBox(-6.0F, -4.0F, 5.0F, 1.0F, 4.0F, 1.0F).addBox(5.0F, -4.0F, 5.0F, 1.0F, 4.0F, 1.0F).addBox(0.0F, -4.0F, -6.0F, 1.0F, 4.0F, 1.0F).addBox(0.0F, -4.0F, 5.0F, 1.0F, 4.0F, 1.0F).addBox(-6.0F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F).addBox(5.0F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, -42.0F, 0.0F));
        PartDefinition t0a = raiz.addOrReplaceChild("t0a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(12.93F, 24.0F, 5.36F));
        PartDefinition t0b = t0a.addOrReplaceChild("t0b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(2.77F, -9.0F, 1.15F));
        t0b.addOrReplaceChild("t0c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(2.77F, -8.0F, 1.15F));
        PartDefinition t1a = raiz.addOrReplaceChild("t1a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(5.36F, 24.0F, 12.93F));
        PartDefinition t1b = t1a.addOrReplaceChild("t1b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(1.15F, -9.0F, 2.77F));
        t1b.addOrReplaceChild("t1c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(1.15F, -8.0F, 2.77F));
        PartDefinition t2a = raiz.addOrReplaceChild("t2a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(-5.36F, 24.0F, 12.93F));
        PartDefinition t2b = t2a.addOrReplaceChild("t2b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(-1.15F, -9.0F, 2.77F));
        t2b.addOrReplaceChild("t2c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(-1.15F, -8.0F, 2.77F));
        PartDefinition t3a = raiz.addOrReplaceChild("t3a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(-12.93F, 24.0F, 5.36F));
        PartDefinition t3b = t3a.addOrReplaceChild("t3b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(-2.77F, -9.0F, 1.15F));
        t3b.addOrReplaceChild("t3c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(-2.77F, -8.0F, 1.15F));
        PartDefinition t4a = raiz.addOrReplaceChild("t4a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(-12.93F, 24.0F, -5.36F));
        PartDefinition t4b = t4a.addOrReplaceChild("t4b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(-2.77F, -9.0F, -1.15F));
        t4b.addOrReplaceChild("t4c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(-2.77F, -8.0F, -1.15F));
        PartDefinition t5a = raiz.addOrReplaceChild("t5a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(-5.36F, 24.0F, -12.93F));
        PartDefinition t5b = t5a.addOrReplaceChild("t5b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(-1.15F, -9.0F, -2.77F));
        t5b.addOrReplaceChild("t5c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(-1.15F, -8.0F, -2.77F));
        PartDefinition t6a = raiz.addOrReplaceChild("t6a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(5.36F, 24.0F, -12.93F));
        PartDefinition t6b = t6a.addOrReplaceChild("t6b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(1.15F, -9.0F, -2.77F));
        t6b.addOrReplaceChild("t6c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(1.15F, -8.0F, -2.77F));
        PartDefinition t7a = raiz.addOrReplaceChild("t7a", CubeListBuilder.create().texOffs(66, 70).addBox(-2.5F, -9.0F, -2.5F, 5.0F, 9.0F, 5.0F), PartPose.offset(12.93F, 24.0F, -5.36F));
        PartDefinition t7b = t7a.addOrReplaceChild("t7b", CubeListBuilder.create().texOffs(66, 70).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(2.77F, -9.0F, -1.15F));
        t7b.addOrReplaceChild("t7c", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offset(2.77F, -8.0F, -1.15F));
        return LayerDefinition.create(malha, 128, 128);
    }

    @Override
    public void setupAnim(ReiAmareloEntity rei, float limbSwing, float limbSwingAmount, float idade,
                          float giroCabeca, float inclinacaoCabeca) {
        float t = idade * 0.08F;
        // o manto flutua suavemente
        this.raiz.y = (float) Math.sin(t) * 1.2F;
        // a coroa gira devagar e oscila
        this.coroa.yRot = idade * 0.03F;
        this.coroa.y = COROA_Y + (float) Math.sin(idade * 0.1F) * 1.5F;
        // tentaculos ondulando
        for (int i = 0; i < 8; i++) {
            this.tentB[i].xRot = 0.25F * (float) Math.sin(t * 2.0F + i);
            this.tentB[i].zRot = 0.25F * (float) Math.cos(t * 1.7F + i * 1.3F);
            this.tentC[i].xRot = 0.35F * (float) Math.sin(t * 2.3F + i * 0.7F);
            this.tentC[i].zRot = 0.30F * (float) Math.cos(t * 1.9F + i * 0.9F);
        }
    }

    @Override
    public void renderToBuffer(PoseStack pilha, VertexConsumer vertices, int luz, int sobreposicao,
                               float r, float g, float b, float a) {
        this.raiz.render(pilha, vertices, luz, sobreposicao, r, g, b, a);
    }
}
