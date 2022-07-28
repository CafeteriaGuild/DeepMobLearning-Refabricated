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

package dev.nathanpb.dml.block

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class BlockCafeteria : HorizontalFacingBlock(
    FabricBlockSettings.of(Material.STONE)
        .hardness(1F)
        .resistance(.5F)
        .nonOpaque()
) {
    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext?
    ) {
        if(world != null) {
        RenderUtils.getTextWithDefaultTextColor(Text.translatable("tooltip.${MOD_ID}.cafeteria.joinus"), world as World)
            .append(Text.of("https://discord.gg/G4PjhEf").copy().formatted(Formatting.WHITE))?.let { tooltip.add(it) }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(FACING, ctx?.playerFacing?.opposite)
    }


    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape? {
        return when(state?.get(FACING)) {
            Direction.NORTH -> cafeteriaNorth
            Direction.SOUTH -> cafeteriaSouth
            Direction.WEST -> cafeteriaWest
            Direction.EAST -> cafeteriaEast
            else -> VoxelShapes.empty()
        }
    }

    private val baseNorth: VoxelShape = Block.createCuboidShape(4.0, 0.0, 2.0, 16.0, 12.0, 14.0)
    private val handleNorth: VoxelShape = Block.createCuboidShape(0.0, 2.0, 7.0, 4.0, 10.0, 9.0)
    private val cafeteriaNorth: VoxelShape = VoxelShapes.union(baseNorth, handleNorth)

    private val baseSouth: VoxelShape = Block.createCuboidShape(0.0, 0.0, 2.0, 12.0, 12.0, 14.0)
    private val handleSouth: VoxelShape = Block.createCuboidShape(12.0, 2.0, 7.0, 16.0, 10.0, 9.0)
    private val cafeteriaSouth: VoxelShape = VoxelShapes.union(baseSouth, handleSouth)

    private val baseWest: VoxelShape = Block.createCuboidShape(2.0, 0.0, 0.0, 14.0, 12.0, 12.0)
    private val handleWest: VoxelShape = Block.createCuboidShape(7.0, 2.0, 12.0, 9.0, 10.0, 16.0)
    private val cafeteriaWest: VoxelShape = VoxelShapes.union(baseWest, handleWest)

    private val baseEast: VoxelShape = Block.createCuboidShape(2.0, 0.0, 4.0, 14.0, 12.0, 16.0)
    private val handleEast: VoxelShape = Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 10.0, 4.0)
    private val cafeteriaEast: VoxelShape = VoxelShapes.union(baseEast, handleEast)

}