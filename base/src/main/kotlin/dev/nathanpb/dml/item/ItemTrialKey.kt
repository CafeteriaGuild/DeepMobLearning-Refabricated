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
import dev.nathanpb.dml.data.trialKeyData
import dev.nathanpb.dml.utils.RenderUtils
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class ItemTrialKey : Item(FabricItemSettings().maxCount(1).fireproof()) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        if (stack != null && tooltip != null && world != null) {
            stack.trialKeyData.also { data ->
                if (data != null) {
                    Text.translatable("tooltip.${MOD_ID}.data_model.bound_to.1").setStyle(RenderUtils.STYLE).append(
                    Text.translatable("tooltip.${MOD_ID}.data_model.bound_to.2", data.category.displayName).setStyle(RenderUtils.ALT_STYLE)
                    ).let {
                        tooltip.add(it)
                    }
                    Text.translatable("tooltip.${MOD_ID}.tier.1").setStyle(RenderUtils.STYLE).append(
                    Text.translatable("tooltip.${MOD_ID}.tier.2", data.tier().text)
                    ).let {
                        tooltip.add(it)
                    }

                    if (data.affixes.isNotEmpty()) {
                        Text.translatable("tooltip.${MOD_ID}.trial_key.affixes").setStyle(RenderUtils.STYLE).let {
                            tooltip.add(it)
                        }
                        data.affixes.forEach {
                            tooltip.add(Text.of(" - ").copy().append(it.name))
                        }
                    }
                } else {
                    tooltip.add(Text.translatable("tooltip.${MOD_ID}.data_model.unbound").formatted(Formatting.DARK_RED))
                    Text.translatable("tooltip.${MOD_ID}.trial_key.unbound.tip").setStyle(RenderUtils.STYLE).let {
                        tooltip.add(it)
                    }
                }
            }
        }
    }
}
