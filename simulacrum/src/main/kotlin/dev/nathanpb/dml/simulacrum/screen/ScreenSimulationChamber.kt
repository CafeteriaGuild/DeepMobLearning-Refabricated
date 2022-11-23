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

open class ScreenSimulationChamber(_handler: ScreenHandlerSimulationChamber, _inventory: PlayerInventory, _title: Text): HandledScreen<ScreenHandlerSimulationChamber>(_handler, _inventory, _title) {

    val GUI = identifier("textures/gui/simulation_chamber_base.png")
    val defaultGUI = identifier("textures/gui/default_gui.png")
    private val WIDTH = 232
    private val HEIGHT = 230
    private var maxEnergy = 0.0
    var blockEntity: BlockEntitySimulationChamber? = null
    private var animationList: HashMap<String, Animation>? = null
    private var currentDataModel = ItemStack.EMPTY
    private var renderer: TextRenderer? = null
    private var world: World? = null

    init {
        blockEntity = MinecraftClient.getInstance().world!!.getBlockEntity(_handler.blockPos) as BlockEntitySimulationChamber?
        maxEnergy = blockEntity!!.energyStorage.getCapacity().toDouble()
        animationList = HashMap()
        world = blockEntity!!.world
        renderer = MinecraftClient.getInstance().textRenderer
        backgroundWidth = WIDTH
        backgroundHeight = HEIGHT
    }


    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        val f = DecimalFormat("0.#")
        val x: Int = this.x + 8
        val spacing = 12
        val yStart: Int = y - 3
        if (dataModelChanged()) {
            resetAnimations()
        }

        //Main Chamber GUI
        RenderSystem.setShaderTexture(0, GUI)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        drawTexture(matrices, x, y, 0, 0, 216, 141)
        drawTexture(matrices, x, y + 145, 0, 141, 18, 18)

        //Energy Bar Rendering
        val energyBarHeight: Int = (handler!!.syncedEnergy / (maxEnergy - 64) * 87).toInt().coerceAtLeast(0).coerceAtMost(87)
        val energyBarOffset: Int = 87 - energyBarHeight
        drawTexture(matrices, x + 203, (y + 48) + energyBarOffset, 25, 141, 7, energyBarHeight)
        val lines: Array<String>
        if (!blockEntity!!.hasDataModel()) {
            lines = arrayOf("Please insert a data model", "to begin the simulation")
            val a1 = getAnimation("pleaseInsert1")
            val a2 = getAnimation("pleaseInsert2")
            animateString(matrices, lines[0], a1, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 10, yStart + spacing * 2, 0xFFFFFF)
        } else if (DataModelUtil.getTier(blockEntity!!.dataModel) == DataModelTier.FAULTY) {
            lines = arrayOf("Insufficient data in model", "please insert a basic model", "or better ")
            val insufData = getAnimation("insufData1")
            val insufData2 = getAnimation("insufData2")
            val insufData3 = getAnimation("insufData3")
            animateString(matrices, lines[0], insufData, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF)
            animateString(matrices, lines[1], insufData2, insufData, 1, false, x + 10, yStart + spacing * 2, 0xFFFFFF)
            animateString(matrices, lines[2], insufData3, insufData2, 1, false, x + 10, yStart + spacing * 3, 0xFFFFFF)
        } else {
            // Draw current data model data
            if (DataModelUtil.getTier(blockEntity!!.dataModel) == DataModelTier.SELF_AWARE) {
                drawTexture(matrices, x + 6, y + 48, 18, 141, 7, 87)
            } else {
                val collectedData = DataModelUtil.getTierCount(blockEntity!!.dataModel) -
                    (DataModelUtil.getTier(blockEntity!!.dataModel)?.dataAmount ?: 0)
                val tierRoof = DataModelUtil.getTierRoof(blockEntity!!.dataModel) -
                    (DataModelUtil.getTier(blockEntity!!.dataModel)?.dataAmount ?: 0)
                val experienceBarHeight = (collectedData.toFloat() / tierRoof * 87).toInt()
                val experienceBarOffset = 87 - experienceBarHeight
                drawTexture(matrices, x + 6, y + 48 + experienceBarOffset, 18, 141, 7, experienceBarHeight)
            }
            DrawableHelper.drawTextWithShadow(
                matrices, renderer, Text.of("Tier: ").copy().append(
                    DataModelUtil.textTier(blockEntity!!.dataModel)
                ), x + 10, yStart + spacing, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, "Iterations: " + f.format(
                    DataModelUtil.getSimulationCount(blockEntity!!.dataModel).toLong()
                ), x + 10, yStart + spacing * 2, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, "Pristine chance: " +
                        PRISTINE_CHANCE[DataModelUtil.getTier(blockEntity!!.dataModel).toString()]
                        + "%", x + 10, yStart + spacing * 3, 0xFFFFFF
            )
        }

