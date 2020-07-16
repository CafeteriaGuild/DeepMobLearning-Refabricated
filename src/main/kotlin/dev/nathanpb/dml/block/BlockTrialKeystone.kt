/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.data.trialKeyData
import dev.nathanpb.dml.item.ItemTrialKey
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.trial.TrialData
import dev.nathanpb.dml.trial.TrialKeystoneIllegalStartException
import dev.nathanpb.dml.trial.TrialKeystoneWrongTerrainException
import dev.nathanpb.dml.trial.TrialWaveData
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.EntityContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class BlockTrialKeystone : Block(
    FabricBlockSettings.of(Material.STONE)
        .hardness(4F)
        .resistance(3000F)
), BlockEntityProvider {

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            (world.getBlockEntity(pos) as? BlockEntityTrialKeystone)?.let { blockEntity ->
                val stackInHand = player.getStackInHand(hand)
                if (stackInHand.item is ItemTrialKey) {
                    stackInHand.trialKeyData?.let { data ->
                        TrialKeystoneRecipe.findOrNull(world, data)
                    }.let { data ->
                        if (data != null) {
                            try {
                                stackInHand.decrement(1)
                                val trialData = TrialData(data, TrialWaveData.fromRecipe(data))
                                val trial = blockEntity.createTrial(trialData)
                                blockEntity.startTrial(trial)
                                return ActionResult.CONSUME
                            } catch (ex: TrialKeystoneIllegalStartException) {
                                return ActionResult.PASS
                            } catch (ex: TrialKeystoneWrongTerrainException) {
                                player.addChatMessage(TranslatableText("chat.deepmoblearning.trial_wrong_terrain"), false)
                            }
                        } else {
                            player.addChatMessage(TranslatableText("chat.deepmoblearning.trial_no_recipe"), false)
                        }
                        return ActionResult.FAIL
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun getOutlineShape(
        state: BlockState?,
        view: BlockView?,
        pos: BlockPos?,
        context: EntityContext?
    ): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        (world.getBlockEntity(pos) as? BlockEntityTrialKeystone)?.let { entity ->
            entity.currentTrial = null
        }
        super.scheduledTick(state, world, pos, random)
    }

    override fun createBlockEntity(view: BlockView?) = BlockEntityTrialKeystone()
}
