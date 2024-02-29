package dev.nathanpb.dml.simulacrum.screen

import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ITEM_POLYMER_CLAY
import dev.nathanpb.dml.item.ItemDataModel
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class SlotSimulationChamber(inventory: Inventory?, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {

    override fun canInsert(stack: ItemStack): Boolean {
        return when(index) {
            0 -> {
                !stack.isEmpty &&
                stack.item is ItemDataModel &&
                stack.dataModel.category != null
            }
            1 -> {
                !stack.isEmpty &&
                stack.isOf(ITEM_POLYMER_CLAY)
            }
            else -> false
        }
    }

}