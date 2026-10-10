package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModItems;
import com.kingyellow.mod.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Castelo do Rei de Amarelo (centro no meio do chunk 40,0; cerca de 215 x 215 blocos).
 *
 * Layout (dx, dz relativos ao centro):
 *  - Muralha grossa (aneis |d| = 97..100, altura 20) com 9 torres e portao sul
 *  - Avenida do portao ate a fortaleza, com postes, estatuas e arvores mortas
 *  - Fortaleza 73x73 (centro em dz = -24, altura 46) com 4 torres de canto de 62 blocos,
 *    nave central aberta ate o teto, galeria-biblioteca no 2o piso, colunas, lustres
 *    e o trono amarelo ao norte
 */
public class CasteloReiPiece extends PecaProcedural {
    public static final int RAIO = 108;
    private static final int HMAX = 80;
    private static final int KZ = 24; // deslocamento do centro da fortaleza em z (kz = dz + 24)

    // {cx, cz, metade, altura, portaLx, portaLz}
    private static final int[][] TORRES = {
            {100, -100, 7, 44, -6, 7}, {-100, -100, 7, 44, 6, 7},
            {100, 100, 7, 44, -6, -7}, {-100, 100, 7, 44, 6, -7},
            {0, -100, 7, 38, 0, 7}, {100, 0, 7, 38, -7, 0}, {-100, 0, 7, 38, 7, 0},
            {-16, 100, 6, 40, 0, -6}, {16, 100, 6, 40, 0, -6},
            {36, -60, 8, 62, 0, -8}, {-36, -60, 8, 62, 0, -8},
            {36, 12, 8, 62, 0, 8}, {-36, 12, 8, 62, 0, 8}
    };

    // baus da biblioteca (dx, kz) no 2o piso (y = 16) e livros em cada um
    private static final int[][] BAUS = {{-30, -10}, {-30, 10}, {30, -10}, {30, 10}};
    private static final int[][] LIVROS_BAU = {{1, 2}, {3, 4}, {5, 6}, {7}};
    private static final Direction[] FACE_BAU = {Direction.EAST, Direction.EAST, Direction.WEST, Direction.WEST};

