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
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.util.Hand

class ModularArmorScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    hand: Hand
): SyncedGuiDescription(
    HANDLER_MODULAR_ARMOR,
    syncId,
    playerInventory,
    SimpleInventory(ModularArmorData(playerInventory.player.getStackInHand(hand)).dataModel?.stack ?: ItemStack.EMPTY),
    ArrayPropertyDelegate(1)
) {
    val stack: ItemStack = playerInventory.player.getStackInHand(hand)
    val data = ModularArmorData(stack)

    init {
        val root = WGridPanel()
        setRootPanel(root)

        val dataModelSlot = WItemSlot.of(blockInventory, 0).setFilter {
            it.isEmpty || (
                (it.item as? ItemDataModel)?.category != null
                && data.tier().ordinal >= it.dataModel.tier().ordinal
            )
        }

        root.add(dataModelSlot, 4, 2)

        root.add(this.createPlayerInventoryPanel(), 0, 5)
        root.validate(this)

        (blockInventory as? SimpleInventory)?.addListener {
            val stack = blockInventory.getStack(0)
            data.dataModel = if (stack.item is ItemDataModel) stack.dataModel else null
            sendContentUpdates()
        }
    }
}
