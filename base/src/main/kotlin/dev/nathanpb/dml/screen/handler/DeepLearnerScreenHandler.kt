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

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.DeepLearnerData
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.screen.handler.slot.WTooltippedItemSlot
import dev.nathanpb.dml.screen.handler.widget.WEntityShowcase
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.closestValue
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import kotlin.properties.Delegates


// I hope no one will ever need to read this code again
// ... I did

// :(

class DeepLearnerScreenHandler (
    syncId: Int,
    playerInventory: PlayerInventory,
    private val hand: Hand
): SyncedGuiDescription(
    HANDLER_DEEP_LEARNER,
    syncId,
    playerInventory,
    SimpleInventory(4),
    ArrayPropertyDelegate(1)
) {

    val stack: ItemStack
        get() = playerInventory.player.getStackInHand(hand)

    val data = DeepLearnerData(stack)

    private fun firstDataModelIndex() : Int {
        return data.inventory.indexOfFirst {
            it.item is ItemDataModel
        }.let {
            if (it == -1) 0 else it
        }
    }

    private fun lastDataModelIndex() : Int {
        return data.inventory.indexOfLast {
            it.item is ItemDataModel
        }.let {
            if (it == -1) 0 else it
        }
    }

    private fun nextForwardDataModelIndex() : Int {
        return if (currentSlot != lastDataModelIndex()) {
            data.inventory.mapIndexed { index, stack ->
                Pair(stack, index)
            }.indexOfFirst { (stack, index) ->
                stack.item is ItemDataModel && index > currentSlot
            }.let {
                if (it == -1) 0 else it
            }
        } else currentSlot
    }

    private fun nextReverseDataModelIndex() : Int {
        return if (currentSlot != firstDataModelIndex()) {
           data.inventory.mapIndexed { index, stack ->
                Pair(stack, index)
            }.indexOfLast { (stack, index) ->
                stack.item is ItemDataModel && index < currentSlot
            }.let {
                if (it == -1) 0 else it
            }
        } else currentSlot
    }


    private var currentSlot by Delegates.observable(firstDataModelIndex()) { _, _, _ ->
        update()
    }

    private val prevButton: WButton = WButton(LiteralText("<")).apply {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addTooltip(TooltipBuilder().add(TranslatableText("gui.$MOD_ID.previous")))
        }

        setOnClick {
            currentSlot = nextReverseDataModelIndex()
        }
    }

    private val nextButton: WButton = WButton(LiteralText(">")).apply {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addTooltip(TooltipBuilder().add(TranslatableText("gui.${MOD_ID}.next")))
        }

        setOnClick {
            currentSlot = nextForwardDataModelIndex()
        }
    }

    private val showcase = WEntityShowcase()
    private val dataAmountText = WText(LiteralText(""))
    private val dataTierText = WText(LiteralText(""))


    private fun update() {
        prevButton.isEnabled = currentSlot > firstDataModelIndex()
        nextButton.isEnabled = currentSlot < lastDataModelIndex()

        val currentDataModel: DataModelData? = run {
            if (data.inventory.size > currentSlot) {
                val stack = data.inventory[currentSlot]
                if (!stack.isEmpty) {
                    return@run stack.dataModel
                }
            }
            null
        }

        showcase.entityTypes = currentDataModel?.category?.tag?.values().orEmpty()
        if (currentDataModel == null) {
            dataAmountText.text = LiteralText("")
            dataTierText.text = LiteralText("")
        } else {
            dataAmountText.text = TranslatableText(
                "tooltip.${MOD_ID}.data_model.data_amount_simple",
                currentDataModel.dataAmount,
                currentDataModel.tier().nextTierOrCurrent().dataAmount
            )

            dataTierText.text = TranslatableText("tooltip.${MOD_ID}.data_model.tier", currentDataModel.tier().text)
        }
    }

    init {
        blockInventory.setStacks(data.inventory)

        val root = WGridPanel()
        root.insets = Insets.ROOT_PANEL
        setRootPanel(root)

        root.add(showcase, 0, 1, 2, 4)

        root.add(
            WTooltippedItemSlot.of(blockInventory, 0, 2, 2, TranslatableText("gui.${MOD_ID}.data_model_only")).apply {
                setFilter { stack ->
                    stack.item is ItemDataModel && stack.dataModel.category != null
                }

                addChangeListener { _, inventory, index, stack ->
                    if (stack.isEmpty && index == currentSlot) {
                        currentSlot = inventory.items().mapIndexedNotNull { slotIndex, itemStack ->
                            slotIndex.takeUnless { itemStack.isEmpty }
                        }.closestValue(currentSlot)
                    }
                }
            }, 7, 2
        )

        root.add(prevButton, 7, 4)
        root.add(nextButton, 8, 4)

        WGridPanel().apply {
            insets = Insets(4)
            add(dataAmountText, 0, 0, 4, 1)
            add(dataTierText, 0, 1, 4, 1)
            root.add(this, 2, 3, 4, 2)
        }

        (blockInventory as? SimpleInventory)?.addListener {
            data.inventory = blockInventory.items()
            sendContentUpdates()
            update()
        }

        root.add(this.createPlayerInventoryPanel(), 0, 5)
        root.validate(this)
        update()
    }

    override fun addPainters() {
        rootPanel.backgroundPainter = RenderUtils.BACKGROUND_PAINTER
    }

}