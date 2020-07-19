/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.item

import dev.nathanpb.dml.data.EntityCategory
import dev.nathanpb.dml.data.dataModel
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ItemDataModel : Item(settings().maxCount(1)) {
    init {
        addPropertyGetter(Identifier("entity")) { stack, _, _ ->
            stack.dataModel.category?.ordinal?.inc()?.toFloat() ?: 0F
        }
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (world != null && stack != null && tooltip != null) {
            stack.dataModel.let { data ->
                if (data.category != null) {
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.bound_to", data.category?.displayName?.formatted()))
                    if (!data.tier().isMaxTier()) {
                        tooltip.add(TranslatableText(
                            "tooltip.deepmoblearning.data_model.data_amount",
                            data.dataAmount,
                            data.tier().nextTierOrCurrent().dataAmount - data.dataAmount
                        ))
                    }
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.tier", data.tier().text.asFormattedString()))
                } else {
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.unbound"))
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.unbound.tip"))
                }
            }
        }
    }

    override fun useOnEntity(_stack: ItemStack?, user: PlayerEntity?, entity: LivingEntity?, hand: Hand?): Boolean {
        if (entity != null && user != null) {
            val stack = user.getStackInHand(hand)
            if (!entity.world.isClient) {
                stack.dataModel.let { data ->
                    if (data.category == null) {
                        data.category = EntityCategory.values().firstOrNull {
                            entity.type in it.tag
                        }
                    }
                }
            }
        }
        return super.useOnEntity(_stack, user, entity, hand)
    }
}
