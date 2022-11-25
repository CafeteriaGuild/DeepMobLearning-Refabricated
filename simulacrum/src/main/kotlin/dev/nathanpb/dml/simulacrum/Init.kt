package dev.nathanpb.dml.simulacrum

import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GROUP
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import dev.nathanpb.dml.simulacrum.block.chamber.BlockSimulationChamber
import dev.nathanpb.dml.simulacrum.item.registerItems
import dev.nathanpb.dml.simulacrum.screen.ScreenHandlerSimulationChamber.Companion.SCS_HANDLER_TYPE
import dev.nathanpb.dml.simulacrum.screen.ScreenSimulationChamber
import dev.nathanpb.dml.simulacrum.util.DataModelUtil
import dev.nathanpb.dml.utils.RenderUtils.Companion.getTextWithDefaultTextColor
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import team.reborn.energy.api.EnergyStorage


val SIMULATION_CHAMBER: Block = BlockSimulationChamber()

var SIMULATION_CHAMBER_ENTITY: BlockEntityType<BlockEntitySimulationChamber> = Registry.register(
    Registry.BLOCK_ENTITY_TYPE, identifier("simulation_chamber_entity"), FabricBlockEntityTypeBuilder.create(
        { pos: BlockPos?, state: BlockState? ->
            BlockEntitySimulationChamber(pos, state)
        }, SIMULATION_CHAMBER).build())

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

@Suppress("unused")
fun initClient() {
    HandledScreens.register(SCS_HANDLER_TYPE) { handler, inventory, title ->
        ScreenSimulationChamber(handler, inventory, title)
    }


    ItemTooltipCallback.EVENT.register(ItemTooltipCallback { item: ItemStack, _: TooltipContext?, lines: MutableList<Text?> ->
        val world: World? = MinecraftClient.getInstance().world
        if (item.item is ItemDataModel && DataModelUtil.getEntityCategory(item) != null) {
            world?.let {
                lines.add(
                    getTextWithDefaultTextColor(Text.translatable(
                        "tooltip.dml-refabricated.data_model.1"),
                        it
                    )
                    .append(Text.translatable(
                        "tooltip.dml-refabricated.data_model.2",
                        DataModelUtil.getEnergyCost(item)
                    ).formatted(Formatting.WHITE))
                )
                lines.add(
                    getTextWithDefaultTextColor(
                        Text.translatable("tooltip.dml-refabricated.data_model.3"),
                        it
                    )
                    .append(Text.translatable(
                        "tooltip.dml-refabricated.data_model.4",
                        DataModelUtil.textType(item)
                    ).formatted(Formatting.WHITE))
                )
            }
        }
    })
}