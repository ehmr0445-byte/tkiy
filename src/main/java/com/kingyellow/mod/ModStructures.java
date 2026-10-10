package com.kingyellow.mod;

import com.kingyellow.mod.worldgen.CasteloAbandonadoPiece;
import com.kingyellow.mod.worldgen.CasteloAbandonadoStructure;
import com.kingyellow.mod.worldgen.CasteloReiPiece;
import com.kingyellow.mod.worldgen.CasteloReiStructure;
import com.kingyellow.mod.worldgen.CidadeCarcosaPiece;
import com.kingyellow.mod.worldgen.CidadeCarcosaStructure;
import com.kingyellow.mod.worldgen.LagoHaliPiece;
import com.kingyellow.mod.worldgen.LagoHaliStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Registro dos tipos de estrutura e das pecas geradas por codigo. */
public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, KingYellowMod.MOD_ID);
    public static final DeferredRegister<StructurePieceType> PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, KingYellowMod.MOD_ID);

    // Castelo Abandonado (Overworld)
    public static final RegistryObject<StructureType<CasteloAbandonadoStructure>> CASTELO =
            STRUCTURES.register("castelo_abandonado",
                    () -> (StructureType<CasteloAbandonadoStructure>) () -> CasteloAbandonadoStructure.CODEC);
    public static final RegistryObject<StructurePieceType> CASTELO_PIECE =
            PIECES.register("castelo_abandonado",
                    () -> (StructurePieceType.ContextlessType) CasteloAbandonadoPiece::new);

    // Cidade de Carcosa
    public static final RegistryObject<StructureType<CidadeCarcosaStructure>> CIDADE =
            STRUCTURES.register("cidade_carcosa",
                    () -> (StructureType<CidadeCarcosaStructure>) () -> CidadeCarcosaStructure.CODEC);
    public static final RegistryObject<StructurePieceType> CIDADE_PIECE =
            PIECES.register("cidade_carcosa",
                    () -> (StructurePieceType.ContextlessType) CidadeCarcosaPiece::new);

    // Castelo do Rei
    public static final RegistryObject<StructureType<CasteloReiStructure>> CASTELO_REI =
            STRUCTURES.register("castelo_do_rei",
                    () -> (StructureType<CasteloReiStructure>) () -> CasteloReiStructure.CODEC);
    public static final RegistryObject<StructurePieceType> CASTELO_REI_PIECE =
            PIECES.register("castelo_do_rei",
                    () -> (StructurePieceType.ContextlessType) CasteloReiPiece::new);

    // Lago de Hali
    public static final RegistryObject<StructureType<LagoHaliStructure>> LAGO_HALI =
            STRUCTURES.register("lago_de_hali",
                    () -> (StructureType<LagoHaliStructure>) () -> LagoHaliStructure.CODEC);
    public static final RegistryObject<StructurePieceType> LAGO_HALI_PIECE =
            PIECES.register("lago_de_hali",
                    () -> (StructurePieceType.ContextlessType) LagoHaliPiece::new);
}
