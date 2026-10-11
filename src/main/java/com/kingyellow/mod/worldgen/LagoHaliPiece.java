package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModStructures;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Lago de Hali (centro no meio do chunk -40,0): um lago enorme de agua luminosa, com uma
 * ilha no centro onde cresce uma arvore gigante.
 *
 *  - Margem irregular (raio medio 78, ate ~95 blocos)
 *  - Agua azul capri brilhante (cor definida no bioma) com lanternas marinhas e luz no fundo
 *  - Arvore: tronco largo com raizes, galhos grossos e copa de ~80 blocos de diametro
 *    (as folhas ganham o tom azul-violeta definido no bioma)
 */
public class LagoHaliPiece extends PecaProcedural {
    public static final int RAIO = 108;
    private static final int HMAX = 62;
    private static final int PROF = 14;

    /** Esqueleto da arvore: trechos de galho {x1,y1,z1,r1, x2,y2,z2,r2}. */
    private static final double[][] SEG;
    /** Nuvens de folhas (elipsoides): {cx, cy, cz, raioHorizontal, raioVertical}. */
    private static final double[][] BLOB;

    static {
        List<double[]> seg = new ArrayList<>();
        List<double[]> blob = new ArrayList<>();
        Random rnd = new Random(1895L);

        // tronco torcido em espiral, grosso na base
        double[][] pt = new double[13][3];
        for (int i = 0; i <= 12; i++) {
            double y = i * 3.0;
            double ang = y * 0.19;
            double amp = 6.5 * (1.0 - y / 55.0);
            pt[i][0] = amp * Math.sin(ang);
            pt[i][1] = y;
            pt[i][2] = amp * Math.cos(ang);
        }
        for (int i = 0; i < 12; i++) {
            double r1 = Math.max(2.4, 7.2 - pt[i][1] * 0.14);
            double r2 = Math.max(2.4, 7.2 - pt[i + 1][1] * 0.14);
            seg.add(new double[]{pt[i][0], pt[i][1], pt[i][2], r1, pt[i + 1][0], pt[i + 1][1], pt[i + 1][2], r2});
        }

        // 5 galhos principais que se abrem em curvas amplas, com ramos secundarios
        int[] inicio = {5, 6, 7, 8, 9};
        for (int k = 0; k < 5; k++) {
            double[] base = pt[inicio[k]];
            double a = k * 2 * Math.PI / 5 + rnd.nextDouble() * 0.6;
            double px = base[0];
            double py = base[1];
            double pz = base[2];
            double r = 2.6;
            double subida = 2.2;
            int passos = 7 + rnd.nextInt(3);
            for (int st = 0; st < passos; st++) {
                a += (rnd.nextDouble() - 0.5) * 0.7;
                double nx = px + Math.cos(a) * 4.0;
                double nz = pz + Math.sin(a) * 4.0;
                double ny = py + subida;
                double nr = Math.max(0.9, r - 0.25);
                seg.add(new double[]{px, py, pz, r, nx, ny, nz, nr});
                if (st == 3 || st == 5) {
                    double sa = a + (st == 3 ? 0.9 : -0.9);
                    double sx = nx;
                    double sy = ny;
                    double sz = nz;
                    double sr = 1.5;
                    for (int q = 0; q < 3; q++) {
                        double tx = sx + Math.cos(sa) * 3.5;
                        double tz = sz + Math.sin(sa) * 3.5;
                        double ty = sy + 1.2;
                        seg.add(new double[]{sx, sy, sz, sr, tx, ty, tz, Math.max(0.7, sr - 0.3)});
                        sx = tx;
                        sy = ty;
                        sz = tz;
                        sr = Math.max(0.7, sr - 0.3);
                    }
                    blob.add(new double[]{sx, sy + 1.5, sz, 8 + rnd.nextDouble() * 2, 4.5 + rnd.nextDouble()});
                }
                px = nx;
                py = ny;
                pz = nz;
                r = nr;
                subida = Math.max(0.2, subida * 0.78);
                if (st >= 2 && st % 2 == 0) {
                    blob.add(new double[]{px, py + 2, pz, 7 + rnd.nextDouble() * 3, 4 + rnd.nextDouble() * 1.5});
                }
            }
            blob.add(new double[]{px, py + 1, pz, 11 + rnd.nextDouble() * 3, 6 + rnd.nextDouble() * 2});
        }
        // copa do topo
        blob.add(new double[]{pt[12][0], pt[12][1] + 4, pt[12][2], 13, 7});

        SEG = seg.toArray(new double[0][]);
        BLOB = blob.toArray(new double[0][]);
    }