        RenderSystem.setShaderTexture(0, defaultGUI)
        drawTexture(matrices, x + 20, y + 145, 0, 0, 176, 90)
        drawConsoleText(matrices, x, y, spacing)
    }

    private fun resetAnimations() {
        animationList = HashMap()
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        val x: Int = mouseX - this.x
        val y: Int = mouseY - this.y
        val f = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val tooltip: MutableList<Text> = ArrayList()
        if (y in 47..134) {
            if (x in 13..21) {
                // Tooltip for data model data bar
                if (blockEntity!!.hasDataModel()) {
                    if (DataModelUtil.getTier(blockEntity!!.dataModel) != DataModelTier.SELF_AWARE) {
                        val currentTierCount = DataModelUtil.getTierCount(
                            blockEntity!!.dataModel
                        ) - (DataModelUtil.getTier(
                            blockEntity!!.dataModel
                        )?.dataAmount ?: 0)
                        val currentTierRoof = DataModelUtil.getTierRoof(
                            blockEntity!!.dataModel
                        ) - (DataModelUtil.getTier(
                            blockEntity!!.dataModel
                        )?.dataAmount ?: 0)
                        tooltip.add(Text.of("$currentTierCount/$currentTierRoof Data collected"))
                    } else {
                        tooltip.add(Text.of("This data model has reached the max tier."))
                    }
                } else {
                    tooltip.add(Text.of("Machine is missing a data model"))
                }
                renderTooltip(matrices, tooltip, x + 2, y + 2)
            } else if (x in 211..219) {
                // Tooltip for energy
                tooltip.add(Text.of(f.format(handler!!.syncedEnergy.toLong()) + "/" + f.format(maxEnergy) + " E"))
                if (blockEntity!!.hasDataModel()) {
                    val data = DataModelUtil.getEnergyCost(blockEntity!!.dataModel)
                    tooltip.add(Text.of("Simulations with current data model drains " + f.format(data.toLong()) + "E/t"))
                }
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
        if (!animationList!!.containsKey(key)) {
            animationList!![key] = Animation()
        }
        return animationList!![key]
    }

    private fun animateString(
        matrices: MatrixStack,
        string: String,
        anim: Animation?,
        precedingAnim: Animation?,
        delay: Int,
        loop: Boolean,
        x: Int,
        y: Int,
        color: Int
    ) {
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

    private fun drawConsoleText(matrices: MatrixStack, x: Int, y: Int, spacing: Int) {
        val lines: Array<String>
        if (!blockEntity!!.hasDataModel() || DataModelUtil.getTier(blockEntity!!.dataModel) == DataModelTier.FAULTY) {
            animateString(matrices, "_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF)
        } else if (!blockEntity!!.hasPolymerClay() && !blockEntity!!.isCrafting) {
            lines = arrayOf("Cannot begin simulation", "Missing polymer medium", "_")
            val a1 = getAnimation("inputSlotEmpty1")
            val a2 = getAnimation("inputSlotEmpty2")
            val a3 = getAnimation("blinkingUnderline1")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (!hasEnergy() && !blockEntity!!.isCrafting) {
            lines = arrayOf("Cannot begin simulation", "System energy levels critical", "_")
            val a1 = getAnimation("lowEnergy1")
            val a2 = getAnimation("lowEnergy2")
            val a3 = getAnimation("blinkingUnderline2")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (blockEntity!!.outputIsFull() || blockEntity!!.pristineIsFull()) {
            lines = arrayOf("Cannot begin simulation", "Output or pristine buffer is full", "_")
            val a1 = getAnimation("outputSlotFilled1")
            val a2 = getAnimation("outputSlotFilled2")
            val a3 = getAnimation("blinkingUnderline3")
            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF)
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF)
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + spacing * 2, 0xFFFFFF)
        } else if (blockEntity!!.isCrafting) {
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                blockEntity!!.percentDone.toString() + "%",
                x + 176,
                y + 123,
                0x62D8FF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                blockEntity!!.getSimulationText("simulationProgressLine1"),
                x + 21,
                y + 51,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                blockEntity!!.getSimulationText("simulationProgressLine1Version"),
                x + 124,
                y + 51,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices,
                renderer,
                blockEntity!!.getSimulationText("simulationProgressLine2"),
                x + 21,
                y + 51 + spacing,
                0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine3"), x + 21,
                y + 51 + spacing * 2, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine4"), x + 21,
                y + 51 + spacing * 3, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine5"), x + 21,
                y + 51 + spacing * 4, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine6"), x + 21,
                y + 51 + spacing * 5, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine6Result"), x + 140,
                y + 51 + spacing * 5, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("simulationProgressLine7"), x + 21,
                y + 51 + spacing * 6, 0xFFFFFF
            )
            DrawableHelper.drawStringWithShadow(
                matrices, renderer, blockEntity!!.getSimulationText("blinkingDots1"), x + 128,
                y + 51 + spacing * 6, 0xFFFFFF
            )
        } else {
            animateString(matrices, "_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF)
        }
    }

    private fun hasEnergy(): Boolean {
        return blockEntity!!.hasEnergyForSimulation()
    }

    private fun dataModelChanged(): Boolean {
        return if (ItemStack.areItemsEqual(currentDataModel, blockEntity!!.dataModel)) {
            false
        } else {
            currentDataModel = blockEntity!!.dataModel
            true
        }
    }
}