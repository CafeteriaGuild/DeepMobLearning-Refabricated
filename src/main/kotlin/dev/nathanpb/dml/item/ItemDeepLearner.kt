package dev.nathanpb.dml.item

import dev.nathanpb.dml.NotDeepLearnerException
import dev.nathanpb.dml.utils.toTag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.DefaultedList

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

class ItemDeepLearner : Item(settings()) {
    companion object {
        const val INVENTORY_SIZE = 4
        const val INVENTORY_TAG = "deepmoblearning.deep_learner.inventory";
    }
}

var ItemStack.deepLearnerInventory: DefaultedList<ItemStack>
    get() {
        if (this.item is ItemDeepLearner) {
            return orCreateTag.let { tag ->
                DefaultedList.ofSize(ItemDeepLearner.INVENTORY_SIZE, ItemStack.EMPTY).apply {
                    tag.getList(ItemDeepLearner.INVENTORY_TAG, 10)
                        ?.filterIsInstance<CompoundTag>()
                        ?.map { stackTag -> ItemStack.fromTag(stackTag) }
                        ?.forEachIndexed { index, itemStack -> set(index, itemStack) }
                }
            }
        } else throw NotDeepLearnerException()
    }
    set(inventory) {
        if (this.item is ItemDeepLearner) {
            orCreateTag.let { tag ->
                ListTag().apply {
                    addAll(inventory.map { stack -> stack.toTag() })
                }.let { stacks ->
                    tag.put(ItemDeepLearner.INVENTORY_TAG, stacks)
                }
            }
        } else throw NotDeepLearnerException()
    }
