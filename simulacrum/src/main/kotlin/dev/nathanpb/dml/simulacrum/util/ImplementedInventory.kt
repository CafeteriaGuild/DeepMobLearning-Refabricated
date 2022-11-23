package dev.nathanpb.dml.simulacrum.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

interface ImplementedInventory: Inventory {

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    fun getItems(): DefaultedList<ItemStack?>

    /**
     * Creates an inventory from the item list.
     */
    fun of(items: DefaultedList<ItemStack?>): ImplementedInventory? {
        return object : ImplementedInventory {
            override fun getItems(): DefaultedList<ItemStack?> {
                return items
            }
        }
    }

    /**
     * Creates a new inventory with the specified size.
     */
    fun ofSize(size: Int): ImplementedInventory? {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY))
    }

    /**
     * Returns the inventory size.
     */
    override fun size(): Int {
        return getItems().size
    }

    /**
     * Checks if the inventory is empty.
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    override fun isEmpty(): Boolean {
        for (i in 0 until size()) {
            val stack = getStack(i)
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    /**
     * Retrieves the item in the slot.
     */
    override fun getStack(slot: Int): ItemStack {
        return getItems()[slot]
    }

    /**
     * Removes items from an inventory slot.
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are fewer items in the slot than what are requested,
     * takes all items in that slot.
     */
    override fun removeStack(slot: Int, count: Int): ItemStack? {
        val result = Inventories.splitStack(getItems(), slot, count)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    /**
     * Removes all items from an inventory slot.
     * @param slot The slot to remove from.
     */
    override fun removeStack(slot: Int): ItemStack? {
        return Inventories.removeStack(getItems(), slot)
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     * this inventory ([Inventory.getMaxCountPerStack]),
     * it gets resized to this inventory's maximum amount.
     */
    override fun setStack(slot: Int, stack: ItemStack) {
        getItems()[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    /**
     * Clears the inventory.
     */
    override fun clear() {
        getItems().clear()
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    override fun markDirty() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return true
    }
}