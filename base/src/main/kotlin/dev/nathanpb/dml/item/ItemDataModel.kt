/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.item

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ItemDataModel(val category: EntityCategory? = null) : Item(FabricItemSettings().maxCount(1).fireproof()) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (world?.isClient == true && stack != null && tooltip != null) {
            if (category != null) {
                stack.dataModel.let { data ->
                    if (!data.tier().isMaxTier()) {
                        Text.translatable("tooltip.${MOD_ID}.data_amount.1").setStyle(RenderUtils.STYLE).append(
                        Text.translatable("tooltip.${MOD_ID}.data_amount.2", data.dataAmount, data.tier().nextTierOrCurrent().dataAmount).setStyle(RenderUtils.ALT_STYLE)
                        ).let {
                            tooltip.add(it)
                        }
                    }
                    Text.translatable("tooltip.${MOD_ID}.tier.1").setStyle(RenderUtils.STYLE).append(
                    Text.translatable("tooltip.${MOD_ID}.tier.2", data.tier().text)
                    ).let {
                        tooltip.add(it)
                    }

                    MinecraftClient.getInstance().player?.let { player ->
                        if (player.isCreative) {
                            tooltip.add(Text.translatable("tooltip.${MOD_ID}.cheat").formatted(Formatting.GRAY, Formatting.ITALIC))
                        }
                    }
                }
            }
        }
    }

    // please do not remove me
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (user?.isCreative == true && user.isSneaking && hand != null) {
            val stack = user.getStackInHand(hand)
            if (stack.item is ItemDataModel) {
                val tier = stack.dataModel.tier()
                stack.dataModel.dataAmount = if (tier.isMaxTier()) {
                    DataModelTier.FAULTY.dataAmount
                } else tier.nextTierOrCurrent().dataAmount
            }
        }
        return super.use(world, user, hand)
    }
}
