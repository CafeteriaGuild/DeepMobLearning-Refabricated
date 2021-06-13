/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.data

import dev.nathanpb.dml.data.serializers.TrialAffixListSerializer
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.TrialState
import dev.nathanpb.ktdatatag.data.MutableCompoundData
import dev.nathanpb.ktdatatag.serializer.EnumSerializer
import dev.nathanpb.ktdatatag.serializer.Serializers
import net.minecraft.nbt.NbtCompound

class TrialData(tag: NbtCompound = NbtCompound()) : MutableCompoundData(tag) {

    constructor(trial: Trial) : this() {
        state = trial.state
        glitchHealth = trial.systemGlitch?.health ?: 0F
        affixes = trial.affixes
        playersUuids = trial.players.toList()
        recipeId = trial.recipe.id
        tickCount = trial.tickCount
    }

    var state by persistentDefaulted(TrialState.NOT_STARTED, EnumSerializer(TrialState::class.java))
    var glitchHealth by persistentDefaulted(0F, Serializers.FLOAT)
    var affixes by persistentDefaulted(emptyList(), TrialAffixListSerializer())
    var playersUuids by persistentDefaulted(emptyList(), Serializers.UUID_LIST)
    var recipeId by persistentDefaulted(identifier(""), Serializers.IDENTIFIER)
    var tickCount by persistentDefaulted(0, Serializers.INT)
}
