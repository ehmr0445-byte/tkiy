package com.kingyellow.mod;

import com.kingyellow.mod.entity.ReiAmareloEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Atributos das entidades do mod (barramento do mod, comum a cliente e servidor). */
@Mod.EventBusSubscriber(modid = KingYellowMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntidadesEventos {

    @SubscribeEvent
    public static void atributos(EntityAttributeCreationEvent evento) {
        evento.put(ModEntities.REI_AMARELO.get(), ReiAmareloEntity.criarAtributos().build());
    }
}
