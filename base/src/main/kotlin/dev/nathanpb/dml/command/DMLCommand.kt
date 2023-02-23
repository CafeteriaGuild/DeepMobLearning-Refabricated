package dev.nathanpb.dml.command

import com.mojang.brigadier.CommandDispatcher
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting


class DMLCommand: CommandRegistrationCallback {

    override fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>?,
        registryAccess: CommandRegistryAccess?,
        environment: RegistrationEnvironment?
    ) {
        dispatcher!!.register(literal("dml-refabricated")
            // Modules
            .then(literal("modules")
                .executes {
                    it.source.sendMessage(appendModuleText("dml-refabricated-base", "Base"))
                    it.source.sendMessage(appendModuleText("dml-refabricated-modular-armor", "Glitch Armor"))
                    it.source.sendMessage(appendModuleText("dmlsimulacrum", "Simulacrum"))
                    return@executes 1
                }
            )

            // Main Command
            .executes {
                it.source.sendMessage(Text.literal("Hello, Brigadier!"))

                return@executes 1
            })
    }


    private fun appendModuleText(modId: String, fancyName: String): Text {
        val isLoaded = FabricLoader.getInstance().isModLoaded(modId)
        val version: String = FabricLoader.getInstance().getModContainer(modId).orElse(null).let {
            if(it != null) "v${it.metadata.version}" else ""
        }

        return coloredText("- [")
            .append(Text.literal(if(isLoaded) "✔" else "✕").formatted(if(isLoaded) Formatting.GREEN else Formatting.RED))
            .append(coloredText("] "))
            .append(coloredText("$fancyName $version"))
    }

    private fun coloredText(message: String): MutableText {
        return coloredText(message, false)
    }

    private fun coloredText(message: String, translatable: Boolean): MutableText {
        return (if(translatable) Text.translatable(message) else Text.literal(message)).styled { RenderUtils.STYLE }
    }
}
