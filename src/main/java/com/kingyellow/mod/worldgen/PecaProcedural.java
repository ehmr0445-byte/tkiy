package com.kingyellow.mod.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.*;

/**
 * Base das pecas de Carcosa construidas por codigo.
 * Cada subclasse implementa amostra(dx, y, dz): o bloco desejado na posicao relativa ao
 * centro (y = 0 e o primeiro bloco acima do chao; y = -1 e a camada do piso), ou null
 * para nao mexer (ar/terreno natural). O chao de Carcosa e plano, entao nao ha fundacao.
 */
public abstract class PecaProcedural extends StructurePiece {
    protected static final BlockState AR = Blocks.AIR.defaultBlockState();

    protected final int ox;
    protected final int oy;
    protected final int oz;
    protected final int semente;
    protected final int raio;
    protected final int hmax;
    protected final int prof;

    protected PecaProcedural(StructurePieceType tipo, int ox, int oy, int oz, int semente,
                             int raio, int hmax, int prof) {
        super(tipo, 0, new BoundingBox(ox - raio, oy - prof, oz - raio, ox + raio, oy + hmax, oz + raio));
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.semente = semente;
        this.raio = raio;
        this.hmax = hmax;
        this.prof = prof;
    }

    protected PecaProcedural(StructurePieceType tipo, CompoundTag tag) {
        super(tipo, tag);
        this.ox = tag.getInt("ox");
        this.oy = tag.getInt("oy");
        this.oz = tag.getInt("oz");
        this.semente = tag.getInt("semente");
        this.raio = tag.getInt("raio");
        this.hmax = tag.getInt("hmax");
        this.prof = tag.getInt("prof");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("ox", ox);
        tag.putInt("oy", oy);
        tag.putInt("oz", oz);
        tag.putInt("semente", semente);
        tag.putInt("raio", raio);
        tag.putInt("hmax", hmax);
        tag.putInt("prof", prof);
    }

    /** Bloco desejado em (dx, y, dz) relativo ao centro, ou null para nao mexer. */
    protected abstract BlockState amostra(int dx, int y, int dz);

    /** Gancho para colocar baus e outros blocos com dados, depois da pintura principal. */
    protected void depois(WorldGenLevel level, BoundingBox box) {
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager mgr, ChunkGenerator gen, RandomSource rnd,
                            BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        int minX = Math.max(box.minX(), this.boundingBox.minX());
        int maxX = Math.min(box.maxX(), this.boundingBox.maxX());
        int minZ = Math.max(box.minZ(), this.boundingBox.minZ());
        int maxZ = Math.min(box.maxZ(), this.boundingBox.maxZ());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int dx = x - ox;
                int dz = z - oz;
                for (int y = -prof; y <= hmax; y++) {
                    BlockState s = amostra(dx, y, dz);
                    if (s == null) {
                        continue;
                    }
                    pos.set(x, oy + y, z);
                    if (s.isAir()) {
                        if (!level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, s, 2);
                        }
                    } else {
                        level.setBlock(pos, s, 2);
                    }
                }
            }
        }
        depois(level, box);
    }

    // ------------------------------------------------------------------
    //  Utilitarios compartilhados
    // ------------------------------------------------------------------

    /** Numero pseudo-aleatorio deterministico por posicao (igual em todos os chunks). */
    protected int hash(int x, int y, int z) {
        long h = semente * 341873128712L + x * 132897987541L + y * 1274126177L + z * 42317861L;
        h ^= (h >>> 29);
        h *= 0xBF58476D1CE4E5B9L;
        h ^= (h >>> 32);
        return (int) (h & 0x7fffffff);
    }

    protected BlockState bloco(Block b) {
        return b.defaultBlockState();
    }

    /** Tijolo de pedra envelhecido (comum, musgo, rachado). */
    protected BlockState tijolo(int dx, int y, int dz) {
        int h = hash(dx, y, dz) % 100;
        if (h < 55) return bloco(Blocks.STONE_BRICKS);
        if (h < 75) return bloco(Blocks.MOSSY_STONE_BRICKS);
        return bloco(Blocks.CRACKED_STONE_BRICKS);
    }

    /**
     * Torre quadrada generica com piso a cada 10 blocos, escada de mao, porta, seteiras,
     * ameias e telhado piramidal amarelo (ou topo em ruinas, conforme a torre).
     *
     * @param half    metade do lado (a torre cobre cx +- half)
     * @param alt     altura das paredes
     * @param portaLx posicao local da porta (relativa ao centro da torre), na parede
     * @param portaLz idem
     */
    protected BlockState torre(int dx, int y, int dz, int cx, int cz, int half, int alt,
                               int portaLx, int portaLz) {
        int lx = dx - cx;
        int lz = dz - cz;
        int borda = Math.max(Math.abs(lx), Math.abs(lz));
        if (borda > half || y < -1) {
            return null;
        }
        if (y == -1) {
            return tijolo(dx, y, dz);
        }
        boolean ruina = hash(cx, 3, cz) % 3 == 0;

        // telhado piramidal e pinaculo
        if (y >= alt) {
            if (ruina) return null;
            int k = y - alt;
            if (k < half) {
                return borda <= half - k ? bloco(Blocks.YELLOW_TERRACOTTA) : null;
            }
            if (lx == 0 && lz == 0 && k < half + 6) return bloco(Blocks.YELLOW_CONCRETE);
            return null;
        }

        // posicao da escada: do lado oposto ao da porta
        int escLx;
        int escLz;
        Direction escFace;
        int atrasLx;
        int atrasLz;
        if (Math.abs(portaLz) == half) {
            int sgn = portaLz > 0 ? 1 : -1;
            escLx = 0;
            escLz = -sgn * (half - 1);
            escFace = sgn > 0 ? Direction.SOUTH : Direction.NORTH;
            atrasLx = 0;
            atrasLz = -sgn * half;
        } else {
            int sgn = portaLx > 0 ? 1 : -1;
            escLx = -sgn * (half - 1);
            escLz = 0;
            escFace = sgn > 0 ? Direction.EAST : Direction.WEST;
            atrasLx = -sgn * half;
            atrasLz = 0;
        }

        boolean interior = borda <= half - 1;
        if (interior) {
            if (lx == escLx && lz == escLz && y <= alt - 2) {
                return bloco(Blocks.LADDER).setValue(LadderBlock.FACING, escFace);
            }
            if (y > 0 && y % 10 == 0) {
                return bloco(Blocks.DARK_OAK_PLANKS);
            }
            return null;
        }

        // paredes
        if (lx == portaLx && lz == portaLz && y <= 2) return null;
        boolean atras = lx == atrasLx && lz == atrasLz;
        if (atras && y <= alt - 2) return tijolo(dx, y, dz);

        boolean faceCentro = (lx == 0 && Math.abs(lz) == half) || (lz == 0 && Math.abs(lx) == half);
        int t1 = alt / 3;
        int t2 = 2 * alt / 3;
        if (faceCentro && (y == t1 || y == t1 + 1 || y == t2 || y == t2 + 1)) return null;

        int h = hash(dx, y, dz) % 100;
        if (ruina && y >= alt - 10 && h < (y - (alt - 11)) * 7) return null;
        if (y >= 6 && h < 4) return null;
        if (y == alt - 1 && ((lx + lz) & 1) != 0) return null;
        return tijolo(dx, y, dz);
    }
}
