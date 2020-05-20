package dev.nathanpb.dml.container

import dev.nathanpb.dml.NotDeepLearnerException
import dev.nathanpb.dml.container.slot.DataModelSlot
import dev.nathanpb.dml.inventory.InventoryDeepLearner
import dev.nathanpb.dml.item.ItemDeepLearner
import dev.nathanpb.dml.item.deepLearnerInventory
import net.minecraft.container.Container
import net.minecraft.container.ContainerListener
import net.minecraft.container.Slot
import net.minecraft.container.SlotActionType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList
import net.minecraft.util.Hand


/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

class ContainerDeepLearner (
    syncId: Int,
    val playerInventory: PlayerInventory,
    hand: Hand
) : Container(null, syncId) {
    val player = playerInventory.player
    val stack = player.getStackInHand(hand)
    val inventory = InventoryDeepLearner(this)

    init {

        if (stack.item !is ItemDeepLearner) {
            throw NotDeepLearnerException()
        }

        addSlot(DataModelSlot(inventory, 0, 134, 45))
        addSlot(DataModelSlot(inventory, 1, 152, 45))
        addSlot(DataModelSlot(inventory, 2, 134, 63))
        addSlot(DataModelSlot(inventory, 3, 152, 63))

        (0..2).forEach { i ->
            (0..8).forEach { m ->
                addSlot(Slot(
                    playerInventory,
                    slots.size - 1,
                    8 + m * 18,
                    84 + i * 18
                ))
            }
        }

        (0..8).forEach { i ->
            addSlot(Slot(playerInventory, slots.size - 1, 8 + i * 18, 142))
        }


        stack.deepLearnerInventory.forEachIndexed { index, itemStack ->
            getSlot(index).stack = itemStack
        }

        addListener(object : ContainerListener {
            override fun onContainerRegistered(container: Container?, defaultedList: DefaultedList<ItemStack>?) {
                // Not Implemented
            }

            override fun onContainerPropertyUpdate(container: Container?, propertyId: Int, i: Int) {
                // Not Implemented
            }

            override fun onContainerSlotUpdate(container: Container?, slotId: Int, itemStack: ItemStack?) {
                if (container === this@ContainerDeepLearner && slotId <= 3) {
                    container.onContentChanged(container.inventory)
                }
            }
        })
    }

    override fun onContentChanged(inventory: Inventory?) {
        if (inventory == this.inventory) {
            sendContentUpdates()
        }
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        stack.deepLearnerInventory = inventory.items
    }

    override fun transferSlot(player: PlayerEntity?, slotNum: Int): ItemStack? {
        val clickedSlot = getSlot(slotNum)
        if (clickedSlot != null && clickedSlot.hasStack()) {
            val clickedStack = clickedSlot.stack
            return clickedStack.copy().also {
                if (slotNum < inventory.invSize) {
                    if (!insertItem(clickedStack,  inventory.invSize, slots.size, true)) {
                        return ItemStack.EMPTY
                    }
                } else if (!insertItem(clickedStack, 0, inventory.invSize, false)) {
                    return ItemStack.EMPTY
                }
                if (clickedStack.isEmpty) {
                    clickedSlot.stack = ItemStack.EMPTY
                } else {
                    clickedSlot.markDirty()
                }
            }
        }
        return ItemStack.EMPTY
    }

    override fun onSlotClick(
        int_1: Int,
        int_2: Int,
        slotActionType_1: SlotActionType?,
        playerEntity_1: PlayerEntity
    ): ItemStack? {
        return if (int_1 > 0 && getSlot(int_1).stack == stack) {
            ItemStack.EMPTY
        } else {
            super.onSlotClick(int_1, int_2, slotActionType_1, playerEntity_1)
        }
    }

    override fun canUse(player: PlayerEntity?) = true
}
