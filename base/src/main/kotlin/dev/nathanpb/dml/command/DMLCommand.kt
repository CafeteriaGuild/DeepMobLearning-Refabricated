package dev.nathanpb.dml.command

import com.mojang.brigadier.CommandDispatcher
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
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
                val metadata by lazy {
                    FabricLoader.getInstance().getModContainer("dml-refabricated").get().metadata
                }
                val authors by lazy {
                    metadata.authors.filter { person -> person.name != "IterationFunk" }.joinToString {
                            person -> person.name
                    }
                }
                val contributors by lazy {
                    metadata.contributors.joinToString {
                            person -> person.name
                    }
                }

                it.source.sendMessage(coloredText("-=  ", true).append(coloredText("${metadata.name} v${metadata.version}")).append(coloredText("  =-", true)))
                it.source.sendMessage(coloredText("Original mod by ").append(coloredText("IterationFunk", true)))
                it.source.sendMessage(coloredText("Reimagining/port by ").append(coloredText(authors, true)))
                it.source.sendMessage(coloredText("With contributions of ").append(coloredText(contributors, true)))
                if(it.source.isExecutedByPlayer) {
                    it.source.sendMessage(Text.empty())
                    it.source.sendMessage(
                        hyperlink("CurseForge", "https://curseforge.com/minecraft/mc-mods/deep-mob-learning-refabricated", 0xF46434).append(
                        " ").append(
                        hyperlink("Modrinth", "https://modrinth.com/mod/deep-mob-learning-refabricated", 0x1CD368)).append(
                        " ").append(
                        hyperlink("Issue Tracker", "https://github.com/CafeteriaGuild/DeepMobLearning-Refabricated/issues", 0x6E5494))
                    )
                }


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

    private fun coloredText(message: String, altStyle: Boolean): MutableText {
        return Text.literal(message).styled { if(altStyle) RenderUtils.ALT_STYLE else RenderUtils.STYLE }
    }

    private fun hyperlink(title: String, url: String, color: Int): MutableText {
        return Text.literal("[$title]").styled { Style.EMPTY
                .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url))
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to open link").styled { Style.EMPTY.withColor(color) }))
                .withColor(color)
                .withBold(true)
        }
    }
}
