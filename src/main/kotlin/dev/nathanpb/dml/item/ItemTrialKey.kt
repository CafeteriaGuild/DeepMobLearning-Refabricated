/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.item

import dev.nathanpb.dml.data.TrialKeyData
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World

class ItemTrialKey : Item(settings().maxCount(1)) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (stack != null && tooltip != null) {
            TrialKeyData.fromStack(stack).let { data ->
                if (data != null) {
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.bound_to", data.entity.name))
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.tier", data.tier().text.asFormattedString()))
                } else {
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.unbound"))
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.trial_key.unbound.tip"))
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.trial_key.unbound.tip2"))
                }
            }
        }
    }
}
