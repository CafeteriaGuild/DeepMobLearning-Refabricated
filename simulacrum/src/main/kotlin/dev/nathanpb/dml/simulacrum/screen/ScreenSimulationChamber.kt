package dev.nathanpb.dml.simulacrum.screen

import com.mojang.blaze3d.systems.RenderSystem
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.simulacrum.PRISTINE_CHANCE
import dev.nathanpb.dml.simulacrum.block.chamber.BlockEntitySimulationChamber
import dev.nathanpb.dml.simulacrum.util.Animation
import dev.nathanpb.dml.simulacrum.util.DataModelUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

open class ScreenSimulationChamber(handler: ScreenHandlerSimulationChamber, inventory: PlayerInventory, title: Text): HandledScreen<ScreenHandlerSimulationChamber>(handler, inventory, title) {

    private val gui = identifier("textures/gui/simulation_chamber_base.png")
    private val defaultGui = identifier("textures/gui/default_gui.png")
    private var blockEntity: BlockEntitySimulationChamber
    private var maxEnergy = 0.0
    private var simulationText: HashMap<String, String> = HashMap<String, String>()
    private var simulationAnimations: HashMap<String, Animation> = HashMap<String, Animation>()
    private var currentDataModel = ItemStack.EMPTY
    private var renderer: TextRenderer = MinecraftClient.getInstance().textRenderer
    private var world: World? = null

    init {
        blockEntity = MinecraftClient.getInstance().world!!.getBlockEntity(handler.blockPos) as BlockEntitySimulationChamber
        maxEnergy = blockEntity.energyStorage.getCapacity().toDouble()
        world = blockEntity.world
        backgroundWidth = 232
        backgroundHeight = 230
    }


    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        val f = DecimalFormat("0.#")
        val x: Int = this.x + 8
        val spacing = 12
        val yStart: Int = y - 3

        //Main Chamber GUI
        RenderSystem.setShaderTexture(0, gui)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        drawTexture(matrices, x, y, 0, 0, 216, 141)
        drawTexture(matrices, x, y + 145, 0, 141, 18, 18)

        //Energy Bar Rendering
        val energyBarHeight: Int = (handler!!.syncedEnergy / (maxEnergy - 64) * 87).toInt().coerceAtLeast(0).coerceAtMost(87)
        val energyBarOffset: Int = 87 - energyBarHeight
        drawTexture(matrices, x + 203, (y + 48) + energyBarOffset, 25, 141, 7, energyBarHeight)


        val lines: Array<String>
        if (!blockEntity.hasDataModel()) {
            lines = arrayOf("text.dml-refabricated.simulation_chamber.insert_data_model.1", "text.dml-refabricated.simulation_chamber.insert_data_model.2")
            val a1 = getAnimation("pleaseInsert1")
            val a2 = getAnimation("pleaseInsert2")
            animateString(matrices, lines[0], a1, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 10, yStart + spacing * 2, 0xFFFFFF)
        } else if (DataModelUtil.getTier(blockEntity.dataModel) == DataModelTier.FAULTY) {
            lines = arrayOf("text.dml-refabricated.simulation_chamber.insuficiente_data.1", "text.dml-refabricated.simulation_chamber.insuficiente_data.2", "text.dml-refabricated.simulation_chamber.insuficiente_data.3")
            val insufData = getAnimation("insufData1")
            val insufData2 = getAnimation("insufData2")
            val insufData3 = getAnimation("insufData3")
            animateString(matrices, lines[0], insufData, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF)
            animateString(matrices, lines[1], insufData2, insufData, 1, false, x + 10, yStart + spacing * 2, 0xFFFFFF)
            animateString(matrices, lines[2], insufData3, insufData2, 1, false, x + 10, yStart + spacing * 3, 0xFFFFFF)
        } else {
            // Draw current data model data
            if (DataModelUtil.getTier(blockEntity.dataModel) == DataModelTier.SELF_AWARE) {
                drawTexture(matrices, x + 6, y + 48, 18, 141, 7, 87)
            } else {
                val collectedData = DataModelUtil.getTierCount(blockEntity.dataModel) -
                    (DataModelUtil.getTier(blockEntity.dataModel)?.dataAmount ?: 0)
                val tierRoof = DataModelUtil.getTierRoof(blockEntity.dataModel) -
                    (DataModelUtil.getTier(blockEntity.dataModel)?.dataAmount ?: 0)
                val experienceBarHeight = (collectedData.toFloat() / tierRoof * 87).toInt()
                val experienceBarOffset = 87 - experienceBarHeight
                drawTexture(matrices, x + 6, y + 48 + experienceBarOffset, 18, 141, 7, experienceBarHeight)
            }
            DrawableHelper.drawTextWithShadow(
                matrices, renderer, Text.translatable("tooltip.dml-refabricated.data_model.3").copy().append(
                    DataModelUtil.textTier(blockEntity.dataModel)
                ), x + 10, yStart + spacing, 0xFFFFFF
            )
            DrawableHelper.drawTextWithShadow(
                matrices, renderer, Text.translatable("text.dml-refabricated.simulation_chamber.iterations", f.format(
                    DataModelUtil.getSimulationCount(blockEntity.dataModel).toLong())
                ), x + 10, yStart + spacing * 2, 0xFFFFFF
            )
            DrawableHelper.drawTextWithShadow(
                matrices, renderer, Text.translatable("text.dml-refabricated.simulation_chamber.pristine_chance",
                        PRISTINE_CHANCE[DataModelUtil.getTier(blockEntity.dataModel).toString()]
                ).append("%"), x + 10, yStart + spacing * 3, 0xFFFFFF
            )
        }
        RenderSystem.setShaderTexture(0, defaultGui)
        drawTexture(matrices, x + 20, y + 145, 0, 0, 176, 90)


