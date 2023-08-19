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

import dev.nathanpb.dml.block.BLOCK_DATA_SYNTHESIZER
import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class DataSynthesizerScreenHandlerFactory(
   private val pos: BlockPos,
   private val handlerFactory: (Int, PlayerInventory, ScreenHandlerContext)->ScreenHandler
   ) : ExtendedScreenHandlerFactory {

    override fun getDisplayName() = Text.translatable(BLOCK_DATA_SYNTHESIZER.translationKey)

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return handlerFactory(syncId, inv, ScreenHandlerContext.create(inv.player.world, pos))
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity?, buf: PacketByteBuf?) {
        buf?.writeBlockPos(pos)
    }

}