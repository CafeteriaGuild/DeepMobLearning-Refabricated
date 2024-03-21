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

package dev.nathanpb.dml.modular_armor.screen

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.BlockEntityMatterCondenser
import dev.nathanpb.dml.modular_armor.BlockMatterCondenser
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor
import dev.nathanpb.dml.screen.handler.registerScreenHandlerForBlockEntity
import dev.nathanpb.dml.screen.handler.widget.CyclingTextureIcon
import dev.nathanpb.dml.screen.handler.widget.WEnergyComponent
import dev.nathanpb.dml.screen.handler.widget.WInfoBubbleWidget
import dev.nathanpb.dml.screen.handler.widget.WInfoBubbleWidget.Companion.INFO_BUBBLE
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.RenderUtils.Companion.ALT_STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text

class MatterCondenserScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    ctx: ScreenHandlerContext
) : SyncedGuiDescription(
    INSTANCE,
    syncId, playerInventory,
    getBlockInventory(ctx),
    ArrayPropertyDelegate(2) // dummy delegate, required for WEnergyComponent's WBar
) {

    companion object {
        val INSTANCE = registerScreenHandlerForBlockEntity(
            BlockMatterCondenser.IDENTIFIER,
            ::MatterCondenserScreenHandler
        )
    }

    init {
        val blockPos = ctx.get { _, pos -> { pos }}.get().invoke()

        val root = WPlainPanel()
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL

        val armorSlot = WItemSlot(blockInventory, 0, 1, 1, true).apply {
            setInputFilter {
                it.item is ItemModularGlitchArmor
            }

            icon = CyclingTextureIcon(listOf(
                identifier("textures/gui/slot_background/helmet_slot_background.png"),
                identifier("textures/gui/slot_background/chestplate_slot_background.png"),
                identifier("textures/gui/slot_background/leggings_slot_background.png"),
                identifier("textures/gui/slot_background/boots_slot_background.png")
            ))
        }
        root.add(armorSlot, 4 * 18, (2 * 18) + 6)

        val armorSlotFrame = WSprite(identifier("textures/gui/slot_background/big_fancy_frame.png"))
        root.add(armorSlotFrame, (3 * 18) + 6, (2 * 18) - 6, 42, 42)


        val energyComponent = object : WEnergyComponent(0, 1, blockInventory, 1, 2, true) {

            override fun tick() {
                super.tick()

                val energyProperties = ArrayPropertyDelegate(2)
                val blockEntity = world.getBlockEntity(blockPos)
                if(blockEntity is BlockEntityMatterCondenser) {
                    energyProperties[0] = blockEntity.energyStorage.amount.toInt()
                    energyProperties[1] = blockEntity.energyCapacity.toInt()
                }
                energyBar.setProperties(energyProperties)
            }
        }

        root.add(energyComponent, 0, (1*18) - 6)

        val infoBubble = WInfoBubbleWidget(
            INFO_BUBBLE,
            listOf(
                Text.translatable(
                    "tooltip.${MOD_ID}.matter_condenser.1",
                    Text.translatable("tooltip.${MOD_ID}.matter_condenser.pristine_matter").also { it.style = STYLE }
                ).also { it.style = ALT_STYLE },
                Text.translatable("tooltip.${MOD_ID}.matter_condenser.2").also { it.style = ALT_STYLE },
                Text.translatable(
                    "tooltip.${MOD_ID}.matter_condenser.3",
                    Text.translatable("text.${MOD_ID}.pristine_energy").also { it.style = STYLE },
                    Text.translatable("text.${MOD_ID}.pristine_energy.short.2").also { it.style = STYLE }
                ).also { it.style = ALT_STYLE },
                Text.empty(),
                Text.translatable(
                    "tooltip.${MOD_ID}.matter_condenser.4",
                    Text.translatable("text.${MOD_ID}.pristine_energy").also { it.style = STYLE },
                ).also { it.style = ALT_STYLE },
                Text.translatable("tooltip.${MOD_ID}.matter_condenser.5").also { it.style = ALT_STYLE },
                Text.translatable(
                    "tooltip.${MOD_ID}.matter_condenser.6",
                    Text.translatable("tooltip.${MOD_ID}.glitch_armor.title").also { it.style = STYLE }
                ).also { it.style = ALT_STYLE },
                Text.translatable("tooltip.${MOD_ID}.matter_condenser.7").also { it.style = ALT_STYLE },
                Text.empty(),
                Text.translatable("tooltip.${MOD_ID}.matter_condenser.8").also { it.style = ALT_STYLE },
                Text.translatable("tooltip.${MOD_ID}.matter_condenser.9").also { it.style = ALT_STYLE },
            )
        )
        root.add(infoBubble, (8 * 18) + 10, 0, 8, 8)

        root.add(createPlayerInventoryPanel(), 0, (5 * 18) + 2)
        setTitleAlignment(HorizontalAlignment.CENTER)
        root.validate(this)
        (blockInventory as? SimpleInventory)?.addListener {
            sendContentUpdates()
        }
    }

    override fun addPainters() {
        rootPanel.backgroundPainter = RenderUtils.DEFAULT_BACKGROUND_PAINTER
    }

    override fun getTitleColor(): Int {
        return RenderUtils.TITLE_COLOR
    }

}