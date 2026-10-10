package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModItems;
import com.kingyellow.mod.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.*;

/**
 * Castelo Abandonado do Rei, construido por codigo (sem arquivos .nbt).
 *
 * Todas as coordenadas internas sao relativas ao centro (dx, y, dz), com y = 0 sendo
 * o nivel do chao; y = -1 e a camada do piso/terreno.
 *
 * Layout:
 *  - Muralha quadrada em ruinas (raio 28) com portao ao sul
 *  - 4 torres de canto (altura 22) com escada de mao
 *  - Fortaleza central (31x31, altura 18) com salao do trono, galeria-biblioteca no
 *    segundo andar e 4 baus com os Livros do Rei
 *  - Patio com fonte seca, postes de lampiao e arvore morta
 */
public class CasteloAbandonadoPiece extends StructurePiece {

    private static final int ALTURA_MAX = 30;

    // Posicoes (dx, dz) dos baus na galeria (y = 9) e os livros guardados em cada um
    private static final int[][] BAUS = {{-12, -12}, {-5, -12}, {-12, 4}, {-5, 4}};
    private static final int[][] LIVROS_BAU = {{1, 2}, {3, 4}, {5, 6}, {7}};
    private static final Direction[] FACE_BAU = {Direction.EAST, Direction.SOUTH, Direction.EAST, Direction.SOUTH};

    private static final BlockState AR = Blocks.AIR.defaultBlockState();

    private final int ox;
    private final int oy;
    private final int oz;
    private final int semente;