        drawConsoleText(matrices, x, y, spacing)
        if(blockEntity.isCrafting) updateSimulationText()
        if((!blockEntity.isCrafting && blockEntity.canStartSimulation()) || hasDataModelChanged() || blockEntity.percentDone == 100) {
            resetAnimations()
        }
    }

    private fun resetAnimations() {
        simulationAnimations = HashMap<String, Animation>()
        simulationText = HashMap<String, String>()
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        val x: Int = mouseX - this.x
        val y: Int = mouseY - this.y
        val f = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val tooltip: MutableList<Text> = ArrayList()
        if (y in 47..134) {
            if (x in 13..21) {
                // Tooltip for data model data bar
                if (blockEntity.hasDataModel()) {
                    if (DataModelUtil.getTier(blockEntity.dataModel) != DataModelTier.SELF_AWARE) {
                        val currentTierCount = DataModelUtil.getTierCount(
                            blockEntity.dataModel
                        ) - (DataModelUtil.getTier(
                            blockEntity.dataModel
                        )?.dataAmount ?: 0)
                        val currentTierRoof = DataModelUtil.getTierRoof(
                            blockEntity.dataModel
                        ) - (DataModelUtil.getTier(
                            blockEntity.dataModel
                        )?.dataAmount ?: 0)
                        tooltip.add(Text.translatable("text.dml-refabricated.simulation_chamber.data_collected", currentTierCount, currentTierRoof))
                    } else {
                        tooltip.add(Text.translatable("text.dml-refabricated.simulation_chamber.max_tier"))
                    }
                } else {
                    tooltip.add(Text.translatable("text.dml-refabricated.simulation_chamber.missing_data_model"))
                }
                renderTooltip(matrices, tooltip, x + 2, y + 2)
            } else if (x in 211..219) {
                // Tooltip for energy
                tooltip.add(Text.of(f.format(handler!!.syncedEnergy.toLong()) + "/" + f.format(maxEnergy) + " E"))
                renderTooltip(matrices, tooltip, x - 90, y - 16)
            }
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    private fun getAnimation(key: String): Animation? {
        if (!simulationAnimations.containsKey(key)) {
            simulationAnimations[key] = Animation()
        }
        return simulationAnimations[key]
    }

    private fun animateString(matrices: MatrixStack, string: String, anim: Animation?, precedingAnim: Animation?, delay: Int, loop: Boolean, x: Int, y: Int, color: Int) {
        if (precedingAnim != null) {
            if (precedingAnim.hasFinished()) {
                val result = anim!!.animate(string, delay, world!!.levelProperties.time, loop)
                DrawableHelper.drawStringWithShadow(matrices, renderer, result, x, y, color)
            } else {
                return
            }
        }
        val result = anim!!.animate(string, delay, world!!.levelProperties.time, loop)
        DrawableHelper.drawStringWithShadow(matrices, renderer, result, x, y, color)
    }

    private fun animate(string: String, anim: Animation?, precedingAnim: Animation?, delayInTicks: Int, loop: Boolean): String {
        return animate(string, anim, precedingAnim, delayInTicks, loop, -1)
    }

    private fun animate(string: String, anim: Animation?, precedingAnim: Animation?, delayInTicks: Int, loop: Boolean, intArg: Int): String {
        return if (precedingAnim != null) {
            if (precedingAnim.hasFinished()) {
                anim!!.animate(string, delayInTicks, world!!.levelProperties.time, loop, intArg)
            } else {
                ""
            }
        } else anim!!.animate(string, delayInTicks, world!!.levelProperties.time, loop, intArg)
    }

    private fun drawConsoleText(matrices: MatrixStack, x: Int, y: Int, spacing: Int) {
        val lines: Array<String>
        if (!blockEntity.hasDataModel() || DataModelUtil.getTier(blockEntity.dataModel) == DataModelTier.FAULTY) {
            animateString(matrices, "_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF)
        } else if (!blockEntity.hasPolymerClay() && !blockEntity.isCrafting) {
            lines = arrayOf("text.dml-refabricated.simulation_chamber.cant_begin", "text.dml-refabricated.simulation_chamber.missing_polymer", "_")
            val a1 = getAnimation("inputSlotEmpty1")
            val a2 = getAnimation("inputSlotEmpty2")
            val a3 = getAnimation("blinkingUnderline1")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (!hasEnergy() && !blockEntity.isCrafting) {
            lines = arrayOf("text.dml-refabricated.simulation_chamber.cant_begin", "text.dml-refabricated.simulation_chamber.missing_energy", "_")
            val a1 = getAnimation("lowEnergy1")
            val a2 = getAnimation("lowEnergy2")
            val a3 = getAnimation("blinkingUnderline2")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (blockEntity.outputIsFull() || blockEntity.pristineIsFull()) {
            lines = arrayOf("text.dml-refabricated.simulation_chamber.cant_begin", "text.dml-refabricated.simulation_chamber.output_full", "_")
            val a1 = getAnimation("outputSlotFilled1")
            val a2 = getAnimation("outputSlotFilled2")
            val a3 = getAnimation("blinkingUnderline3")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (blockEntity.isCrafting) {
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                blockEntity.percentDone.toString() + "%",
                x + 176,
                y + 123,
                0x62D8FF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                getSimulationText("simulationProgressLine1"),
                x + 21,
                y + 51,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                getSimulationText("simulationProgressLine1Version"),
                x + 124,
                y + 51,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                getSimulationText("simulationProgressLine2"),
                x + 21,
                y + 51 + spacing,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine3"), x + 21,
                y + 51 + spacing * 2, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine4"), x + 21,
                y + 51 + spacing * 3, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine5"), x + 21,
                y + 51 + spacing * 4, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine6"), x + 21,
                y + 51 + spacing * 5, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine6Result"), x + 140,
                y + 51 + spacing * 5, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("simulationProgressLine7"), x + 21,
                y + 51 + spacing * 6, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, getSimulationText("blinkingDots1"), x + 128,
                y + 51 + spacing * 6, 0xFFFFFF
            )
        } else {
            animateString(matrices, "_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF)
        }
    }

    private fun updateSimulationText() {
        val lines = arrayOf(
            "text.dml-refabricated.simulation_chamber.sim.1",
            "text.dml-refabricated.simulation_chamber.sim.2",
            "text.dml-refabricated.simulation_chamber.sim.3",
            "text.dml-refabricated.simulation_chamber.sim.4",
            "text.dml-refabricated.simulation_chamber.sim.5",
            "text.dml-refabricated.simulation_chamber.sim.6",
            "text.dml-refabricated.simulation_chamber.sim.7",
            if(blockEntity.byproductSuccess) "text.dml-refabricated.simulation_chamber.succeeded" else "text.dml-refabricated.simulation_chamber.fail",
            "text.dml-refabricated.simulation_chamber.sim.8",
            "..."
        )
        val resultPrefix = if(blockEntity.byproductSuccess) "§a" else "§c"
        val aLine1: Animation? = getAnimation("simulationProgressLine1")
        val aLine1Version: Animation? = getAnimation("simulationProgressLine1Version")
        val aLine2: Animation? = getAnimation("simulationProgressLine2")
        val aLine3: Animation? = getAnimation("simulationProgressLine3")
        val aLine4: Animation? = getAnimation("simulationProgressLine4")
        val aLine5: Animation? = getAnimation("simulationProgressLine5")
        val aLine6: Animation? = getAnimation("simulationProgressLine6")
        val aLine6Result: Animation? = getAnimation("simulationProgressLine6Result")
        val aLine7: Animation? = getAnimation("simulationProgressLine7")
        val aLine8: Animation? = getAnimation("blinkingDots1")
        simulationText["simulationProgressLine1"] = animate(lines[0], aLine1, null, 1, false)
        simulationText["simulationProgressLine1Version"] = "§6" + animate(lines[1], aLine1Version, aLine1, 1, false) + "§r"
        hasDataModelChanged() // resync data model from BE
        simulationText["simulationProgressLine2"] = animate(lines[2], aLine2, aLine1Version, 1, false, (DataModelUtil.getSimulationCount(currentDataModel) + 1))
        simulationText["simulationProgressLine3"] = animate(lines[3], aLine3, aLine2, 2, false)
        simulationText["simulationProgressLine4"] = animate(lines[4], aLine4, aLine3, 1, false)
        simulationText["simulationProgressLine5"] = animate(lines[5], aLine5, aLine4, 2, false)
        simulationText["simulationProgressLine6"] = animate(lines[6], aLine6, aLine5, 2, false)
        simulationText["simulationProgressLine6Result"] =
            resultPrefix + animate(lines[7], aLine6Result, aLine6, 2, false) + "§r"
        simulationText["simulationProgressLine7"] = animate(lines[8], aLine7, aLine6Result, 1, false)
        simulationText["blinkingDots1"] = animate(lines[9], aLine8, aLine7, 8, true)
    }

    private fun getSimulationText(key: String): String? {
        if (!simulationText.containsKey(key)) {
            simulationText[key] = ""
        }
        return simulationText[key]
    }

    private fun hasEnergy(): Boolean {
        return blockEntity.hasEnergyForSimulation()
    }

    private fun hasDataModelChanged(): Boolean {
        if(ItemStack.areEqual(currentDataModel, blockEntity.dataModel)) return false

        currentDataModel = blockEntity.dataModel
        return true
    }
}