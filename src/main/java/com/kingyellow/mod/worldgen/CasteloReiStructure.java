package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.Carcosa;
import com.kingyellow.mod.ModStructures;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.*;

/** O Castelo do Rei de Amarelo (dimensao Carcosa). */
public class CasteloReiStructure extends EstruturaCarcosaBase {
    public static final Codec<CasteloReiStructure> CODEC = simpleCodec(CasteloReiStructure::new);

    public CasteloReiStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected int chunkX() {
        return Carcosa.CASTELO_CHUNK_X;
    }

    @Override
    protected int chunkZ() {
        return Carcosa.CASTELO_CHUNK_Z;
    }

    @Override
    protected StructurePiece criarPeca(int cx, int oy, int cz, int semente) {
        return new CasteloReiPiece(cx, oy, cz, semente);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CASTELO_REI.get();
    }
}