    public LagoHaliPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.LAGO_HALI_PIECE.get(), ox, oy, oz, semente, RAIO, HMAX, PROF);
    }

    public LagoHaliPiece(CompoundTag tag) {
        super(ModStructures.LAGO_HALI_PIECE.get(), tag);
    }

    private BlockState folha() {
        return bloco(Blocks.OAK_LEAVES).setValue(LeavesBlock.PERSISTENT, true);
    }

    @Override
    protected BlockState amostra(int dx, int y, int dz) {
        double d = Math.sqrt((double) dx * dx + (double) dz * dz);
        double ang = Math.atan2(dz, dx);
        double margem = 78 + 9 * Math.sin(3 * ang + 1.0) + 5 * Math.cos(5 * ang) + 3 * Math.sin(9 * ang);

        // ---------- ilha central ----------
        if (y <= -1 && y >= -12) {
            double raioIlha = 12 + (y < -6 ? 4 : 0);
            if (d <= raioIlha) {
                return y == -1 ? bloco(Blocks.GRASS_BLOCK) : bloco(Blocks.DIRT);
            }
        }

        // ---------- arvore gigante ----------
        if (y >= 0) {
            BlockState arvore = arvore(dx, y, dz, d, ang);
            if (arvore != null) return arvore;
            if (d <= 12 && y == 0 && hash(dx, y, dz) % 100 < 6) return bloco(Blocks.FERN);
        }

        // ---------- agua ----------
        if (d < margem) {
            int fundo = d < margem - 6 ? -12 : (d < margem - 3 ? -8 : -4);
            if (y == -1) return AR;
            if (y >= fundo && y <= -2) {
                int h = hash(dx, y, dz) % 100;
                if (y == fundo && h < 5) return bloco(Blocks.SEA_LANTERN);
                // blocos de luz invisiveis dentro da agua: deixam o lago brilhante
                if (y == -4 && h < 6) {
                    return bloco(Blocks.LIGHT).setValue(BlockStateProperties.LEVEL, 13)
                            .setValue(BlockStateProperties.WATERLOGGED, true);
                }
                return bloco(Blocks.WATER);
            }
        }
        return null;
    }

    private BlockState arvore(int dx, int y, int dz, double d, double ang) {
        if (d > 56.0 || y > 58) return null;

        // madeira: troncos e galhos
        if (emMadeira(dx, y, dz)) return madeira(dx, y, dz);
        // raizes se espalhando pelo chao da ilha
        if (y == 0 && d > 6 && d < 20 && Math.abs(Math.sin(6 * ang)) > 0.93) return madeira(dx, y, dz);

        // folhas: nuvens com bordas irregulares
        int h = hash(dx, y, dz) % 100;
        double v = menorV(dx, y, dz);
        if (v + h / 100.0 * 0.45 < 1.0) return folha();

        // folhas pendentes (como glicinias) sob as nuvens
        if (hash(dx, 7, dz) % 100 < 14) {
            int comp = 2 + hash(dx, 9, dz) % 5;
            for (int k = 1; k <= comp; k++) {
                if (menorV(dx, y + k, dz) < 0.7) return folha();
            }
        }
        return null;
    }

    /** O ponto esta dentro de algum trecho de tronco/galho? */
    private boolean emMadeira(int px, int py, int pz) {
        for (double[] sg : SEG) {
            double r = Math.max(sg[3], sg[7]);
            if (py < Math.min(sg[1], sg[5]) - r - 1 || py > Math.max(sg[1], sg[5]) + r + 1) continue;
            double ax = sg[0];
            double ay = sg[1];
            double az = sg[2];
            double bx = sg[4] - ax;
            double by = sg[5] - ay;
            double bz = sg[6] - az;
            double len2 = bx * bx + by * by + bz * bz;
            double t = len2 == 0 ? 0 : ((px - ax) * bx + (py - ay) * by + (pz - az) * bz) / len2;
            t = Math.max(0.0, Math.min(1.0, t));
            double cx = ax + bx * t - px;
            double cy = ay + by * t - py;
            double cz = az + bz * t - pz;
            double rad = sg[3] + (sg[7] - sg[3]) * t;
            if (cx * cx + cy * cy + cz * cz <= rad * rad) return true;
        }
        return false;
    }

    /** Menor valor de "distancia normalizada" ao centro das nuvens de folhas (< 1 = dentro). */
    private double menorV(int px, int py, int pz) {
        double melhor = 9.0;
        for (double[] b : BLOB) {
            double ex = (px - b[0]) / b[3];
            double ey = (py - b[1]) / b[4];
            double ez = (pz - b[2]) / b[3];
            double v = ex * ex + ey * ey + ez * ez;
            if (v < melhor) melhor = v;
        }
        return melhor;
    }

    /** Casca clara de bétula com listras em espiral de tons de areia (como na referencia). */
    private BlockState madeira(int dx, int y, int dz) {
        int h = hash(dx, y, dz) % 100;
        int faixa = (int) Math.floor(dx * 0.30 + dz * 0.30 + y * 0.28);
        boolean clara = (Math.floorMod(faixa, 2) == 0) ^ (h < 12);
        if (clara) {
            return h % 2 == 0 ? bloco(Blocks.BIRCH_WOOD) : bloco(Blocks.STRIPPED_BIRCH_WOOD);
        }
        return h < 60 ? bloco(Blocks.STRIPPED_ACACIA_WOOD) : bloco(Blocks.STRIPPED_OAK_WOOD);
    }
}
