package com.kingyellow.mod.worldgen;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;

/**
 * Estrutura de Carcosa: nasce em UM chunk fixo (garante exatamente uma de cada na dimensao).
 * As estruturas so geram em chunks onde chunkX()/chunkZ() coincidem; nos demais, nada.
 */
public abstract class EstruturaCarcosaBase extends Structure {

    protected EstruturaCarcosaBase(Structure.StructureSettings settings) {
        super(settings);
    }

    protected abstract int chunkX();

    protected abstract int chunkZ();

    protected abstract StructurePiece criarPeca(int cx, int oy, int cz, int semente);

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext ctx) {
        ChunkPos cp = ctx.chunkPos();
        if (cp.x != chunkX() || cp.z != chunkZ()) {
            return Optional.empty();
        }
        final int cx = cp.getMiddleBlockX();
        final int cz = cp.getMiddleBlockZ();
        final int oy = ctx.chunkGenerator().getFirstFreeHeight(cx, cz,
                Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());
        final int semente = (int) (ctx.seed() ^ 0x5EEDL);
        return Optional.of(new Structure.GenerationStub(new BlockPos(cx, oy, cz),
                builder -> builder.addPiece(criarPeca(cx, oy, cz, semente))));
    }
}
