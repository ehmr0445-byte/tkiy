package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModStructures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cidade de Carcosa (centro no meio do chunk 0,0; cerca de 215 x 215 blocos).
 *
 * Layout (dx, dz relativos ao centro):
 *  - Muralha em ruinas (aneis |d| = 98..100) com 4 torres de canto e 2 torres no portao sul
 *  - Grade de ruas (largura 6) e quarteiroes de 24 blocos, com 4 tipos de lote:
 *    saloes, torres altas, conjuntos de casas e jardins com arvore morta
 *  - Praca central 49x49 com uma grande fonte seca e um obelisco amarelo
 *  - Postes de lampiao nas esquinas e no meio das ruas
 */
public class CidadeCarcosaPiece extends PecaProcedural {
    public static final int RAIO = 108;
    private static final int HMAX = 62;

    // centros das torres: {cx, cz, metade, altura, portaLx, portaLz}
    private static final int[][] TORRES = {
            {100, -100, 6, 34, -5, 6}, {-100, -100, 6, 34, 5, 6},
            {100, 100, 6, 34, -5, -6}, {-100, 100, 6, 34, 5, -6},
            {-11, 99, 5, 30, 0, -5}, {11, 99, 5, 30, 0, -5}
    };

    public CidadeCarcosaPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.CIDADE_PIECE.get(), ox, oy, oz, semente, RAIO, HMAX, 2);
    }

    public CidadeCarcosaPiece(CompoundTag tag) {
        super(ModStructures.CIDADE_PIECE.get(), tag);
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
        // estrada de pedra saindo do portao
        if (dz > 100 && adx <= 2 && y == -1) {
            return hash(dx, y, dz) % 100 < 30 ? bloco(Blocks.GRAVEL) : bloco(Blocks.COBBLESTONE);
        }
        if (m > 100) return null;
        if (m >= 98) return muralha(dx, y, dz, adx);

        // praca central
        if (adx <= 24 && adz <= 24) return praca(dx, y, dz, adx, adz);

        // grade de ruas e lotes
        int u = dx + 99;
        int w = dz + 99;
        int ou = Math.floorMod(u, 24);
        int ov = Math.floorMod(w, 24);
        boolean ruaU = ou < 6;
        boolean ruaV = ov < 6;
        if (ruaU || ruaV) return rua(dx, y, dz, ou, ov, ruaU, ruaV);
        return lote(dx, y, dz, Math.floorDiv(u, 24), Math.floorDiv(w, 24), ou - 6, ov - 6);
    }

    // ---------------- muralha ----------------

    private BlockState muralha(int dx, int y, int dz, int adx) {
        // portao ao sul
        if (dz >= 98 && adx <= 5) {
            if (y >= 0 && y <= 12 && !(adx >= 4 && y >= 11)) return null;
            if (y == -1) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        if (y == -1) return tijolo(dx, y, dz);
        if (y > 14) return null;
        int h = hash(dx, y, dz) % 100;
        if (y == 14) {
            if (((dx + dz) & 1) != 0 || h < 45) return null;
            return tijolo(dx, y, dz);
        }
        if (y >= 8 && h < 28) return null;
        if (y >= 3 && h < 8) return null;
        return tijolo(dx, y, dz);
    }

    // ---------------- ruas ----------------

    private BlockState rua(int dx, int y, int dz, int ou, int ov, boolean ruaU, boolean ruaV) {
        if (y == -1) {
            int h = hash(dx, y, dz) % 100;
            if (h < 45) return bloco(Blocks.STONE_BRICKS);
            if (h < 70) return bloco(Blocks.COBBLESTONE);
            if (h < 85) return bloco(Blocks.GRAVEL);
            return bloco(Blocks.CRACKED_STONE_BRICKS);
        }
        boolean poste = (ruaU && ruaV && (ou == 0 || ou == 5) && (ov == 0 || ov == 5))
                || (ruaU && !ruaV && ou == 0 && ov == 14)
                || (ruaV && !ruaU && ov == 0 && ou == 14);
        if (poste) {
            if (y <= 5) return bloco(Blocks.POLISHED_BLACKSTONE_WALL);
            if (y == 6) return hash(dx, y, dz) % 100 < 75 ? bloco(Blocks.SOUL_LANTERN) : null;
            return null;
        }
        if (y == 0 && hash(dx, y, dz) % 100 < 2) return bloco(Blocks.COBBLESTONE);
        return null;
    }

    // ---------------- praca central ----------------

    private BlockState praca(int dx, int y, int dz, int adx, int adz) {
        int d2 = dx * dx + dz * dz;
        if (y == -1) {
            if (d2 <= 100) return bloco(Blocks.POLISHED_BLACKSTONE);
            boolean xadrez = (((dx >> 2) + (dz >> 2)) & 1) == 0;
            return xadrez ? bloco(Blocks.POLISHED_BLACKSTONE_BRICKS) : bloco(Blocks.YELLOW_TERRACOTTA);
        }
        // fonte seca: anel de pedra
        if (d2 >= 81 && d2 <= 100 && y <= 2) {
            return hash(dx, y, dz) % 100 < 88 ? bloco(Blocks.STONE_BRICKS) : null;
        }
        // pedestal e obelisco amarelo
        if (d2 <= 25 && y <= 3) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        if (adx <= 1 && adz <= 1 && y >= 4 && y <= 14) return bloco(Blocks.YELLOW_CONCRETE);
        if (adx == 0 && adz == 0 && y >= 15 && y <= 34) return bloco(Blocks.YELLOW_CONCRETE);
        if (adx == 0 && adz == 0 && y == 35) return bloco(Blocks.GLOWSTONE);
        // postes nos cantos da praca
        if (adx == 22 && adz == 22 && y <= 6) {
            return y <= 5 ? bloco(Blocks.POLISHED_BLACKSTONE_WALL) : bloco(Blocks.SOUL_LANTERN);
        }
        return null;
    }

    // ---------------- lotes ----------------

    private BlockState lote(int dx, int y, int dz, int cu, int cv, int lx, int lz) {
        int h0 = hash(cu, 7, cv);
        int tipo = h0 % 10;
        int paleta = (h0 / 10) % 3;
        int a = hash(cu, 11, cv);

        if (tipo <= 3) {
            int alt = 12 + a % 9;
            return construcao(dx, y, dz, 1, 1, 16, 16, alt, lx, lz, paleta, false);
        }
        if (tipo <= 6) {
            int alt = 26 + a % 19;
            return construcao(dx, y, dz, 4, 4, 13, 13, alt, lx, lz, paleta, true);
        }
        if (tipo <= 8) {
            int[][] casas = {{1, 1, 7, 7}, {10, 1, 16, 7}, {1, 10, 7, 16}, {10, 10, 16, 16}};
            for (int i = 0; i < 4; i++) {
                int[] c = casas[i];
                if (lx >= c[0] && lx <= c[2] && lz >= c[1] && lz <= c[3]) {
                    int alt = 7 + hash(cu, 20 + i, cv) % 4;
                    return construcao(dx, y, dz, c[0], c[1], c[2], c[3], alt, lx, lz, paleta, false);
                }
            }
            return null;
        }
        return jardim(dx, y, dz, lx, lz);
    }

    private BlockState jardim(int dx, int y, int dz, int lx, int lz) {
        if (y < 0) return null;
        if (lx == 8 && lz == 8 && y <= 9) return bloco(Blocks.DARK_OAK_LOG);
        if ((lx == 7 && lz == 8 && y == 5) || (lx == 9 && lz == 8 && y == 6)
                || (lx == 8 && lz == 7 && y == 7) || (lx == 8 && lz == 9 && y == 5)) {
            return bloco(Blocks.DARK_OAK_FENCE);
        }
        if (y == 0 && hash(dx, y, dz) % 100 < 4) return bloco(Blocks.DEAD_BUSH);
        return null;
    }

    private BlockState parede(int paleta, int dx, int y, int dz) {
        int h = hash(dx, y, dz) % 100;
        if (paleta == 0) {
            return tijolo(dx, y, dz);
        }
        if (paleta == 1) {
            if (h < 62) return bloco(Blocks.DEEPSLATE_BRICKS);
            if (h < 92) return bloco(Blocks.CRACKED_DEEPSLATE_BRICKS);
            return bloco(Blocks.POLISHED_DEEPSLATE);
        }
        if (h < 40) return bloco(Blocks.SANDSTONE);
        if (h < 75) return bloco(Blocks.CUT_SANDSTONE);
        return bloco(Blocks.SMOOTH_SANDSTONE);
    }

    /** Predio retangular oco, com pisos, janelas, porta ao sul, ameias e ruinas. */
    private BlockState construcao(int dx, int y, int dz, int x0, int z0, int x1, int z1, int alt,
                                  int lx, int lz, int paleta, boolean torreAlta) {
        if (lx < x0 || lx > x1 || lz < z0 || lz > z1) return null;
        boolean borda = lx == x0 || lx == x1 || lz == z0 || lz == z1;

        if (y == -1) return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);

        // telhado em piramide para as torres altas
        if (torreAlta && y > alt) {
            int k = y - alt;
            int ladoMeio = (x1 - x0) / 2;
            if (k <= ladoMeio) {
                int cx = (x0 + x1) / 2;
                int cz = (z0 + z1) / 2;
                int dist = Math.max(Math.abs(lx - cx), Math.abs(lz - cz));
                if (dist <= ladoMeio + 1 - k) return bloco(Blocks.YELLOW_TERRACOTTA);
                return null;
            }
            if (lx == (x0 + x1) / 2 && lz == (z0 + z1) / 2 && k <= ladoMeio + 6) {
                return bloco(Blocks.YELLOW_CONCRETE);
            }
            return null;
        }
        if (y > alt + 1) return null;

        if (y == alt) {
            // laje do teto (com buracos) e ameias
            if (borda) {
                if (torreAlta) return parede(paleta, dx, y, dz);
                return (((lx + lz) & 1) != 0 || hash(dx, y, dz) % 100 < 40) ? null : parede(paleta, dx, y, dz);
            }
            return hash(dx, y, dz) % 100 < 18 ? null : bloco(Blocks.DARK_OAK_PLANKS);
        }
        if (y == alt + 1) {
            if (!borda || torreAlta) return null;
            return (((lx + lz) & 1) != 0 || hash(dx, y, dz) % 100 < 45) ? null : parede(paleta, dx, y, dz);
        }

        if (!borda) {
            // pisos intermediarios
            if (y > 0 && y % 6 == 0) {
                return hash(dx, y, dz) % 100 < 18 ? null : bloco(Blocks.DARK_OAK_PLANKS);
            }
            if (y >= 2 && hash(dx, y, dz) % 1000 < 4) return bloco(Blocks.COBWEB);
            return null;
        }

        // porta ao sul (lado lz maximo)
        if (lz == z1 && Math.abs(2 * lx - (x0 + x1)) <= 1 && y <= 2) return null;

        // janelas
        int ao = (lz == z0 || lz == z1) ? lx : lz;
        boolean canto = (lx == x0 || lx == x1) && (lz == z0 || lz == z1);
        int andar = y % 6;
        if (!canto && ao % 4 == 2 && y >= 2 && (andar == 2 || andar == 3) && y < alt - 1) {
            return hash(dx, y, dz) % 100 < 50 ? bloco(Blocks.YELLOW_STAINED_GLASS) : null;
        }

        int h = hash(dx, y, dz) % 100;
        if (y >= alt - 5 && h < 22) return null;
        if (y >= 4 && h < 5) return null;
        if (canto && h < 88) return bloco(Blocks.YELLOW_TERRACOTTA);
        return parede(paleta, dx, y, dz);
    }
}
