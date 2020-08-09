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

package dev.nathanpb.dml.armor.modular.core

import dev.nathanpb.dml.trial.affix.core.DuplicatedRegistryException
import net.minecraft.util.Identifier

class ModularEffectRegistry {
    companion object {
        val INSTANCE = ModularEffectRegistry()

        fun registerDefaults() {
            INSTANCE.apply {
                // TODO register the effects here
            }
        }
    }

    private val entries = mutableListOf<ModularEffect>()

    fun register(effect: ModularEffect) {
        if (entries.none { it.id == effect.id }) {
            entries += effect.also(ModularEffect::registerEvents)
        } else throw DuplicatedRegistryException(effect.id)
    }

    val all
        get() = entries.toList()

    fun fromId(id: Identifier) = entries.firstOrNull { it.id == id }

    @Suppress("unchecked_cast")
    fun <T>fromId(id: Identifier) = fromId(id) as? T?
}
