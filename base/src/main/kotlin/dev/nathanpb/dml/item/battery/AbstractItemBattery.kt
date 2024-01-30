package dev.nathanpb.dml.item.battery

import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_STYLE
import dev.nathanpb.dml.utils.getBooleanInfoText
import dev.nathanpb.dml.utils.getEnergyTooltipText
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyItem
import java.awt.Color


abstract class AbstractItemBattery: Item(Settings().maxCount(1)), SimpleEnergyItem {

    abstract fun isPristineEnergy(): Boolean /** true on modular-armor only! */

    abstract fun getColor(): Color

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if(world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand))
        if(user.isSneaking) {
            val stack = user.getStackInHand(hand)
            val isEnabled = hasDistributeEnergy(stack)

            stack.getOrCreateNbt().putBoolean(distributeEnergyKey, !isEnabled)
            (user as ServerPlayerEntity).networkHandler.sendPacket(
                PlaySoundS2CPacket(
                    Registries.SOUND_EVENT.getEntry(
                        if(isEnabled) SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE else SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN
                    ),
                    SoundCategory.PLAYERS,
                    user.getX(), user.getY(), user.getZ(),
                    1.0f, 1.0f,
                    user.getBlockPos().asLong()
                )
            )
            user.sendMessage(getEnabledText(stack), true)
            return TypedActionResult.success(stack)
        }
        return super.use(world, user, hand)
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if(world.isClient() || entity !is PlayerEntity) return
        if(!hasDistributeEnergy(stack)) return

        // TODO add distribute energy logic
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: net.minecraft.client.item.TooltipContext) {
        tooltip.add(getEnergyTooltipText(stack, primaryStyle, secondaryStyle, isPristineEnergy()))
        tooltip.add(getEnabledText(stack))
    }

    override fun hasGlint(stack: ItemStack?): Boolean {
        return getStoredEnergy(stack) >= getEnergyCapacity(stack)
    }

    fun getScaledColor(stack: ItemStack): Int {
        val hsbColor = Color.RGBtoHSB(getColor().red, getColor().green, getColor().blue, null)
        val energyPercentage: Float = ((getStoredEnergy(stack).toFloat() / getEnergyCapacity(stack).toFloat()))

        hsbColor[1] = hsbColor[1] * energyPercentage

        return Color.HSBtoRGB(hsbColor[0], hsbColor[1], hsbColor[2])
    }

    open val primaryStyle: Style = ENERGY_STYLE
    open val secondaryStyle: Style = Style.EMPTY.withFormatting(Formatting.YELLOW)


    private fun getEnabledText(stack: ItemStack): Text {
        val enabledText = Text.translatable("tooltip.dml-refabricated.distribute_energy")
        return getBooleanInfoText(enabledText, hasDistributeEnergy(stack), primaryStyle, secondaryStyle)
    }

    private fun hasDistributeEnergy(stack: ItemStack): Boolean {
        return stack.orCreateNbt.getBoolean(distributeEnergyKey)
    }

    private val distributeEnergyKey = "dml-refabricated:distribute_energy"

}