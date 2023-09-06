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

package dev.nathanpb.dml.modular_armor.listener

import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.listener.DataCollectListener
import dev.nathanpb.dml.modular_armor.ItemGlitchSword.Companion.GLITCH_SWORD
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

class GlitchSwordDataCollectListener : DataCollectListener() {

    override fun modifyDataAmount(dataModelData: DataModelData) {
        dataModelData.dataAmount += config.dataCollection.glitchSwordDataBonus
    }

    override fun onlyIf(player: PlayerEntity, stack: ItemStack): Boolean {
        return player.getStackInHand(player.activeHand).isOf(GLITCH_SWORD) && super.onlyIf(player, stack)
    }

}