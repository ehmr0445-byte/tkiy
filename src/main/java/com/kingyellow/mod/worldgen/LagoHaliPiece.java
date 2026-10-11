package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.ModStructures;
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

    public LagoHaliPiece(int ox, int oy, int oz, int semente) {
        super(ModStructures.LAGO_HALI_PIECE.get(), ox, oy, oz, semente, RAIO, HMAX, PROF);
    }

    public LagoHaliPiece(CompoundTag tag) {
        super(ModStructures.LAGO_HALI_PIECE.get(), tag);
    }

    private BlockState folha() {
        return bloco(Blocks.DARK_OAK_LEAVES).setValue(LeavesBlock.PERSISTENT, true);
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
        // tronco: alarga na base e afina ate a copa
        if (y <= 46) {
            double raioTronco = y <= 8 ? 8.0 - y * 0.45 : Math.max(3.0, 5.0 - (y - 8) * 0.04);
            if (d <= raioTronco) return bloco(Blocks.DARK_OAK_WOOD);
        }
        // raizes se espalhando pelo chao da ilha
        if (y == 0 && d > 8 && d < 20 && Math.abs(Math.sin(6 * ang)) > 0.93) {
            return bloco(Blocks.DARK_OAK_WOOD);
        }
        // galhos grossos subindo em 8 direcoes
        if (y >= 22 && y <= 38 && d >= 5 && d <= 32) {
            for (int k = 0; k < 8; k++) {
                double a = k * Math.PI / 4 + 0.3;
                double dif = ang - a;
                if (Math.cos(dif) > 0 && d * Math.abs(Math.sin(dif)) < 2.2 && y == 22 + (int) (d / 3.0)) {
                    return bloco(Blocks.DARK_OAK_WOOD);
                }
            }
        }
        // copa: elipsoide largo, com folhas esparsas
        if (y >= 24) {
            double ex = d / 40.0;
            double ey = (y - 42) / 17.0;
            if (ex * ex + ey * ey < 1.0 && hash(dx, y, dz) % 100 < 72) {
                return folha();
            }
        }
        return null;
    }
}
