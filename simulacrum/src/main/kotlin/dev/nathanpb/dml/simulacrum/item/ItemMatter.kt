package dev.nathanpb.dml.simulacrum.item

import dev.nathanpb.dml.utils.RenderUtils
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.random.Random.Default.nextFloat

class ItemMatter(_settings: Settings?, _experience: Int) : Item(_settings) {


    private val experience: Int = _experience

    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack>? {
        val stack = user.getStackInHand(hand)
        if (!world.isClient) {
            var count = 1
            if(user.isSneaking) {
                count = stack.count
                stack.decrement(count)
            } else {
                stack.decrement(1)
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.1f, nextFloat())
            }
            user.addExperience(count * experience)
        }
        return TypedActionResult.consume(stack)
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text?>, context: TooltipContext?) {
        if(world == null) return
        tooltip.add(
            Text.translatable("tooltip.dml-refabricated.matter.1").setStyle(RenderUtils.STYLE).append(
            Text.translatable("tooltip.dml-refabricated.matter.2").setStyle(RenderUtils.ALT_STYLE))
        )
        tooltip.add(
            Text.translatable("tooltip.dml-refabricated.matter.3").setStyle(RenderUtils.STYLE).append(
            Text.translatable("tooltip.dml-refabricated.matter.4").setStyle(RenderUtils.ALT_STYLE))
        )
        tooltip.add(
            Text.translatable("tooltip.dml-refabricated.matter.5").setStyle(RenderUtils.STYLE).append(
            Text.literal(experience.toString()).setStyle(RenderUtils.ALT_STYLE))
        )
    }
}