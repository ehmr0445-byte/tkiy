package com.kingyellow.mod;

import com.kingyellow.mod.worldgen.CasteloAbandonadoPiece;
import com.kingyellow.mod.worldgen.CasteloAbandonadoStructure;
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

    public static final RegistryObject<StructureType<CasteloAbandonadoStructure>> CASTELO =
            STRUCTURES.register("castelo_abandonado",
                    () -> (StructureType<CasteloAbandonadoStructure>) () -> CasteloAbandonadoStructure.CODEC);

    public static final RegistryObject<StructurePieceType> CASTELO_PIECE =
            PIECES.register("castelo_abandonado",
                    () -> (StructurePieceType.ContextlessType) CasteloAbandonadoPiece::new);
}
