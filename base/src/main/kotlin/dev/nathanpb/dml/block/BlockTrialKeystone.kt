/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.block

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.trialKeyData
import dev.nathanpb.dml.item.ItemTrialKey
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.trial.TrialKeystoneIllegalStartException
import dev.nathanpb.dml.trial.TrialKeystoneWrongTerrainException
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.takeOrNull
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

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
                        val recipe = TrialKeystoneRecipe.findOrNull(world, data)
                        if (recipe != null) {
                            try {
                                val trial = blockEntity.createTrial(recipe, data.affixes)
                                blockEntity.startTrial(
                                    trial,
                                    takeOrNull(
                                        !player.isCreative
                                        && config.trial.trialKeyConsume
                                        && config.trial.trialKeyReturnIfSucceed
                                    ) { stackInHand }
                                )

                                if (!player.isCreative && config.trial.trialKeyConsume)
                                    stackInHand.decrement(1)

                                return ActionResult.CONSUME
                            } catch (ex: TrialKeystoneIllegalStartException) {
                                return ActionResult.PASS
                            } catch (ex: TrialKeystoneWrongTerrainException) {
                                player.sendMessage(
                                    Text.translatable("chat.$MOD_ID.trial_wrong_terrain").setStyle(RenderUtils.STYLE)
                                )
                                blockEntity.checkTerrain().forEach {
                                    if(!world.getBlockState(it).isSideSolidFullSquare(world, it, Direction.UP)) {
                                        player.sendMessage(getInvalidTerrainText(it.x, it.y, it.z, true))
                                    } else {
                                        player.sendMessage(getInvalidTerrainText(it.x, it.y, it.z, false))
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(Text.translatable("chat.${MOD_ID}.trial_no_recipe"), false)
                        }
                        return ActionResult.FAIL
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> {
        return BlockEntityTrialKeystone.ticker as BlockEntityTicker<T>
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BlockEntityTrialKeystone(pos, state)


    private fun getInvalidTerrainText(x: Int, y: Int, z: Int, isFloor: Boolean): MutableText {
        return Text.literal("- ").formatted(Formatting.WHITE)
            .append(Text.translatable("chat.$MOD_ID.trial_wrong_terrain." + (if(isFloor) "floor" else "dome"))
                .setStyle(RenderUtils.STYLE))
            .append(Text.literal(String.format(" (%d, %d, %d)", x, y, z))
                .formatted(Formatting.WHITE))
    }
}