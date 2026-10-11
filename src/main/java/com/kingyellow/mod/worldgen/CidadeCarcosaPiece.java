package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModStructures;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Cidade de Carcosa (centro no meio do chunk 0,0; cerca de 215 x 215 blocos).
 *
 * Layout (dx, dz relativos ao centro):
 *  - Muralha em ruinas (aneis |d| = 98..100) com 4 torres de canto e 2 torres no portao sul
 *  - Grade de ruas (largura 6) e quarteiroes de 24 blocos com 4 tipos de lote:
 *    saloes, torres altas, conjuntos de casas e jardins com arvore morta
 *  - Predios mobiliados: andares ligados por escadas, bibliotecas, dormitorios, armazens
 *    e oficinas, com baus de loot
 *  - Praca central com uma grande fonte de 3 niveis (16 blocos de altura) com agua
 *  - Postes de lampiao nas esquinas e no meio das ruas
 */
public class CidadeCarcosaPiece extends PecaProcedural {
    public static final int RAIO = 108;
    private static final int HMAX = 62;
    private static final ResourceLocation LOOT = new ResourceLocation("kingyellow", "chests/cidade_carcosa");

    // centros das torres: {cx, cz, metade, altura, portaLx, portaLz}
    private static final int[][] TORRES = {
            {100, -100, 6, 34, -5, 6}, {-100, -100, 6, 34, 5, 6},
            {100, 100, 6, 34, -5, -6}, {-100, 100, 6, 34, 5, -6},
            {-11, 99, 5, 30, 0, -5}, {11, 99, 5, 30, 0, -5}
    };

    /** Cache dos predios de cada lote: {x0, z0, x1, z1, alt, paleta, tipoPredio, indice}. */
    private final Map<Integer, int[][]> cachePredios = new ConcurrentHashMap<>();

