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

package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.identifier
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

class UnderwaterHasteEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x46D99C)
class DepthStriderEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x02A7D9)
class SoulVisionEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0xFFD900)

val UNDERWATER_HASTE_EFFECT = register("underwater_haste", UnderwaterHasteEffect())
val DEPTH_STRIDER_EFFECT = register("depth_strider", DepthStriderEffect())
val SOUL_VISION_EFFECT = register("soul_vision", SoulVisionEffect())

private fun register(idPath: String, entry: StatusEffect): StatusEffect {
    return Registry.register(Registries.STATUS_EFFECT, Registries.STATUS_EFFECT.toList().size, identifier(idPath).toString(), entry)
}

fun registerStatusEffects() {
    UNDERWATER_HASTE_EFFECT
    DEPTH_STRIDER_EFFECT
    SOUL_VISION_EFFECT
}
