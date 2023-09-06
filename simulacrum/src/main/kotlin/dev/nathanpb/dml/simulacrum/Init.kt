package dev.nathanpb.dml.simulacrum

import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import dev.nathanpb.dml.simulacrum.block.chamber.BlockSimulationChamber
import dev.nathanpb.dml.simulacrum.item.registerItems
import dev.nathanpb.dml.simulacrum.screen.ScreenHandlerSimulationChamber
import dev.nathanpb.dml.utils.MODULAR_ARMOR_ID
import dev.nathanpb.dml.utils.isModLoaded
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.BlockPos
import team.reborn.energy.api.EnergyStorage


val SIMULATION_CHAMBER: Block = BlockSimulationChamber()

val SIMULATION_CHAMBER_ENTITY: BlockEntityType<BlockEntitySimulationChamber> = Registry.register(
    Registries.BLOCK_ENTITY_TYPE, identifier("simulation_chamber_entity"), FabricBlockEntityTypeBuilder.create(
        { pos: BlockPos?, state: BlockState? ->
            BlockEntitySimulationChamber(pos, state)
        }, SIMULATION_CHAMBER).build())

val SCS_HANDLER_TYPE: ScreenHandlerType<ScreenHandlerSimulationChamber> = ScreenHandlerRegistry.registerExtended(identifier("simulation")) {
        syncId: Int, playerInventory: PlayerInventory, packetByteBuf: PacketByteBuf ->
    ScreenHandlerSimulationChamber(syncId, playerInventory, packetByteBuf)
}

var PRISTINE_CHANCE = hashMapOf(
    "BASIC" to config.simulationChamber.basicTierPristineChance,
    "ADVANCED" to config.simulationChamber.advancedTierPristineChance,
    "SUPERIOR" to config.simulationChamber.superiorTierPristineChance,
    "SELF_AWARE" to config.simulationChamber.selfAwareTierPristineChance
)

var ENERGY_COST = hashMapOf(
    "NETHER" to config.simulationChamber.netherEnergyCost,
    "SLIMY" to config.simulationChamber.slimyEnergyCost,
    "OVERWORLD" to config.simulationChamber.overworldEnergyCost,
    "ZOMBIE" to config.simulationChamber.zombieEnergyCost,
    "SKELETON" to config.simulationChamber.skeletonEnergyCost,
    "END" to config.simulationChamber.endEnergyCost,
    "GHOST" to config.simulationChamber.ghostEnergyCost,
    "ILLAGER" to config.simulationChamber.illagerEnergyCost,
    "OCEAN" to config.simulationChamber.oceanEnergyCost
)

@Suppress("unused")
fun init() {
    Registry.register(Registries.BLOCK, identifier("simulation_chamber"), SIMULATION_CHAMBER)
    Registry.register(Registries.ITEM, identifier("simulation_chamber"), BlockItem(SIMULATION_CHAMBER, FabricItemSettings()))

    ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
        if(isModLoaded(MODULAR_ARMOR_ID)) {
            it.addAfter(ItemStack(Registries.BLOCK.get(identifier("matter_condenser"))), SIMULATION_CHAMBER)
        } else {
            it.addAfter(ItemStack(BLOCK_LOOT_FABRICATOR), SIMULATION_CHAMBER)
        }
    }

    registerItems()

    EnergyStorage.SIDED.registerForBlockEntity(
        { blockEntity, _ -> blockEntity.energyStorage },
        SIMULATION_CHAMBER_ENTITY
    )
}