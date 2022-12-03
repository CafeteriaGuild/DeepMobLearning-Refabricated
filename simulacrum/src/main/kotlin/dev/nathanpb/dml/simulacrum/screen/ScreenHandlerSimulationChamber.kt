package dev.nathanpb.dml.simulacrum.screen

import dev.nathanpb.dml.simulacrum.SCS_HANDLER_TYPE
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class ScreenHandlerSimulationChamber(syncId: Int, playerInventory: PlayerInventory?, packetByteBuf: PacketByteBuf) :
    ScreenHandler(SCS_HANDLER_TYPE, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory?, inventory: Inventory?, blockEntity: BlockEntitySimulationChamber) : this(syncId, playerInventory, PacketByteBufs.create().writeBlockPos(blockEntity.pos))

    private val inventory: Inventory?
    private val player: PlayerEntity
    private val blockEntity: BlockEntitySimulationChamber?
    var blockPos: BlockPos
    private val world: World
    var propertyDelegate: PropertyDelegate

    init {
        player = playerInventory!!.player
        world = player.world
        blockPos = packetByteBuf.readBlockPos()
        blockEntity = playerInventory.player.entityWorld.getBlockEntity(blockPos) as BlockEntitySimulationChamber?
        inventory = blockEntity
        propertyDelegate = blockEntity!!.propertyDelegate
        checkSize(inventory, 4)
        if (world.isClient) addProperties(propertyDelegate)
        addSlots()
        addInventorySlots()
    }

    val syncedEnergy: Int
        get() = propertyDelegate[0]

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory!!.canPlayerUse(player)
    }

    private fun addSlots() {
        addSlot(SlotSimulationChamber(inventory, 0, 9, 146))
        addSlot(SlotSimulationChamber(inventory, 1, 176, 7))
        addSlot(SlotSimulationChamber(inventory, 2, 196, 7))
        addSlot(SlotSimulationChamber(inventory, 3, 186, 27))
    }

    private fun addInventorySlots() {
        // Bind actionbar
        for (row in 0..8) {
            val slot = Slot(player.inventory, row, 36 + row * 18, 211)
            addSlot(slot)
        }

        // 3 Top rows, starting with the bottom one
        for (row in 0..2) {
            for (column in 0..8) {
                val x = 36 + column * 18
                val y = 153 + row * 18
                val index = column + row * 9 + 9
                val slot = Slot(player.inventory, index, x, y)
                addSlot(slot)
            }
        }
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()
        if (!world.isClient) {
            // Update the BE every tick while container is open
            blockEntity!!.updateState()
        }
    }

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        if (!world.isClient) {
            val slot = slots[index]
            if (slot != null && slot.hasStack()) {
                val originalStack = slot.stack
                newStack = originalStack.copy()
                if (index < inventory!!.size()) {
                    if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                        return ItemStack.EMPTY
                    }
                } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                    return ItemStack.EMPTY
                }
                if (originalStack.isEmpty) {
                    slot.stack = ItemStack.EMPTY
                } else {
                    slot.markDirty()
                }
            }
        }
        return newStack
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        if (!world.isClient) {
            super.onSlotClick(slotIndex, button, actionType, player)
        }
    }

}