    public CasteloAbandonadoPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.CASTELO_PIECE.get(), 0,
                new BoundingBox(ox - 33, oy - 40, oz - 33, ox + 33, oy + ALTURA_MAX, oz + 33));
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.semente = semente;
    }

    public CasteloAbandonadoPiece(CompoundTag tag) {
        super(ModStructures.CASTELO_PIECE.get(), tag);
        this.ox = tag.getInt("ox");
        this.oy = tag.getInt("oy");
        this.oz = tag.getInt("oz");
        this.semente = tag.getInt("semente");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("ox", ox);
        tag.putInt("oy", oy);
        tag.putInt("oz", oz);
        tag.putInt("semente", semente);
    }

    // ------------------------------------------------------------------
    //  Geracao
    // ------------------------------------------------------------------

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

                if (amostra(dx, -1, dz) != null) {
                    fundacao(level, pos, x, z);
                }
                for (int y = -1; y <= ALTURA_MAX; y++) {
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
        colocarBaus(level, box);
    }

    /** Enche abaixo do piso ate encontrar terreno solido, para o castelo nao flutuar. */
    private void fundacao(WorldGenLevel level, BlockPos.MutableBlockPos pos, int x, int z) {
        for (int y = -2; y >= -40; y--) {
            pos.set(x, oy + y, z);
            if (pos.getY() < level.getMinBuildHeight()) {
                return;
            }
            BlockState e = level.getBlockState(pos);
            boolean oco = e.isAir() || !e.getFluidState().isEmpty() || e.getCollisionShape(level, pos).isEmpty();
            if (!oco) {
                return;
            }
            level.setBlock(pos, y < -4 ? Blocks.COBBLESTONE.defaultBlockState() : tijolo(x - ox, y, z - oz), 2);
        }
    }

    /** Coloca os baus da biblioteca com os Livros do Rei dentro. */
    private void colocarBaus(WorldGenLevel level, BoundingBox box) {
        for (int i = 0; i < BAUS.length; i++) {
            BlockPos p = new BlockPos(ox + BAUS[i][0], oy + 9, oz + BAUS[i][1]);
            if (!box.isInside(p)) {
                continue;
            }
            level.setBlock(p, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, FACE_BAU[i]), 2);
            BlockEntity be = level.getBlockEntity(p);
            if (be instanceof ChestBlockEntity bau) {
                bau.setItem(0, new ItemStack(ModItems.PAGINA_AMARELA.get()));
                int slot = 10;
                for (int id : LIVROS_BAU[i]) {
                    bau.setItem(slot, new ItemStack(ModItems.LIVROS.get(id - 1).get()));
                    slot += 3;
                }
            }
        }
    }

    // ------------------------------------------------------------------
    //  Utilitarios
    // ------------------------------------------------------------------

    /** Numero pseudo-aleatorio deterministico por posicao (igual em todos os chunks). */
    private int hash(int x, int y, int z) {
        long h = semente * 341873128712L + x * 132897987541L + y * 1274126177L + z * 42317861L;
        h ^= (h >>> 29);
        h *= 0xBF58476D1CE4E5B9L;
        h ^= (h >>> 32);
        return (int) (h & 0x7fffffff);
    }

    private BlockState bloco(Block b) {
        return b.defaultBlockState();
    }

    private BlockState escada(Block b, Direction d) {
        return b.defaultBlockState().setValue(StairBlock.FACING, d).setValue(StairBlock.HALF, Half.BOTTOM);
    }

    private BlockState tijolo(int dx, int y, int dz) {
        int h = hash(dx, y, dz) % 100;
        if (h < 50) return bloco(Blocks.STONE_BRICKS);
        if (h < 75) return bloco(Blocks.MOSSY_STONE_BRICKS);
        if (h < 92) return bloco(Blocks.CRACKED_STONE_BRICKS);
        return bloco(Blocks.MOSSY_COBBLESTONE);
    }

    private BlockState tijoloChao(int dx, int dz) {
        int h = hash(dx, -1, dz) % 100;
        if (h < 40) return bloco(Blocks.STONE_BRICKS);
        if (h < 70) return bloco(Blocks.CRACKED_STONE_BRICKS);
        if (h < 90) return bloco(Blocks.MOSSY_STONE_BRICKS);
        return bloco(Blocks.COBBLESTONE);
    }

    private boolean ehBau(int dx, int dz) {
        for (int[] b : BAUS) {
            if (b[0] == dx && b[1] == dz) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------
    //  Mapa do castelo: devolve o bloco desejado em (dx, y, dz) ou null (nao mexer)
    // ------------------------------------------------------------------

    private BlockState amostra(int dx, int y, int dz) {
        int adx = Math.abs(dx);
        int adz = Math.abs(dz);

        // Torres de canto
        if (adx >= 24 && adz >= 24 && adx <= 32 && adz <= 32) {
            return torre(dx, y, dz);
        }
        int m = Math.max(adx, adz);
        if (m > 28) {
            return null;
        }
        // Muralha
        if (m >= 26) {
            return muralha(dx, y, dz, adx);
        }
        // Fortaleza central
        if (adx <= 15 && adz <= 15) {
            return menagem(dx, y, dz, adx, adz);
        }
        return patio(dx, y, dz, adx);
    }

    // ---------------- Torres ----------------

    private BlockState torre(int dx, int y, int dz) {
        if (y < -1) return null;
        int sx = dx > 0 ? 1 : -1;
        int sz = dz > 0 ? 1 : -1;
        int lx = dx - 28 * sx;
        int lz = dz - 28 * sz;
        int borda = Math.max(Math.abs(lx), Math.abs(lz));

        if (y == -1) return tijoloChao(dx, dz);
        if (y >= 22) return null;

        boolean interior = borda <= 3;
        if (interior) {
            // escada de mao encostada na parede
            if (lx == 0 && lz == 3 * sz && y <= 20) {
                return bloco(Blocks.LADDER).setValue(net.minecraft.world.level.block.LadderBlock.FACING,
                        sz > 0 ? Direction.NORTH : Direction.SOUTH);
            }
            if (y == 7 || y == 14) {
                if (y == 14 && hash(dx, y, dz) % 100 < 30) return AR;
                return bloco(Blocks.DARK_OAK_PLANKS);
            }
            return AR;
        }

        // parede da torre
        boolean atrasEscada = lx == 0 && lz == 4 * sz;
        if (atrasEscada && y <= 20) return tijolo(dx, y, dz);
        // porta voltada para o patio
        if (lx == -4 * sx && lz == -3 * sz && y <= 1) return AR;
        // seteiras
        boolean faceSeteira = (lx == 0 && lz == -4 * sz) || (lz == 0 && lx == -4 * sx);
        if (faceSeteira && ((y >= 3 && y <= 4) || (y >= 10 && y <= 11))) return AR;

        int h = hash(dx, y, dz) % 100;
        if (y >= 14 && h < (y - 13) * 9) return AR;
        if (y >= 4 && h < 8) return AR;
        if (y == 21 && ((lx + lz) & 1) != 0) return AR;
        return tijolo(dx, y, dz);
    }

    // ---------------- Muralha ----------------

    private BlockState muralha(int dx, int y, int dz, int adx) {
        if (y < -1) return null;
        // portao ao sul
        if (dz >= 26 && adx <= 3) {
            if (y >= 0 && y <= 6 && !(adx == 3 && y == 6)) return AR;
            if (y == -1) return hash(dx, y, dz) % 100 < 25 ? bloco(Blocks.GRAVEL) : bloco(Blocks.COBBLESTONE);
        }
        if (y == -1) return tijoloChao(dx, dz);
        if (y > 10) return null;

        int h = hash(dx, y, dz) % 100;
        if (y == 10) {
            if (((dx + dz) & 1) != 0 || h < 45) return AR;
            return tijolo(dx, y, dz);
        }
        if (y >= 7 && h < 30) return AR;
        if (y >= 3 && h < 10) return AR;
        return tijolo(dx, y, dz);
    }

    // ---------------- Fortaleza central ----------------

    private BlockState menagem(int dx, int y, int dz, int adx, int adz) {
        if (y < -1) return null;
        if (y > 18) return AR;

        int m = Math.max(adx, adz);
        if (m <= 13) {
            return menagemInterior(dx, y, dz, adx);
        }

        // paredes (espessura 2)
        if (y == -1) return tijoloChao(dx, dz);
        // entrada ao sul
        if (dz >= 14 && adx <= 2 && y <= 5 && !(adx == 2 && y == 5)) return AR;

        // janelas
        if (!(adx >= 14 && adz >= 14)) {
            int ao = adz >= 14 ? dx : dz;
            boolean linha = (ao % 5 == 0) && ao != 0;
            if (linha && ((y >= 3 && y <= 5) || (y >= 11 && y <= 13))) {
                return hash(dx, y, dz) % 100 < 45 ? bloco(Blocks.YELLOW_STAINED_GLASS) : AR;
            }
        }

        int h = hash(dx, y, dz) % 100;
        if (y >= 12 && h < 22) return AR;
        if (y == 18) {
            if (((dx + dz) & 1) != 0 || h < 40) return AR;
            return tijolo(dx, y, dz);
        }
        return tijolo(dx, y, dz);
    }

    private BlockState menagemInterior(int dx, int y, int dz, int adx) {
        if (y == -1) return tijoloChao(dx, dz);
        if (y == 16) return hash(dx, y, dz) % 100 < 30 ? AR : tijolo(dx, y, dz);
        if (y >= 17) return AR;

        // escada de pedra ate a galeria (sobe para oeste)
        if (dz == 12 && dx <= -3 && dx >= -11) {
            int i = -3 - dx;
            if (y == i) return escada(Blocks.STONE_BRICK_STAIRS, Direction.WEST);
            if (y >= 0 && y < i) return bloco(Blocks.STONE_BRICKS);
        }

        // pilares
        boolean pilarLeste = dx == 8 && (dz == -9 || dz == -3 || dz == 3 || dz == 9);
        boolean pilarOeste = dx == -4 && (dz == -12 || dz == -6 || dz == 0 || dz == 6);
        if (pilarLeste || pilarOeste) {
            int topo = pilarOeste ? 7 : 7 + hash(dx, 0, dz) % 9;
            if (y <= topo) {
                return y == topo ? bloco(Blocks.CHISELED_STONE_BRICKS) : bloco(Blocks.STONE_BRICKS);
            }
        }

        // trono
        int adz = Math.abs(dz);
        if (dz >= -13 && dz <= -9 && adx <= 4) {
            if (y == 0) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
            if (dz <= -11 && adx <= 2) {
                if (y == 1) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
                if (dx == 0 && dz == -12 && y == 2) {
                    return escada(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH);
                }
                if (adx == 1 && dz == -12 && y == 2) return bloco(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
                if (dz == -13 && y >= 2 && y <= 6) {
                    if (adx == 0) return bloco(Blocks.YELLOW_CONCRETE);
                    if (adx == 1 && y <= 4) return bloco(Blocks.YELLOW_TERRACOTTA);
                }
            }
        }

        // tapete amarelo
        if (y == 0 && adx <= 1 && dz >= -8 && dz <= 13) {
            return hash(dx, y, dz) % 100 < 20 ? AR : bloco(Blocks.YELLOW_CARPET);
        }

        // galeria-biblioteca (oeste, segundo andar)
        if (dx >= -13 && dx <= -4) {
            if (y == 8) {
                if (dz == 12 && dx >= -10) return AR; // buraco da escada
                if (ehBau(dx, dz)) return bloco(Blocks.DARK_OAK_PLANKS);
                if (hash(dx, y, dz) % 100 < 14) return AR;
                return bloco(Blocks.DARK_OAK_PLANKS);
            }
            if (y >= 9 && y <= 12) {
                if (ehBau(dx, dz) && y == 9) return AR;
                if (dx == -13 && dz >= -12 && dz <= 10) {
                    return hash(dx, y, dz) % 100 < 18 ? AR : bloco(Blocks.BOOKSHELF);
                }
                boolean fileira = dz == -10 || dz == -6 || dz == -2 || dz == 2 || dz == 6;
                if (y <= 10 && dx >= -12 && dx <= -10 && fileira) {
                    return hash(dx, y, dz) % 100 < 12 ? AR : bloco(Blocks.BOOKSHELF);
                }
                if (y == 9 && dx == -4 && dz != 12 && hash(dx, y, dz) % 100 >= 15) {
                    return bloco(Blocks.DARK_OAK_FENCE);
                }
            }
        }

        // teias de aranha
        if (y >= 2 && y <= 15 && hash(dx, y, dz) % 1000 < 5) return bloco(Blocks.COBWEB);
        return AR;
    }

    // ---------------- Patio ----------------

    private BlockState chaoPatio(int dx, int dz, int adx) {
        int h = hash(dx, -1, dz) % 100;
        if (adx <= 1 && dz >= 16) {
            return h < 25 ? bloco(Blocks.GRAVEL) : bloco(Blocks.COBBLESTONE);
        }
        if (h < 35) return bloco(Blocks.GRASS_BLOCK);
        if (h < 60) return bloco(Blocks.COARSE_DIRT);
        if (h < 80) return bloco(Blocks.PODZOL);
        if (h < 90) return bloco(Blocks.GRAVEL);
        return bloco(Blocks.MOSSY_COBBLESTONE);
    }

    private BlockState patio(int dx, int y, int dz, int adx) {
        if (y < -1) return null;
        if (y == -1) return chaoPatio(dx, dz, adx);

        // fonte seca (centro em dx=20, dz=0)
        int fx = dx - 20;
        int fm = Math.max(Math.abs(fx), Math.abs(dz));
        if (fm <= 3) {
            if (fm == 3 && y <= 1) {
                return hash(dx, y, dz) % 100 < 80 ? bloco(Blocks.STONE_BRICKS) : AR;
            }
            if (fm == 0 && y <= 4) return bloco(Blocks.STONE_BRICK_WALL);
        }

        // postes de lampiao ao longo do caminho
        if (adx == 3 && (dz == 19 || dz == 24) && y <= 4) {
            int h = hash(dx, y, dz) % 100;
            if (y <= 3) return (y >= 2 && h < 25) ? AR : bloco(Blocks.OAK_FENCE);
            return h < 70 ? bloco(Blocks.LANTERN) : AR;
        }

        // arvore morta
        if (dx == -20 && dz == -20 && y <= 9) return bloco(Blocks.DARK_OAK_LOG);
        if ((dx == -19 && dz == -20 && y == 5) || (dx == -21 && dz == -20 && y == 6)
                || (dx == -20 && dz == -19 && y == 7) || (dx == -20 && dz == -21 && y == 5)
                || (dx == -18 && dz == -20 && y == 6)) {
            return bloco(Blocks.DARK_OAK_FENCE);
        }

        // entulho
        if (y == 0 && hash(dx, y, dz) % 100 < 2) {
            return hash(dz, y, dx) % 2 == 0 ? bloco(Blocks.COBBLESTONE) : bloco(Blocks.MOSSY_COBBLESTONE);
        }
        return AR;
    }
}
