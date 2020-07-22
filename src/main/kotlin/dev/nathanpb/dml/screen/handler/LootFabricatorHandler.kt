/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.screen.handler

import dev.nathanpb.dml.item.ItemPristineMatter
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerContext

class LootFabricatorHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    ctx: ScreenHandlerContext
) : SyncedGuiDescription(
    HANDLER_LOOT_FABRICATOR,
    syncId, playerInventory,
    getBlockInventory(ctx),
    getBlockPropertyDelegate(ctx)
) {
    init {
        val root = WGridPanel()
        setRootPanel(root)

        val inputSlot = WItemSlot.of(blockInventory, 0).setFilter { it.item is ItemPristineMatter }
        root.add(inputSlot, 1, 2)

        val progressBar = WBar(null, null, 0, 1, WBar.Direction.DOWN)
        progressBar.setSize(1, 128)
        root.add(progressBar, 3, 1, 1, 3)


        (0 until 9).forEach {
            val x = (it % 3)
            val y = (it / 3)
            val slot = WItemSlot.of(blockInventory, it + 1).setFilter { false }
            root.add(slot, x  + 5, y + 1)
        }

        root.add(this.createPlayerInventoryPanel(), 0, 5)

        root.validate(this)

        (blockInventory as? SimpleInventory)?.addListener {
            sendContentUpdates()
        }
    }

    override fun canUse(entity: PlayerEntity?) = true
}
