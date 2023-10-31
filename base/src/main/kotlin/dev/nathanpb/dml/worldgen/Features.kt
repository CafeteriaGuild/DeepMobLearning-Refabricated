/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.worldgen

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.worldgen.disruption.DisruptionFeature
import dev.nathanpb.dml.worldgen.disruption.DisruptionFeatureConfig
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeature
import java.util.function.Predicate


fun registerFeatures() {
    Registry.register(Registries.FEATURE, DISRUPTION_KEY.value, DISRUPTION_FEATURE)

    /*addFeature(
        DISRUPTION_KEY,
        BiomeSelectors.foundInOverworld().and(
            Predicate.not(BiomeSelectors.tag(ConventionalBiomeTags.AQUATIC))
        ),
        true
    ) // TODO add config to disable*/
}


private fun addFeature(registryKey: RegistryKey<PlacedFeature>, biomeSelector: Predicate<BiomeSelectionContext>, enabled: Boolean) {
    if (!enabled) return
    BiomeModifications.addFeature(biomeSelector, GenerationStep.Feature.VEGETAL_DECORATION, registryKey)
}

val DISRUPTION_FEATURE: Feature<DisruptionFeatureConfig> = DisruptionFeature(DisruptionFeatureConfig.CODEC)
val DISRUPTION_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier("disruption"))
