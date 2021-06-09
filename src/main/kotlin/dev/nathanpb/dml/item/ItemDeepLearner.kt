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

package dev.nathanpb.dml.item

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.NotDeepLearnerException
import dev.nathanpb.dml.screen.handler.CONTAINER_DEEP_LEARNER
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ItemDeepLearner : Item(settings().maxCount(1)) {
    companion object {
        const val INVENTORY_SIZE = 4
        const val INVENTORY_TAG = "${MOD_ID}.deep_learner.inventory"

    }

    override fun use(world: World?, player: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (world != null && player != null && hand != null) {
            if (!world.isClient) {
                (player as? ServerPlayerEntity)?.let {
                    ContainerProviderRegistry.INSTANCE.openContainer(
                        CONTAINER_DEEP_LEARNER, player
                    ) {
                        it.writeString(hand.toString())
                    }
                }
            }
        }
        return super.use(world, player, hand)
    }
}

var ItemStack.deepLearnerInventory: DefaultedList<ItemStack>
    get() {
        if (this.item is ItemDeepLearner) {
            return getOrCreateSubTag(ItemDeepLearner.INVENTORY_TAG).let { invTag ->
               DefaultedList.ofSize(ItemDeepLearner.INVENTORY_SIZE, ItemStack.EMPTY).also {
                   Inventories.readNbt(invTag, it)
               }
            }
        } else throw NotDeepLearnerException()
    }
    set(inventory) {
        if (this.item is ItemDeepLearner) {
            getOrCreateSubTag(ItemDeepLearner.INVENTORY_TAG).let { invTag ->
                Inventories.writeNbt(invTag, inventory)
            }
        } else throw NotDeepLearnerException()
    }
