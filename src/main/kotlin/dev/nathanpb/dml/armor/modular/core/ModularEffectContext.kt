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

import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.item.ItemModularGlitchArmor
import net.minecraft.entity.player.PlayerEntity

data class ModularEffectContext (
    val effect: ModularEffect,
    val player: PlayerEntity,
    val dataModel: DataModelData,
    val armor: ModularArmorData,
    val tier: DataModelTier,
    val item: ItemModularGlitchArmor = armor.stack.item as ItemModularGlitchArmor
)
