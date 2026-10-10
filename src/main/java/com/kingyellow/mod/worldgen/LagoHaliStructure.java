package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.Carcosa;
import com.kingyellow.mod.ModStructures;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.*;

/** O Lago de Hali (dimensao Carcosa). */
public class LagoHaliStructure extends EstruturaCarcosaBase {
    public static final Codec<LagoHaliStructure> CODEC = simpleCodec(LagoHaliStructure::new);

    public LagoHaliStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected int chunkX() {
        return Carcosa.LAGO_CHUNK_X;
    }

    @Override
    protected int chunkZ() {
        return Carcosa.LAGO_CHUNK_Z;
    }

    @Override
    protected StructurePiece criarPeca(int cx, int oy, int cz, int semente) {
        return new LagoHaliPiece(cx, oy, cz, semente);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LAGO_HALI.get();
    }
}
