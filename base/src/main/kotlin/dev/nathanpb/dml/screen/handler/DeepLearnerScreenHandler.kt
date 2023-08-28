/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/ or modify
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
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.screen.handler.widget.WEntityShowcase
import dev.nathanpb.dml.screen.handler.widget.WStylizedButton
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.closestValue
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.Icon
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.text.Text
import net.minecraft.util.Formatting
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

    private fun nextDataModelIndex() : Int {
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

    private fun previousDataModelIndex() : Int {
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

    private val prevButton: WButton = WStylizedButton(Text.of("<"), RenderUtils.DML_WIDGETS).apply {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addTooltip(TooltipBuilder().add(Text.translatable("gui.$MOD_ID.previous")))
        }

        setOnClick {
            currentSlot = previousDataModelIndex()
        }
    }

    private val nextButton: WButton = WStylizedButton(Text.of(">"), RenderUtils.DML_WIDGETS).apply {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addTooltip(TooltipBuilder().add(Text.translatable("gui.${MOD_ID}.next")))
        }

        setOnClick {
            currentSlot = nextDataModelIndex()
        }
    }

    private val showcaseBackground = WSprite(identifier("textures/gui/entity_showcase_background.png"))
    private val showcase = WEntityShowcase(this)

    private val entityName = WText(Text.of(""))
    private val entityHealth = WText(Text.of(""))
    private val dataAmount = WLabel(Text.of(""))
    private val dataTier = WLabel(Text.of(""))



    private fun update() {
        prevButton.isEnabled = currentSlot > firstDataModelIndex()
        nextButton.isEnabled = currentSlot < lastDataModelIndex()

        updateEntityInformation()

        val currentDataModel: DataModelData? = run {
            if (data.inventory.size > currentSlot) {
                val stack = data.inventory[currentSlot]
                if (!stack.isEmpty) {
                    return@run stack.dataModel
                }
            }
            null
        }

        val showcaseEntities: MutableList<EntityType<*>> = arrayListOf()
        Registries.ENTITY_TYPE.iterateEntries(currentDataModel?.category?.tagKey).forEach {
            showcaseEntities.add(it.value())
        }

        showcase.entityTypes = showcaseEntities
        if (currentDataModel == null) {
            dataAmount.text = Text.of("")
            dataTier.text = Text.of("")
        } else if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            dataAmount.text =
                Text.translatable("tooltip.${MOD_ID}.data_amount.1").setStyle(RenderUtils.STYLE).append(
                Text.translatable("tooltip.${MOD_ID}.data_amount.2", currentDataModel.dataAmount, currentDataModel.tier().nextTierOrCurrent().dataAmount).setStyle(RenderUtils.ALT_STYLE))

            dataTier.text =
                Text.translatable("tooltip.${MOD_ID}.tier.1").setStyle(RenderUtils.STYLE).append(
                Text.translatable("tooltip.${MOD_ID}.tier.2", currentDataModel.tier().text))
        }
    }

    fun updateEntityInformation() {
        if(showcase.entityType != null && FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            entityName.text =
                Text.translatable("tooltip.${MOD_ID}.deep_learner.entityName.1").setStyle(RenderUtils.STYLE).append(
                Text.translatable("tooltip.${MOD_ID}.deep_learner.entityName.2", showcase.entityType!!.name).formatted(Formatting.WHITE))

            entityHealth.text =
                Text.translatable("tooltip.${MOD_ID}.deep_learner.entityHealth.1").setStyle(RenderUtils.STYLE).append(
                Text.of("❤").copy().formatted(Formatting.RED)).append(
                Text.translatable("tooltip.${MOD_ID}.deep_learner.entityHealth.2", (showcase.entityType!!.create(world) as LivingEntity).maxHealth).formatted(Formatting.WHITE))
        }
    }

    init {
        blockInventory.setStacks(data.inventory)

        val root = WPlainPanel()
        root.insets = Insets.ROOT_PANEL
        setRootPanel(root)

        root.add(showcaseBackground, 0, 1*18, 3*18, 4*18)
        root.add(showcase, 0, 1*18, 3*18, 4*18)

        root.add(
            DataModelSlots(blockInventory).apply {
                addChangeListener { _, inventory, index, stack ->
                    if (stack.isEmpty && index == currentSlot) {
                        currentSlot = inventory.items().mapIndexedNotNull { slotIndex, itemStack ->
                            slotIndex.takeUnless { itemStack.isEmpty }
                        }.closestValue(currentSlot)
                    }
                }

            }, 7*18, 1*18
        )

        root.add(prevButton, 7*18, 3*18, 18, 20)
        root.add(nextButton, 8*18, 3*18, 18, 20)

        WPlainPanel().apply {
            insets = Insets(4)
            add(entityName, 1*18, -2*18-3, 4*18,1)
            add(entityHealth, 1*18, -1*18+6, 2*18,1)
            add(dataAmount, 1*18, 1*18-3, 1, 1)
            add(dataTier, 1*18, 1*18+6, 1, 1)
            root.add(this, 2*18, 3*18, 1, 1)
        }

        (blockInventory as? SimpleInventory)?.addListener {
            data.inventory = blockInventory.items()
            sendContentUpdates()
            update()
        }

        root.add(createPlayerInventoryPanel(), 0, 5*18+6)
        root.validate(this)
        update()
    }


    override fun addPainters() {
        rootPanel.backgroundPainter = RenderUtils.DEFAULT_BACKGROUND_PAINTER
    }

    override fun getTitleColor(): Int {
        return RenderUtils.TITLE_COLOR
    }


    private class DataModelSlots(
        inventory: Inventory
    ): WItemSlot(inventory, 0, 2, 2, false) {

        init {
            setFilter { stack ->
                stack.item is ItemDataModel && stack.dataModel.category != null
            }

            icon = TextureIcon(identifier("textures/gui/slot_background/2x2_data_model_slot_background.png"))
        }

        // Hacky way to render a TextureIcon on a 2x2 slot, ignore the on-screen warnings
        override fun paint(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
            backgroundPainter?.paintBackground(context, x, y, this)

            icon?.paint(context, x + 1, y + 1, 34)
        }

    }
}