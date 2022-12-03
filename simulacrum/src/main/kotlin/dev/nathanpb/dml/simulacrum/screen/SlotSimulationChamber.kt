package dev.nathanpb.dml.simulacrum.screen

import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.simulacrum.item.POLYMER_CLAY
import dev.nathanpb.dml.simulacrum.util.DataModelUtil
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class SlotSimulationChamber(inventory: Inventory?, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {

    override fun canInsert(stack: ItemStack): Boolean {
        return when(index) {
            0 -> {
                !stack.isEmpty &&
                stack.item is ItemDataModel &&
                DataModelUtil.getEntityCategory(stack) != null
            }
            1 -> {
                !stack.isEmpty &&
                stack.isOf(POLYMER_CLAY)
            }
            else -> false
        }
    }

}