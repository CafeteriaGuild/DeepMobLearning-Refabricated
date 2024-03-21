package dev.nathanpb.dml

import dev.nathanpb.dml.blockEntity.BLOCKENTITY_DATA_SYNTHESIZER
import dev.nathanpb.dml.blockEntity.BLOCKENTITY_DISRUPTIONS_CORE
import dev.nathanpb.dml.blockEntity.renderer.BlockEntityRendererDataSynthesizer
import dev.nathanpb.dml.blockEntity.renderer.BlockEntityRendererDisruptionsCore
import dev.nathanpb.dml.entity.registerEntityRenderer
import dev.nathanpb.dml.item.ITEM_ENERGY_OCTAHEDRON
import dev.nathanpb.dml.screen.registerScreens
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

@Suppress("unused")
fun initClient() {
    registerScreens()
    registerEntityRenderer()

    BlockEntityRendererFactories.register(
        BLOCKENTITY_DATA_SYNTHESIZER,
        ::BlockEntityRendererDataSynthesizer
    )
    BlockEntityRendererFactories.register(
        BLOCKENTITY_DISRUPTIONS_CORE,
        ::BlockEntityRendererDisruptionsCore
    )

    ModelLoadingPlugin.register{ ctx ->
        ctx.addModels(
            identifier("block/data_synthesizer_grid")
        )
    }

    ITEM_ENERGY_OCTAHEDRON.let {
        ColorProviderRegistry.ITEM.register({ stack, _ -> it.getScaledColor(stack) }, it)
    }
}