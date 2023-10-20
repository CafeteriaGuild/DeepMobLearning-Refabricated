package dev.nathanpb.dml.simulacrum.block.chamber

import dev.nathanpb.dml.item.ITEM_POLYMER_CLAY
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.simulacrum.PRISTINE_CHANCE
import dev.nathanpb.dml.simulacrum.SIMULATION_CHAMBER_ENTITY
import dev.nathanpb.dml.simulacrum.item.ItemMatter
import dev.nathanpb.dml.simulacrum.screen.ScreenHandlerSimulationChamber
import dev.nathanpb.dml.simulacrum.util.DataModelUtil
import dev.nathanpb.dml.simulacrum.util.DataModelUtil.Companion.dataModel2MatterMap
import dev.nathanpb.dml.simulacrum.util.ImplementedInventory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerChunkManager
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyStorage
import java.util.*


class BlockEntitySimulationChamber(pos: BlockPos?, state: BlockState?) : BlockEntity(SIMULATION_CHAMBER_ENTITY, pos, state), ImplementedInventory, ExtendedScreenHandlerFactory, SidedInventory {

    private val inventory = DefaultedList.ofSize(4, ItemStack.EMPTY)
    var ticks = 0
    var percentDone = 0
    var isCrafting = false
        private set
    var byproductSuccess = false
    private var currentDataModelType = ""
    var energyStorage = SimpleEnergyStorage(2000000, 25600, 0)

