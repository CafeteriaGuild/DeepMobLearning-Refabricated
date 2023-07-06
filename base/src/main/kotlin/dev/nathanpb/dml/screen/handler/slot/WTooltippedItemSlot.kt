package dev.nathanpb.dml.screen.handler.slot

import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.inventory.Inventory
import net.minecraft.text.Text

/*
 * This piece of software is part of Gabriel Henrique de Oliveira's 'Industrial Revolution', licensed under the Apache 2.0 License.
 *
 * The original code, in its integrity, can be seen here: https://github.com/GabrielOlvH/Industrial-Revolution/blob/master/src/main/kotlin/me/steven/indrev/gui/widgets/misc/WTooltipedItemSlot.kt
 */
open class WTooltippedItemSlot(
    private val emptyTooltip: MutableList<Text>,
    private val inventory: Inventory,
    private val startIndex: Int = 0,
    private val slotsWide: Int = 1,
    private val slotsHigh: Int = 1,
    big: Boolean = false
) : WItemSlot(inventory, startIndex, slotsWide, slotsHigh, big) {

    override fun renderTooltip(ctx: DrawContext, x: Int, y: Int, tX: Int, tY: Int) {
        val slots = startIndex until startIndex + (slotsHigh * slotsWide)
        if (emptyTooltip.size != 0 && slots.all { inventory.getStack(it).isEmpty }) {
            ctx.drawTooltip(MinecraftClient.getInstance().textRenderer, emptyTooltip, tX + x, tY + y)
        }
    }

    companion object {
        fun of(inventory: Inventory, index: Int, vararg emptyTooltip: Text): WTooltippedItemSlot =
            WTooltippedItemSlot(emptyTooltip.toMutableList(), inventory, index)

        fun of(
            inventory: Inventory,
            startIndex: Int,
            slotsWide: Int,
            slotsHigh: Int,
            vararg emptyTooltip: Text
        ): WTooltippedItemSlot = WTooltippedItemSlot(emptyTooltip.toMutableList(), inventory, startIndex, slotsWide, slotsHigh)
    }

}