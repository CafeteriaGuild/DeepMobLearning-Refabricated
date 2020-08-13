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

package dev.nathanpb.dml.screen.handler

import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.item.ItemModularGlitchArmor
import dev.nathanpb.dml.item.ItemPristineMatter
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Texture
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerContext

class MatterCondenserHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    ctx: ScreenHandlerContext
) : SyncedGuiDescription(
    HANDLER_MATTER_CONDENSER,
    syncId, playerInventory,
    getBlockInventory(ctx),
    getBlockPropertyDelegate(ctx)
) {
    init {
        val root = WGridPanel()
        setRootPanel(root)

        val slots = WGridPanel()
        val armorSlot = WItemSlot.of(blockInventory, 0).setFilter {
            it.item is ItemModularGlitchArmor && !ModularArmorData(it).tier().isMaxTier()
        }

        slots.add(armorSlot, 2, 2)

        val matterSlots = (1..6).map {
            WItemSlot.of(blockInventory, it).setFilter { stack ->
                stack.item is ItemPristineMatter
            }
        }

        slots.add(matterSlots[0], 2, 0)
        slots.add(matterSlots[1], 0, 1)
        slots.add(matterSlots[2], 4, 1)
        slots.add(matterSlots[3], 0, 3)
        slots.add(matterSlots[4], 4, 3)
        slots.add(matterSlots[5], 2, 4)

        root.add(slots, 2, 1)
        root.add(this.createPlayerInventoryPanel(), 0, 6)

        val progressBar1 = WBar(null as Texture?, null, 0, 1, WBar.Direction.UP)
        val progressBar2 = WBar(null as Texture?, null, 0, 1, WBar.Direction.UP)
        root.add(progressBar1, 0, 1, 1, 5)
        root.add(progressBar2, 8, 1, 1, 5)

        root.validate(this)

        (blockInventory as? SimpleInventory)?.addListener {
            sendContentUpdates()
        }
    }
}
