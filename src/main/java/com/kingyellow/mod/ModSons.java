package com.kingyellow.mod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Sons do mod (arquivos em assets/kingyellow/sounds, declarados em sounds.json). */
public class ModSons {
    public static final DeferredRegister<SoundEvent> SONS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, KingYellowMod.MOD_ID);

    public static final RegistryObject<SoundEvent> SUSSURROS = SONS.register("sussurros",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(KingYellowMod.MOD_ID, "sussurros")));
}
