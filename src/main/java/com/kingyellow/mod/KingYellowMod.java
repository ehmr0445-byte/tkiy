package com.kingyellow.mod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Classe principal do mod "The King In Yellow".
 * O MOD_ID deve ser igual ao definido em gradle.properties.
 */
@Mod(KingYellowMod.MOD_ID)
public class KingYellowMod {
    public static final String MOD_ID = "kingyellow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public KingYellowMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registros do mod (cada etapa nova adiciona seus registros aqui)
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        LOGGER.info("The King In Yellow carregado. O Rei aguarda.");
    }
}
