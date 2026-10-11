package com.kingyellow.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** Constantes e viagens da dimensao Carcosa. */
public final class Carcosa {
    private Carcosa() {
    }

    public static final ResourceKey<Level> DIMENSAO =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(KingYellowMod.MOD_ID, "carcosa"));

    // Chunks onde nascem as estruturas (uma de cada, em posicao fixa)
    public static final int CIDADE_CHUNK_X = 0;
    public static final int CIDADE_CHUNK_Z = 0;
    public static final int CASTELO_CHUNK_X = 18;
    public static final int CASTELO_CHUNK_Z = 0;
    public static final int LAGO_CHUNK_X = -18;
    public static final int LAGO_CHUNK_Z = 0;

    // Centro da cidade = meio do chunk (0, 0)
    public static final int CIDADE_CENTRO_X = 8;
    public static final int CIDADE_CENTRO_Z = 8;

    // Ponto de chegada: logo fora do portao sul da cidade
    public static final double SPAWN_X = CIDADE_CENTRO_X + 0.5;
    public static final double SPAWN_Z = CIDADE_CENTRO_Z + 105.5;

    /** Leva o jogador do Overworld para a entrada da cidade de Carcosa. */
    public static boolean entrar(ServerPlayer jogador) {
        MinecraftServer servidor = jogador.getServer();
        if (servidor == null) {
            return false;
        }
        ServerLevel carcosa = servidor.getLevel(DIMENSAO);
        if (carcosa == null) {
            return false;
        }
        // forca a geracao do chunk de destino; sem isso getHeight devolve a altura minima (vazio)
        int bx = (int) Math.floor(SPAWN_X);
        int bz = (int) Math.floor(SPAWN_Z);
        carcosa.getChunk(bx >> 4, bz >> 4);
        int y = carcosa.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
        if (y < 8) {
            y = 64;
        }
        jogador.fallDistance = 0.0F;
        jogador.teleportTo(carcosa, SPAWN_X, y, SPAWN_Z, 180.0F, 0.0F);
        return true;
    }

    /** Traz o jogador de volta ao ponto de renascimento (ou ao spawn do mundo). */
    public static void voltar(ServerPlayer jogador) {
        MinecraftServer servidor = jogador.getServer();
        if (servidor == null) {
            return;
        }
        ServerLevel destino = servidor.getLevel(jogador.getRespawnDimension());
        BlockPos cama = jogador.getRespawnPosition();
        double x;
        double y;
        double z;
        if (destino != null && cama != null) {
            x = cama.getX() + 0.5;
            y = cama.getY() + 1.0;
            z = cama.getZ() + 0.5;
        } else {
            destino = servidor.overworld();
            BlockPos spawn = destino.getSharedSpawnPos();
            x = spawn.getX() + 0.5;
            z = spawn.getZ() + 0.5;
            destino.getChunk(spawn.getX() >> 4, spawn.getZ() >> 4);
            y = destino.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
        }
        jogador.fallDistance = 0.0F;
        jogador.teleportTo(destino, x, y, z, jogador.getYRot(), 0.0F);
    }
}