    public CasteloReiPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.CASTELO_REI_PIECE.get(), ox, oy, oz, semente, RAIO, HMAX, 2);
    }

    public CasteloReiPiece(CompoundTag tag) {
        super(ModStructures.CASTELO_REI_PIECE.get(), tag);
    }

    private BlockState escada(net.minecraft.world.level.block.Block b, Direction d) {
        return b.defaultBlockState().setValue(StairBlock.FACING, d).setValue(StairBlock.HALF, Half.BOTTOM);
    }

    private boolean ehBau(int dx, int kz) {
        for (int[] b : BAUS) {
            if (b[0] == dx && b[1] == kz) return true;
        }
        return false;
    }

    @Override
    protected BlockState amostra(int dx, int y, int dz) {
        if (y < -1) return null;
        int adx = Math.abs(dx);
        int adz = Math.abs(dz);

        for (int[] t : TORRES) {
            if (Math.max(Math.abs(dx - t[0]), Math.abs(dz - t[1])) <= t[2]) {
                return torre(dx, y, dz, t[0], t[1], t[2], t[3], t[4], t[5]);
            }
        }
        int m = Math.max(adx, adz);
        if (m > 100) return null;
        if (m >= 97) return muralha(dx, y, dz, adx);

        int kz = dz + KZ;
        int km = Math.max(adx, Math.abs(kz));
        if (km <= 36) return fortaleza(dx, y, dz, kz, adx, km);
        return patio(dx, y, dz, adx);
    }

    // ---------------- muralha ----------------

    private BlockState muralha(int dx, int y, int dz, int adx) {
        if (dz >= 97 && adx <= 8) {
            if (y >= 0 && y <= 15 && !(adx >= 7 && y >= 14)) return null;
            if (y == -1) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        if (y == -1) return tijolo(dx, y, dz);
        if (y > 21) return null;
        int h = hash(dx, y, dz) % 100;
        if (y == 21) {
            if (((dx + dz) & 1) != 0 || h < 40) return null;
            return tijolo(dx, y, dz);
        }
        if (y >= 14 && h < 25) return null;
        if (y >= 6 && h < 8) return null;
        return tijolo(dx, y, dz);
    }

    // ---------------- patio ----------------

    private BlockState patio(int dx, int y, int dz, int adx) {
        if (y == -1) {
            if (adx <= 4 && dz >= 13) {
                if (adx <= 1) {
                    return (dz & 1) == 0 ? bloco(Blocks.YELLOW_TERRACOTTA) : bloco(Blocks.YELLOW_CONCRETE);
                }
                return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
            }
            return null;
        }
        // postes ao longo da avenida
        if (adx == 6 && dz >= 20 && dz <= 92 && (dz - 20) % 12 == 0 && y <= 6) {
            if (y <= 5) return bloco(Blocks.POLISHED_BLACKSTONE_WALL);
            return hash(dx, y, dz) % 100 < 80 ? bloco(Blocks.SOUL_LANTERN) : null;
        }
        // estatuas encapuzadas
        for (int sz : new int[]{30, 60}) {
            int lx = adx - 14;
            int lz = Math.abs(dz - sz);
            if (lx >= -1 && lx <= 1 && lz <= 1 && y <= 1) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
            if (lx == 0 && lz == 0 && y >= 2 && y <= 10) return bloco(Blocks.YELLOW_CONCRETE);
            if (lz == 0 && lx >= -1 && lx <= 1 && y == 8) return bloco(Blocks.YELLOW_CONCRETE);
            if (lx == 0 && lz == 0 && y == 11) return bloco(Blocks.YELLOW_TERRACOTTA);
        }
        // arvores mortas
        for (int tz : new int[]{-30, 30, 70}) {
            int lx = adx - 60;
            int lz = dz - tz;
            if (lx == 0 && lz == 0 && y <= 14) return bloco(Blocks.DARK_OAK_LOG);
            if (y >= 9 && y <= 12 && ((Math.abs(lx) == 1 && lz == 0) || (lx == 0 && Math.abs(lz) == 1))) {
                return bloco(Blocks.DARK_OAK_FENCE);
            }
            if (y == 12 && ((Math.abs(lx) == 2 && lz == 0) || (lx == 0 && Math.abs(lz) == 2))) {
                return bloco(Blocks.DARK_OAK_FENCE);
            }
        }
        if (y == 0 && hash(dx, y, dz) % 100 < 2) return bloco(Blocks.DEAD_BUSH);
        return null;
    }

    // ---------------- fortaleza ----------------

    private BlockState fortaleza(int dx, int y, int dz, int kz, int adx, int km) {
        if (y > 47) return null;

        if (km >= 34) {
            // paredes (espessura 3)
            if (y == -1) return tijolo(dx, y, dz);
            if (kz >= 34 && adx <= 6 && y <= 14 && !(adx >= 5 && y >= 13)) return null; // portal
            // janela grande atras do trono
            if (kz <= -34 && adx <= 6 && y >= 6 && y <= 30) {
                return hash(dx, y, dz) % 100 < 70 ? bloco(Blocks.YELLOW_STAINED_GLASS) : null;
            }
            // janelas altas nas paredes
            if (!(adx >= 34 && Math.abs(kz) >= 34)) {
                int ao = Math.abs(kz) >= 34 ? dx : kz;
                int r = Math.floorMod(ao, 8);
                boolean fileira = (y >= 6 && y <= 13) || (y >= 20 && y <= 28) || (y >= 34 && y <= 42);
                if ((r == 3 || r == 4) && Math.abs(ao) <= 30 && fileira) {
                    return hash(dx, y, dz) % 100 < 60 ? bloco(Blocks.YELLOW_STAINED_GLASS) : null;
                }
            }
            int h = hash(dx, y, dz) % 100;
            if (y >= 36 && h < 25) return null;
            if (y == 47) {
                if (((dx + kz) & 1) != 0 || h < 40) return null;
                return tijolo(dx, y, dz);
            }
            return tijolo(dx, y, dz);
        }

        // ---- interior ----
        if (y == -1) {
            boolean xadrez = (((dx >> 1) + (kz >> 1)) & 1) == 0;
            return xadrez ? bloco(Blocks.POLISHED_BLACKSTONE_BRICKS) : bloco(Blocks.STONE_BRICKS);
        }
        if (y == 44) {
            // teto com uma grande abertura (oculo) no centro da nave
            if (dx * dx + kz * kz <= 100) return null;
            return hash(dx, y, dz) % 100 < 14 ? null : tijolo(dx, y, dz);
        }
        if (y > 44) return null;

        // escada para a galeria (sobe para oeste, em kz = 28)
        if (kz == 28 && dx <= -18 && dx >= -33) {
            int i = -18 - dx;
            if (y == i) return escada(Blocks.STONE_BRICK_STAIRS, Direction.WEST);
            if (y >= 0 && y < i) return bloco(Blocks.STONE_BRICKS);
        }

        // galeria do 2o piso (anel km >= 20, piso em y = 15)
        if (km >= 20) {
            if (y == 15) {
                if (kz == 28 && dx >= -32 && dx <= -18) return null; // buraco da escada
                if (ehBau(dx, kz)) return bloco(Blocks.DARK_OAK_PLANKS);
                return hash(dx, y, dz) % 100 < 10 ? null : bloco(Blocks.DARK_OAK_PLANKS);
            }
            if (y == 16 && km == 20 && hash(dx, y, dz) % 100 < 85) return bloco(Blocks.DARK_OAK_FENCE);
            if (y >= 16 && y <= 19 && km == 33) {
                return hash(dx, y, dz) % 100 < 65 ? bloco(Blocks.BOOKSHELF) : null;
            }
        }

        // trono
        if (kz <= -22 && kz >= -33 && adx <= 14 && y <= 2) {
            boolean degrau0 = y == 0;
            boolean degrau1 = y == 1 && adx <= 11 && kz <= -24;
            boolean degrau2 = y == 2 && adx <= 8 && kz <= -26;
            if (degrau0 || degrau1 || degrau2) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        if (kz <= -29 && kz >= -32 && adx <= 8) {
            if (dx == 0 && kz == -30 && y == 3) {
                return escada(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH);
            }
            if (adx == 1 && kz == -30 && y == 3) return bloco(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
            if (kz == -32 && adx <= 3 && y >= 3 && y <= 16) return bloco(Blocks.YELLOW_CONCRETE);
            if (kz == -32 && (adx == 3 || adx == 1) && y >= 17 && y <= 19) return bloco(Blocks.YELLOW_CONCRETE);
            if (kz == -32 && adx == 4 && y >= 3 && y <= 12) return bloco(Blocks.YELLOW_TERRACOTTA);
        }

        // tapete
        if (y == 0 && adx <= 3 && kz >= -21 && kz <= 34) {
            return hash(dx, y, dz) % 100 < 8 ? null : bloco(Blocks.YELLOW_CARPET);
        }

        // colunas da nave (algumas quebradas)
        if (adx == 12 && (kz == -20 || kz == -8 || kz == 4 || kz == 16)) {
            int topo = 22 + hash(dx, 0, kz) % 22;
            if (y <= topo) {
                return (y % 8 == 7 || y == topo) ? bloco(Blocks.CHISELED_STONE_BRICKS) : bloco(Blocks.STONE_BRICKS);
            }
        }

        // lustres pendurados
        if (adx == 6 && (kz == -12 || kz == 0 || kz == 12)) {
            if (y >= 38 && y <= 43) return bloco(Blocks.CHAIN);
            if (y == 37) return bloco(Blocks.SOUL_LANTERN).setValue(LanternBlock.HANGING, true);
        }

        if (y >= 2 && y <= 43 && hash(dx, y, dz) % 1000 < 3) return bloco(Blocks.COBWEB);
        return null;
    }

    // ---------------- baus ----------------

    @Override
    protected void depois(WorldGenLevel level, BoundingBox box) {
        for (int i = 0; i < BAUS.length; i++) {
            BlockPos p = new BlockPos(ox + BAUS[i][0], oy + 16, oz + BAUS[i][1] - KZ);
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
}
