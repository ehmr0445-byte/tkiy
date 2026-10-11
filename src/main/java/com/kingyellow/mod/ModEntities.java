package com.kingyellow.mod;

import com.kingyellow.mod.entity.ReiAmareloEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro das entidades do mod. */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, KingYellowMod.MOD_ID);

    public static final RegistryObject<EntityType<ReiAmareloEntity>> REI_AMARELO = ENTITIES.register("rei_amarelo",
            () -> EntityType.Builder.of(ReiAmareloEntity::new, MobCategory.MISC)
                    .sized(1.6F, 4.2F)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build(new ResourceLocation(KingYellowMod.MOD_ID, "rei_amarelo").toString()));
}