    public CidadeCarcosaPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.CIDADE_PIECE.get(), ox, oy, oz, semente, RAIO, HMAX, 2);
    }

    public CidadeCarcosaPiece(CompoundTag tag) {
        super(ModStructures.CIDADE_PIECE.get(), tag);
    }

    // ------------------------------------------------------------------
    //  Mapa geral
    // ------------------------------------------------------------------

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

    // ---------------- praca e fonte ----------------

    /** Octogono: lado r, com os cantos cortados. */
    private boolean oct(int adx, int adz, int r) {
        return Math.max(adx, adz) <= r && adx + adz <= r * 3 / 2;
    }

    private BlockState agua() {
        return bloco(Blocks.WATER);
    }

    private BlockState aguaCaindo() {
        return bloco(Blocks.WATER).setValue(LiquidBlock.LEVEL, 8);
    }

    private BlockState praca(int dx, int y, int dz, int adx, int adz) {
        if (adx <= 11 && adz <= 11 && oct(adx, adz, 11)) {
            BlockState f = fonte(dx, y, dz, adx, adz);
            if (f != null || y >= 0) return f;
            // y == -1: piso de fonte
            return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        if (y == -1) {
            boolean xadrez = (((dx >> 2) + (dz >> 2)) & 1) == 0;
            return xadrez ? bloco(Blocks.POLISHED_BLACKSTONE_BRICKS) : bloco(Blocks.YELLOW_TERRACOTTA);
        }
        // postes nos cantos da praca
        if (adx == 22 && adz == 22 && y <= 6) {
            return y <= 5 ? bloco(Blocks.POLISHED_BLACKSTONE_WALL) : bloco(Blocks.SOUL_LANTERN);
        }
        return null;
    }

    /**
     * Fonte de 3 niveis, 16 blocos de altura (y = 0..15):
     *  nivel 1: bacia octogonal de raio 11 (borda y 0..2, agua y 0..1)
     *  nivel 2: bacia de raio 7 sobre um pilar central (piso y 5, borda y 6..8, agua y 6..7)
     *  nivel 3: bacia de raio 4 (piso y 11, borda y 12..13, agua y 12)
     *  espigao central ate y 15 com lanterna marinha; cascatas nos 4 lados entre os niveis.
     */
    private BlockState fonte(int dx, int y, int dz, int adx, int adz) {
        if (y < 0) return null;
        boolean cardinal = (adx == 0 || adz == 0);
        int dist = Math.max(adx, adz);

        // pilar central do nivel 1 (raio 2) ate o piso do nivel 2
        if (adx <= 2 && adz <= 2 && y <= 4) {
            return y == 4 ? bloco(Blocks.CHISELED_STONE_BRICKS) : tijolo(dx, y, dz);
        }
        // coluna central do nivel 2 (3x3) ate o piso do nivel 3
        if (adx <= 1 && adz <= 1 && y >= 5 && y <= 10) {
            return y == 8 ? bloco(Blocks.CHISELED_STONE_BRICKS) : tijolo(dx, y, dz);
        }
        // espigao
        if (adx == 0 && adz == 0 && y >= 11 && y <= 15) {
            if (y == 15) return bloco(Blocks.SEA_LANTERN);
            return bloco(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }

        // cascatas (agua caindo) entre os niveis
        if (cardinal) {
            if (dist == 5 && y >= 8 && y <= 11) return aguaCaindo();   // nivel 3 -> nivel 2
            if (dist == 8 && y >= 2 && y <= 5) return aguaCaindo();    // nivel 2 -> nivel 1
        }

        // nivel 3
        if (y >= 11 && y <= 13 && oct(adx, adz, 4)) {
            if (y == 11) return bloco(Blocks.DEEPSLATE_BRICKS);
            if (!oct(adx, adz, 3)) {
                if (cardinal && dist == 4 && y == 12) return agua();   // bica
                return y == 13 ? bloco(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB) : tijolo(dx, y, dz);
            }
            return y == 12 ? agua() : null;
        }
        // nivel 2
        if (y >= 5 && y <= 8 && oct(adx, adz, 7)) {
            if (y == 5) return bloco(Blocks.DEEPSLATE_BRICKS);
            if (!oct(adx, adz, 6)) {
                if (cardinal && dist == 7 && y <= 7) return agua();    // bica
                return y == 8 ? bloco(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB) : tijolo(dx, y, dz);
            }
            return y <= 7 ? agua() : null;
        }
        // nivel 1
        if (y <= 2) {
            if (!oct(adx, adz, 10)) {
                return y == 2 ? bloco(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB) : tijolo(dx, y, dz);
            }
            if (y <= 1) return agua();
            if (y == 2 && hash(dx, y, dz) % 100 < 5) return bloco(Blocks.LILY_PAD);
        }
        return null;
    }

    // ---------------- lotes ----------------

    private int[][] predios(int cu, int cv) {
        return cachePredios.computeIfAbsent(cu * 64 + cv, k -> calcularPredios(cu, cv));
    }

    private int[][] calcularPredios(int cu, int cv) {
        int h0 = hash(cu, 7, cv);
        int tipo = h0 % 10;
        int paleta = (h0 / 10) % 3;
        int a = hash(cu, 11, cv);
        if (tipo <= 3) {
            return new int[][]{{1, 1, 16, 16, 12 + a % 11, paleta, 0, 0}};
        }
        if (tipo <= 6) {
            return new int[][]{{4, 4, 13, 13, 26 + a % 19, paleta, 1, 0}};
        }
        if (tipo <= 8) {
            int[][] casas = {{1, 1, 8, 8}, {10, 1, 17, 8}, {1, 10, 8, 17}, {10, 10, 17, 17}};
            int[][] r = new int[4][];
            for (int i = 0; i < 4; i++) {
                int[] c = casas[i];
                r[i] = new int[]{c[0], c[1], c[2], c[3], 11 + hash(cu, 20 + i, cv) % 4, paleta, 2, i};
            }
            return r;
        }
        return new int[0][];
    }

    private BlockState lote(int dx, int y, int dz, int cu, int cv, int lx, int lz) {
        int[][] lista = predios(cu, cv);
        if (lista.length == 0) return jardim(dx, y, dz, lx, lz);
        for (int[] p : lista) {
            if (lx >= p[0] && lx <= p[2] && lz >= p[1] && lz <= p[3]) {
                return construcao(dx, y, dz, p, lx, lz, cu, cv);
            }
        }
        return null;
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

    // ------------------------------------------------------------------
    //  Predios: pisos a cada 6 blocos (laje em y = 6k + 5), escadas e mobilia
    // ------------------------------------------------------------------

    private BlockState escada(Block b, Direction d) {
        return b.defaultBlockState().setValue(StairBlock.FACING, d).setValue(StairBlock.HALF, Half.BOTTOM);
    }

    /** Predio retangular oco: p = {x0, z0, x1, z1, alt, paleta, tipoPredio, indice}. */
    private BlockState construcao(int dx, int y, int dz, int[] p, int lx, int lz, int cu, int cv) {
        int x0 = p[0];
        int z0 = p[1];
        int x1 = p[2];
        int z1 = p[3];
        int alt = p[4];
        int paleta = p[5];
        int tipoPredio = p[6];
        boolean torreAlta = tipoPredio == 1;
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
            return interior(dx, y, dz, p, lx, lz, cu, cv);
        }

        // porta ao sul (lado lz maximo)
        if (lz == z1 && Math.abs(2 * lx - (x0 + x1)) <= 1 && y <= 2) return null;

        // janelas (a altura dos olhos de cada andar)
        int ao = (lz == z0 || lz == z1) ? lx : lz;
        boolean canto = (lx == x0 || lx == x1) && (lz == z0 || lz == z1);
        int rel = y % 6;
        if (!canto && ao % 4 == 2 && y >= 1 && (rel == 1 || rel == 2) && y < alt - 1) {
            return hash(dx, y, dz) % 100 < 50 ? bloco(Blocks.YELLOW_STAINED_GLASS) : null;
        }

        int h = hash(dx, y, dz) % 100;
        if (y >= alt - 5 && h < 22) return null;
        if (y >= 4 && h < 5) return null;
        if (canto && h < 88) return bloco(Blocks.YELLOW_TERRACOTTA);
        return parede(paleta, dx, y, dz);
    }

    /** Celula interior: escadas, lajes dos pisos, mobilia, teias. */
    private BlockState interior(int dx, int y, int dz, int[] p, int lx, int lz, int cu, int cv) {
        int x0 = p[0];
        int z0 = p[1];
        int alt = p[4];
        int tipoPredio = p[6];
        int idx = p[7];
        int W = p[2] - x0 - 1;
        int D = p[3] - z0 - 1;
        int ix = lx - (x0 + 1);
        int iz = lz - (z0 + 1);
        int pisos = (alt - 4) / 6;

        // ---- escadas: voo kk liga o andar kk ao kk+1; alterna norte (sobe p/ leste) e sul (sobe p/ oeste)
        for (int kk = 0; kk < pisos; kk++) {
            boolean norte = kk % 2 == 0;
            int linha = norte ? 0 : D - 1;
            if (iz == linha) {
                int i = norte ? ix : (W - 1 - ix);
                if (i >= 0 && i <= 5) {
                    int yy = 6 * kk + i;
                    if (y == yy) {
                        return escada(Blocks.STONE_BRICK_STAIRS, norte ? Direction.EAST : Direction.WEST);
                    }
                    if (y >= 6 * kk && y < yy) return bloco(Blocks.STONE_BRICKS);
                }
            }
        }

        // ---- lajes dos pisos (y = 6k + 5), com buraco sobre o voo de escada
        if (y % 6 == 5 && y / 6 < pisos) {
            int kk = y / 6;
            boolean norte = kk % 2 == 0;
            int linha = norte ? 0 : D - 1;
            int i = norte ? ix : (W - 1 - ix);
            if (iz == linha && i >= 0 && i <= 4) return null;
            return bloco(Blocks.DARK_OAK_PLANKS);
        }

        // ---- mobilia do andar
        int nivel = y / 6;
        int yr = y - 6 * nivel;
        if (y >= 0 && nivel <= pisos && yr <= 3) {
            int tpl = template(tipoPredio, cu, cv, nivel, idx);
            BlockState m = mobilia(tpl, ix, iz, yr, W, D, hash(dx, y, dz) % 100);
            if (m != null) return m;
        }
        // lampiao pendurado sob a laje do andar de cima
        if (y >= 0 && yr == 4 && nivel < pisos && ix == W / 2 && iz == D / 2) {
            return bloco(Blocks.SOUL_LANTERN).setValue(LanternBlock.HANGING, true);
        }
        if (y >= 2 && hash(dx, y, dz) % 1000 < 4) return bloco(Blocks.COBWEB);
        return null;
    }

    /** Escolhe o tipo de comodo: 0 biblioteca, 1 dormitorio, 2 armazem, 3 oficina/sala. */
    private int template(int tipoPredio, int cu, int cv, int nivel, int idx) {
        if (tipoPredio == 2) return nivel == 0 ? 3 : 1;
        int h = hash(cu, 31 + nivel, cv + idx * 7) % 4;
        if (tipoPredio == 1) return h == 3 ? 2 : 0;
        return h;
    }

    /** Celulas de bau de cada tipo de comodo: {ix, iz, direcao}. */
    private List<int[]> bausDe(int tpl, int W, int D) {
        List<int[]> r = new ArrayList<>();
        if (tpl == 1) {
            r.add(new int[]{W / 2, D - 3, Direction.NORTH.get3DDataValue()});
        } else if (tpl == 2) {
            for (int iz = 2; iz <= D - 3; iz++) {
                if (iz % 2 == 0) r.add(new int[]{W - 1, iz, Direction.WEST.get3DDataValue()});
            }
        }
        return r;
    }

    private BlockState bau(Direction d) {
        return bloco(Blocks.CHEST).setValue(ChestBlock.FACING, d);
    }

    /** Mobilia de um comodo (ix, iz = posicao interior; yr = altura sobre o piso do andar). */
    private BlockState mobilia(int tpl, int ix, int iz, int yr, int W, int D, int h) {
        boolean oeste = ix == 0;
        boolean leste = ix == W - 1;
        boolean fileira = iz >= 2 && iz <= D - 3;   // linhas 1 e D-2 ficam livres (saida das escadas)
        int cx = W / 2;
        int cz = D / 2;

        switch (tpl) {
            case 0: // biblioteca
                if ((oeste || leste) && fileira && yr <= 2) {
                    return h < 82 ? bloco(Blocks.BOOKSHELF) : null;
                }
                return mesa(ix, iz, yr, cx, cz, h);
            case 1: { // dormitorio
                if (yr == 0 && (ix == 1 || ix == W - 2) && (iz == 2 || iz == 3) && W >= 5) {
                    Block cama = ix == 1 ? Blocks.YELLOW_BED : Blocks.BLACK_BED;
                    BedPart parte = iz == 2 ? BedPart.FOOT : BedPart.HEAD;
                    return cama.defaultBlockState().setValue(BedBlock.FACING, Direction.SOUTH)
                            .setValue(BedBlock.PART, parte);
                }
                if (yr == 0 && ix == cx && iz == D - 3) return bau(Direction.NORTH);
                if (yr == 0 && ix >= 2 && ix <= W - 3 && fileira && !(ix == cx && iz == D - 3)) {
                    return h < 90 ? bloco(Blocks.YELLOW_CARPET) : null;
                }
                if (yr <= 2 && (oeste || leste) && iz == D - 3 && h < 70) return bloco(Blocks.BOOKSHELF);
                return null;
            }
            case 2: // armazem
                if (leste && fileira && yr == 0 && iz % 2 == 0) return bau(Direction.WEST);
                if (leste && fileira && yr <= 1 && iz % 2 != 0) return h < 85 ? bloco(Blocks.BARREL) : null;
                if (oeste && fileira && yr <= 1) return h < 80 ? bloco(Blocks.BARREL) : null;
                if (ix == cx && iz == cz && yr <= 1) return bloco(Blocks.BARREL);
                return null;
            default: // oficina / sala
                if (oeste && fileira && yr == 0) {
                    if (iz == 2) return bloco(Blocks.CRAFTING_TABLE);
                    if (iz == 3) return bloco(Blocks.FURNACE).setValue(FurnaceBlock.FACING, Direction.EAST);
                    if (iz == 4) return bloco(Blocks.BARREL);
                }
                return mesa(ix, iz, yr, cx, cz, h);
        }
    }

    /** Mesa com duas cadeiras e um lampiao. */
    private BlockState mesa(int ix, int iz, int yr, int cx, int cz, int h) {
        if (ix == cx && iz == cz) {
            if (yr == 0) return bloco(Blocks.DARK_OAK_FENCE);
            if (yr == 1) return bloco(Blocks.DARK_OAK_SLAB);
            if (yr == 2 && h < 70) return bloco(Blocks.SOUL_LANTERN);
        }
        if (yr == 0 && iz == cz && ix == cx - 1) return escada(Blocks.DARK_OAK_STAIRS, Direction.WEST);
        if (yr == 0 && iz == cz && ix == cx + 1) return escada(Blocks.DARK_OAK_STAIRS, Direction.EAST);
        return null;
    }

    // ------------------------------------------------------------------
    //  Loot dos baus
    // ------------------------------------------------------------------

    @Override
    protected void depois(WorldGenLevel level, BoundingBox box) {
        RandomSource rnd = RandomSource.create(semente);
        for (int cu = 0; cu <= 7; cu++) {
            for (int cv = 0; cv <= 7; cv++) {
                if ((cu == 3 || cu == 4) && (cv == 3 || cv == 4)) continue; // area da praca
                for (int[] p : predios(cu, cv)) {
                    int W = p[2] - p[0] - 1;
                    int D = p[3] - p[1] - 1;
                    int pisos = (p[4] - 4) / 6;
                    for (int nivel = 0; nivel <= pisos; nivel++) {
                        int tpl = template(p[6], cu, cv, nivel, p[7]);
                        for (int[] c : bausDe(tpl, W, D)) {
                            int dx = 24 * cu - 93 + (p[0] + 1 + c[0]);
                            int dz = 24 * cv - 93 + (p[1] + 1 + c[1]);
                            BlockPos pos = new BlockPos(ox + dx, oy + 6 * nivel, oz + dz);
                            if (box.isInside(pos) && level.getBlockState(pos).is(Blocks.CHEST)) {
                                RandomizableContainerBlockEntity.setLootTable(level, rnd, pos, LOOT);
                            }
                        }
                    }
                }
            }
        }
    }
}
