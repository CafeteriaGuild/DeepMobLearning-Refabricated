package dev.nathanpb.dml.simulacrum

import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.simulacrum.screen.ScreenSimulationChamber
import dev.nathanpb.dml.simulacrum.util.DataModelUtil
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

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
                    RenderUtils.getTextWithDefaultTextColor(
                        Text.translatable(
                            "tooltip.dml-refabricated.data_model.1"
                        ),
                        it
                    )
                        .append(
                            Text.translatable(
                            "tooltip.dml-refabricated.data_model.2",
                            DataModelUtil.getEnergyCost(item)
                        ).formatted(Formatting.WHITE))
                )
                lines.add(
                    RenderUtils.getTextWithDefaultTextColor(
                        Text.translatable("tooltip.dml-refabricated.data_model.3"),
                        it
                    )
                        .append(
                            Text.translatable(
                            "tooltip.dml-refabricated.data_model.4",
                            DataModelUtil.textType(item)
                        ).formatted(Formatting.WHITE))
                )
            }
        }
    })
}