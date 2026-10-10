package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModStructures;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;

/**
 * Castelo Abandonado do Rei (Overworld).
 * Onde e quanto de distancia entre castelos: ver data/kingyellow/worldgen/structure_set.
 */
public class CasteloAbandonadoStructure extends Structure {
    public static final Codec<CasteloAbandonadoStructure> CODEC = simpleCodec(CasteloAbandonadoStructure::new);

    public CasteloAbandonadoStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext ctx) {
        ChunkPos cp = ctx.chunkPos();
        final int cx = cp.getMiddleBlockX();
        final int cz = cp.getMiddleBlockZ();
        ChunkGenerator gen = ctx.chunkGenerator();

        // Media de 5 amostras do terreno (centro + cantos da muralha)
        int[][] pontos = {{0, 0}, {-28, -28}, {28, -28}, {-28, 28}, {28, 28}};
        int soma = 0;
        for (int[] p : pontos) {
            soma += gen.getFirstOccupiedHeight(cx + p[0], cz + p[1],
                    Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());
        }
        final int oy = soma / pontos.length;
        if (oy < 60) {
            return Optional.empty(); // evita agua profunda
        }
        final int semente = (int) (ctx.seed() ^ ((long) cx * 31L + (long) cz));

        return Optional.of(new Structure.GenerationStub(new BlockPos(cx, oy, cz),
                builder -> builder.addPiece(new CasteloAbandonadoPiece(cx, oy, cz, semente))));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CASTELO.get();
    }
}
