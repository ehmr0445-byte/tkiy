package com.kingyellow.mod.worldgen;

import com.kingyellow.mod.Carcosa;
import com.kingyellow.mod.ModStructures;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.*;

/** A Cidade de Carcosa. */
public class CidadeCarcosaStructure extends EstruturaCarcosaBase {
    public static final Codec<CidadeCarcosaStructure> CODEC = simpleCodec(CidadeCarcosaStructure::new);

    public CidadeCarcosaStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected int chunkX() {
        return Carcosa.CIDADE_CHUNK_X;
    }

    @Override
    protected int chunkZ() {
        return Carcosa.CIDADE_CHUNK_Z;
    }

    @Override
    protected StructurePiece criarPeca(int cx, int oy, int cz, int semente) {
        return new CidadeCarcosaPiece(cx, oy, cz, semente);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CIDADE.get();
    }
}
