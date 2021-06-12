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

package dev.nathanpb.dml.gui.screen.handler

import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectRegistry
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.gui.screen.handler.widget.WModularEffectToggle
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.net.C2S_MODULAR_EFFECT_TOGGLE
import dev.nathanpb.dml.utils.takeOrNull
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class ModularArmorScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    private val hand: Hand
): SyncedGuiDescription(
    HANDLER_MODULAR_ARMOR,
    syncId,
    playerInventory,
    SimpleInventory(ModularArmorData(playerInventory.player.getStackInHand(hand)).dataModel?.stack ?: ItemStack.EMPTY),
    ArrayPropertyDelegate(1)
) {
    val stack: ItemStack
        get() = playerInventory.player.getStackInHand(hand)

    val data
        get() = ModularArmorData(stack)

    init {
        val root = WGridPanel()
        setRootPanel(root)

        var lastEffectsList: WListPanel<ModularEffect<*>, WModularEffectToggle>? = null
        fun updateEffectsList() {
            val disabledEffects = data.disabledEffects
            root.remove(lastEffectsList)
            lastEffectsList = WListPanel(getPossibleEffects(), {
                WModularEffectToggle().apply {
                    setOnToggle { flag ->
                        effect?.id?.let {
                            sendToggleUpdate(it, flag)
                        }
                    }
                }
            }) { effect, widget ->
                widget.effect = effect
                widget.toggle = effect.id !in disabledEffects
            }

            root.add(lastEffectsList, 1, 0, 8, 5)
        }

        val dataModelSlot = WItemSlot.of(blockInventory, 0).apply {
            setFilter {
                it.isEmpty || (
                        (it.item as? ItemDataModel)?.category != null
                                && data.tier().ordinal >= it.dataModel.tier().ordinal
                        )
            }
            addChangeListener { _, _, _, _ -> updateEffectsList()}
        }

        root.add(dataModelSlot, 0, 2)

        root.add(this.createPlayerInventoryPanel(), 0, 5)
        root.validate(this)

        (blockInventory as? SimpleInventory)?.addListener {
            val stack = blockInventory.getStack(0)
            data.dataModel = takeOrNull(stack.item is ItemDataModel, stack::dataModel)
            sendContentUpdates()
        }
    }

    private fun getPossibleEffects() : List<ModularEffect<*>> {
        val category = data.dataModel?.category ?: return emptyList()
        return ModularEffectRegistry.INSTANCE.allMatching(category, data.tier())
            .filter { it.isEnabled() }
    }

    private fun sendToggleUpdate(effectId: Identifier, flag: Boolean) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(
            C2S_MODULAR_EFFECT_TOGGLE,
            PacketByteBuf(Unpooled.buffer()).apply {
                writeIdentifier(effectId)
                writeBoolean(flag)
                writeInt(hand.ordinal)
            }
        )
    }
}
