package dev.nathanpb.dml.simulacrum

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GLITCH_SWORD
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import dev.nathanpb.dml.simulacrum.block.chamber.BlockSimulationChamber
import dev.nathanpb.dml.simulacrum.item.registerItems
import dev.nathanpb.dml.simulacrum.screen.ScreenHandlerSimulationChamber
import dev.nathanpb.dml.utils.MODULAR_ARMOR_ID
import dev.nathanpb.dml.utils.getItemFromRegistry
import dev.nathanpb.dml.utils.initConfig
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

val simulacrumConfig: SimulacrumConfig = initConfig("simulacrum", SimulacrumConfig(), SimulacrumConfig::class.java)

var PRISTINE_CHANCE = hashMapOf(
    "BASIC" to simulacrumConfig.simulationChamber.basicTierPristineChance,
    "ADVANCED" to simulacrumConfig.simulationChamber.advancedTierPristineChance,
    "SUPERIOR" to simulacrumConfig.simulationChamber.superiorTierPristineChance,
    "SELF_AWARE" to simulacrumConfig.simulationChamber.selfAwareTierPristineChance
)

var ENERGY_COST = hashMapOf(
    "NETHER" to simulacrumConfig.simulationChamber.netherEnergyCost,
    "SLIMY" to simulacrumConfig.simulationChamber.slimyEnergyCost,
    "OVERWORLD" to simulacrumConfig.simulationChamber.overworldEnergyCost,
    "ZOMBIE" to simulacrumConfig.simulationChamber.zombieEnergyCost,
    "SKELETON" to simulacrumConfig.simulationChamber.skeletonEnergyCost,
    "END" to simulacrumConfig.simulationChamber.endEnergyCost,
    "GHOST" to simulacrumConfig.simulationChamber.ghostEnergyCost,
    "ILLAGER" to simulacrumConfig.simulationChamber.illagerEnergyCost,
    "OCEAN" to simulacrumConfig.simulationChamber.oceanEnergyCost
)

@Suppress("unused")
fun init() {
    Registry.register(Registries.BLOCK, identifier("simulation_chamber"), SIMULATION_CHAMBER)
    Registry.register(Registries.ITEM, identifier("simulation_chamber"), BlockItem(SIMULATION_CHAMBER, FabricItemSettings()))

    ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
        if(isModLoaded(MODULAR_ARMOR_ID)) {
            it.addAfter(ItemStack(getItemFromRegistry("energy_cube")), SIMULATION_CHAMBER)
        } else {
            it.addAfter(ItemStack(ITEM_GLITCH_SWORD), SIMULATION_CHAMBER)
        }
    }

    registerItems()

    EnergyStorage.SIDED.registerForBlockEntity(
        { blockEntity, _ -> blockEntity.energyStorage },
        SIMULATION_CHAMBER_ENTITY
    )
}