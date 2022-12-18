package dev.nathanpb.dml.simulacrum

import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GROUP
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import dev.nathanpb.dml.simulacrum.block.chamber.BlockSimulationChamber
import dev.nathanpb.dml.simulacrum.item.registerItems
import dev.nathanpb.dml.simulacrum.screen.ScreenHandlerSimulationChamber
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.BlockItem
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import team.reborn.energy.api.EnergyStorage


val SIMULATION_CHAMBER: Block = BlockSimulationChamber()

val SIMULATION_CHAMBER_ENTITY: BlockEntityType<BlockEntitySimulationChamber> = Registry.register(
    Registry.BLOCK_ENTITY_TYPE, identifier("simulation_chamber_entity"), FabricBlockEntityTypeBuilder.create(
        { pos: BlockPos?, state: BlockState? ->
            BlockEntitySimulationChamber(pos, state)
        }, SIMULATION_CHAMBER).build())

val SCS_HANDLER_TYPE: ScreenHandlerType<ScreenHandlerSimulationChamber> = ScreenHandlerRegistry.registerExtended(identifier("simulation")) {
        syncId: Int, playerInventory: PlayerInventory, packetByteBuf: PacketByteBuf ->
    ScreenHandlerSimulationChamber(syncId, playerInventory, packetByteBuf)
}

var PRISTINE_CHANCE = hashMapOf(
    "BASIC" to config.pristineChance.basic,
    "ADVANCED" to config.pristineChance.advanced,
    "SUPERIOR" to config.pristineChance.superior,
    "SELF_AWARE" to config.pristineChance.self_aware
)

var ENERGY_COST = hashMapOf(
    "NETHER" to config.energyCost.nether,
    "SLIMY" to config.energyCost.slimy,
    "OVERWORLD" to config.energyCost.overworld,
    "ZOMBIE" to config.energyCost.zombie,
    "SKELETON" to config.energyCost.skeleton,
    "END" to config.energyCost.end,
    "GHOST" to config.energyCost.ghost,
    "ILLAGER" to config.energyCost.illager,
    "OCEAN" to config.energyCost.ocean
)

@Suppress("unused")
fun init() {
    Registry.register(Registry.BLOCK, identifier("simulation_chamber"), SIMULATION_CHAMBER)
    Registry.register(Registry.ITEM, identifier("simulation_chamber"), BlockItem(SIMULATION_CHAMBER, FabricItemSettings().group(ITEM_GROUP)))

    registerItems()

    EnergyStorage.SIDED.registerForBlockEntity(
        { blockEntity, _ -> blockEntity.energyStorage },
        SIMULATION_CHAMBER_ENTITY
    )
}