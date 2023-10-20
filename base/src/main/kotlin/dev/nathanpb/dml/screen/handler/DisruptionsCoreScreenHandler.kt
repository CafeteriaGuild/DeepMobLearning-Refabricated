/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.screen.handler

import dev.nathanpb.dml.block.BLOCK_DISRUPTIONS_CORE
import dev.nathanpb.dml.utils.RenderUtils
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext

class DisruptionsCoreScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val ctx: ScreenHandlerContext
) : SyncedGuiDescription(
    HANDLER_DISRUPTIONS_CORE,
    syncId, playerInventory,
    getBlockInventory(ctx),
    getBlockPropertyDelegate(ctx)
) {
    init {
        val root = WPlainPanel()
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL

        val slots = WItemSlot.of(blockInventory, 0, 9, 3)
        root.add(slots, 0, 0 + 10)

        root.add(createPlayerInventoryPanel(), 0, 3*18 + 13)

        root.validate(this)
    }

    override fun canUse(player: PlayerEntity) = ScreenHandler.canUse(ctx, player, BLOCK_DISRUPTIONS_CORE)

    override fun addPainters() {
        rootPanel.backgroundPainter = RenderUtils.DEFAULT_BACKGROUND_PAINTER
    }

    override fun getTitleColor(): Int {
        return RenderUtils.TITLE_COLOR
    }

}