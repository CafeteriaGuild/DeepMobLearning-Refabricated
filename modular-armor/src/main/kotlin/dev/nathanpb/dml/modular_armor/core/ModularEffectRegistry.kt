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

package dev.nathanpb.dml.modular_armor.core

import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.modular_armor.effects.*
import dev.nathanpb.dml.trial.affix.core.DuplicatedRegistryException
import net.minecraft.util.Identifier

class ModularEffectRegistry {
    companion object {
        val INSTANCE = ModularEffectRegistry()

        fun registerDefaults() {
            INSTANCE.apply {
                // Nether
                register(FireProtectionEffect())
                register(PiglinTruceEffect())
                register(AutoExtinguishEffect())
                register(FireImmunityEffect())

                // Slimy
                register(FeatherFallingEffect())
                register(JumpBoostEffect())
                register(FallImmunityEffect())

                // Overworld
                register(PlentyEffect())

                // Zombie
                register(UnrottenFleshEffect())
                register(RotResistanceEffect())
                register(ZombieFriendlyEffect())

                // Skeleton
                register(ArcheryEffect())
                register(SkeletonFriendlyEffect())

                // End
                register(EndermenProofVisionEffect())
                register(TeleportEffect())
                register(ShulkerFriendlyEffect())

                // Ghost
                register(SoulVisionEffect())
                register(NightVisionEffect())
                register(FlyEffect())

                // Illager
                register(ResistanceEffect())
                register(UndyingEffect())

                // Ocean
                register(UnderwaterHasteEffect())
                register(DepthStriderEffect())
                register(WaterBreathingEffect())
                register(PoseidonBlessEffect())
            }
        }
    }

    private val entries = mutableListOf<ModularEffect<*>>()

    fun register(effect: ModularEffect<*>) {
        if (entries.none { it.id == effect.id }) {
            entries += effect.apply {
                registerEntityAttribute()
                registerEvents()
            }
        } else throw DuplicatedRegistryException(effect.id)
    }

    val all
        get() = entries.toList()

    fun fromId(id: Identifier) = entries.firstOrNull { it.id == id }

    @Suppress("unchecked_cast")
    fun <T>fromId(id: Identifier) = fromId(id) as? T?

    fun allMatching(category: EntityCategory, tier: DataModelTier) = entries.filter {
        it.isEnabled() && it.category == category && it.acceptTier(tier)
    }
}
