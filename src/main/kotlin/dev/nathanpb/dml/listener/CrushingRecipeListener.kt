/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.listener

import dev.nathanpb.dml.recipe.CrushingRecipe
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class CrushingRecipeListener : AttackBlockCallback {
    override fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        pos: BlockPos,
        direction: Direction
    ): ActionResult {
        if (!world.isClient) {
            player.getStackInHand(hand)?.let { stack ->
                val inv = SimpleInventory(stack)
                world.getBlockState(pos)?.let { state ->
                    world.recipeManager
                        .values()
                        .filterIsInstance<CrushingRecipe>()
                        .firstOrNull {
                            state.block == it.block && it.matches(inv, world)
                        }?.apply {
                            player.inventory.offerOrDrop(world, craft(inv))
                            return ActionResult.CONSUME
                        }
                }
            }
        }
        return ActionResult.PASS
    }

}