    var propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return energyStorage.getAmount().toInt()
        }

        override fun set(index: Int, value: Int) {
            energyStorage.amount = value.toLong()
        }

        override fun size(): Int {
            return 1
        }
    }

    fun updateState() {
        val state = world!!.getBlockState(getPos())
        world!!.updateListeners(getPos(), state, state, 3)
    }

    fun dataModelTypeChanged(): Boolean {
        return currentDataModelType != DataModelUtil.getEntityCategory(dataModel).toString()
    }

    override fun readNbt(compound: NbtCompound) {
        super.readNbt(compound)
        energyStorage.amount = compound.getLong("energy")
        byproductSuccess = compound.getBoolean("byproductSuccess")
        isCrafting = compound.getBoolean("isCrafting")
        percentDone = compound.getInt("percentDone")
        currentDataModelType = compound.getString("currentDataModelType")
        Inventories.readNbt(compound, inventory)
    }

    public override fun writeNbt(compound: NbtCompound) {
        super.writeNbt(compound)
        compound.putLong("energy", energyStorage.amount)
        compound.putBoolean("byproductSuccess", byproductSuccess)
        compound.putBoolean("isCrafting", isCrafting)
        compound.putInt("percentDone", percentDone)
        compound.putString("currentDataModelType", currentDataModelType)
        Inventories.writeNbt(compound, inventory)
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
        super<ImplementedInventory>.markDirty()
        if (!world!!.isClient) {
            (world!!.chunkManager as ServerChunkManager).markForUpdate(getPos())
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = NbtCompound()
        writeNbt(nbt)
        return nbt
    }

    private fun startSimulation() {
        isCrafting = true
        currentDataModelType = DataModelUtil.getEntityCategory(dataModel).toString()
        inventory[1].count = polymerClay.count - 1
    }

    private fun finishSimulation(abort: Boolean) {
        percentDone = 0
        isCrafting = false
        // Only decrease input and increase output if not aborted, and only if on the server's BE
        if (!abort && !world!!.isClient) {
            DataModelUtil.updateSimulationCount(dataModel)
            DataModelUtil.updateTierCount(dataModel)
            if (inventory[2].item is ItemMatter) {
                inventory[2].count = living.count + 1
            } else {
                inventory[2] = ItemStack(dataModel2MatterMap[currentDataModelType]?.type?.matter, 1)
            }
            if (byproductSuccess) {
                byproductSuccess = false
                if (inventory[3].item is ItemPristineMatter) {
                    inventory[3].increment(1)
                } else {
                    inventory[3] = ItemStack(dataModel2MatterMap[currentDataModelType]?.pristine, 1)
                }
            }
            updateState()
        }
    }

    fun canStartSimulation(): Boolean {
        return hasEnergyForSimulation() && canContinueSimulation() && !outputIsFull() && !pristineIsFull() && hasPolymerClay()
    }

    fun canContinueSimulation(): Boolean {
        return hasDataModel() && !DataModelUtil.getTier(dataModel).toString().equals("faulty", ignoreCase = true)
    }

    fun hasEnergyForSimulation(): Boolean {
        return if (hasDataModel()) {
            val ticksPerSimulation = 300
            energyStorage.amount > ticksPerSimulation.toLong() * DataModelUtil.getEnergyCost(dataModel)
        } else {
            false
        }
    }



    val dataModel: ItemStack
        get() = getStack(0)
    private val polymerClay: ItemStack
        get() = getStack(1)
    private val living: ItemStack
        get() = getStack(2)
    private val pristine: ItemStack
        get() = getStack(3)

    fun hasDataModel(): Boolean {
        return dataModel.item is ItemDataModel
    }

    fun hasPolymerClay(): Boolean {
        val stack = polymerClay
        return stack.isOf(ITEM_POLYMER_CLAY) && stack.count > 0
    }

    fun outputIsFull(): Boolean {
        val stack = living
        if (stack.isEmpty) {
            return false
        }
        val stackLimitReached = stack.count == living.maxCount
        val outputMatches = dataModelMatchesOutput(
            dataModel,
            living
        )
        return stackLimitReached || !outputMatches
    }

    fun pristineIsFull(): Boolean {
        val stack = pristine
        if (stack.isEmpty) {
            return false
        }
        val stackLimitReached = stack.count == inventory[3].maxCount
        val outputMatches = dataModelMatchesPristine(
            dataModel,
            pristine
        )
        return stackLimitReached || !outputMatches
    }

    override fun getDisplayName(): Text {
        return Text.translatable("block.dml-refabricated.simulation_chamber")
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ScreenHandlerSimulationChamber(syncId, inv, this, this)
    }

    override fun getItems(): DefaultedList<ItemStack?> {
        return inventory
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(getPos())
    }

    override fun getAvailableSlots(side: Direction): IntArray {
        return if (side == Direction.UP) {
            intArrayOf(0, 1)
        } else intArrayOf(2, 3)
    }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return if (dir == Direction.UP) {
            when (slot) {
                0 -> stack.item is ItemDataModel && DataModelUtil.getEntityCategory(stack) != null
                1 -> stack.isOf(ITEM_POLYMER_CLAY)
                else -> false
            }
        } else false
    }

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction): Boolean {
        return if (dir != Direction.UP) {
            when (slot) {
                2, 3 -> true
                else -> false
            }
        } else false
    }

    companion object {
        private fun dataModelMatchesOutput(stack: ItemStack, output: ItemStack): Boolean {
            val livingMatter: Item? =
                dataModel2MatterMap[DataModelUtil.getEntityCategory(stack).toString()]?.type?.matter
            return Registries.ITEM.getId(livingMatter) == Registries.ITEM.getId(output.item)
        }

        private fun dataModelMatchesPristine(stack: ItemStack, pristine: ItemStack): Boolean {
            val pristineMatter: ItemPristineMatter? = dataModel2MatterMap[DataModelUtil.getEntityCategory(stack).toString()]?.pristine
            return Registries.ITEM.getId(pristineMatter) == Registries.ITEM.getId(pristine.item)
        }

        fun tick(world: World, pos: BlockPos?, state: BlockState?, blockEntity: BlockEntitySimulationChamber) {
            blockEntity.ticks++
            if (!world.isClient) {
                if (!blockEntity.isCrafting) {
                    if (blockEntity.canStartSimulation()) {
                        blockEntity.startSimulation()
                    }
                } else {
                    if (!blockEntity.canContinueSimulation() || blockEntity.dataModelTypeChanged()) {
                        blockEntity.finishSimulation(true)
                        return
                    }
                    if (blockEntity.percentDone == 0) {
                        val rand = Random()
                        val num = rand.nextInt(100)
                        val chance: Int? = PRISTINE_CHANCE[(DataModelUtil.getTier(blockEntity.dataModel).toString())]
                        blockEntity.byproductSuccess = num <= ((chance)?.coerceAtLeast(1)?.coerceAtMost(100) ?: 0)
                    }
                    val energyTickCost = DataModelUtil.getEnergyCost(blockEntity.dataModel)
                    blockEntity.energyStorage.amount -= energyTickCost
                    if (blockEntity.ticks % (20 * 15 / 100) == 0) {
                        blockEntity.percentDone++
                    }

                    // Notify while crafting every other second, this is done more frequently when the container is open
                    if (blockEntity.ticks % (20 * 2) == 0) {
                        blockEntity.updateState()
                    }
                }
                if (blockEntity.percentDone == 100) {
                    blockEntity.finishSimulation(false)
                    return
                }
                blockEntity.markDirty()
            }
        }
    }
}