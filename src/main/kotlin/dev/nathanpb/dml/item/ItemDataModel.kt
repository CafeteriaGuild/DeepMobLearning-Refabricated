/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.item

import dev.nathanpb.dml.data.dataModel
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ItemDataModel : Item(settings().maxCount(1)) {

    companion object {
        val MODEL_OVERRIDE_MAP = arrayOf(
            EntityType.BLAZE,
            EntityType.CREEPER,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN,
            EntityType.GHAST,
            EntityType.GUARDIAN,
            EntityType.SHULKER,
            EntityType.SKELETON,
            EntityType.SLIME,
            EntityType.SPIDER,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE
        )
    }

    init {
        addPropertyGetter(Identifier("entity")) { stack, world, entity ->
            stack.dataModel.let { data ->
                if(data.isBound()) {
                    MODEL_OVERRIDE_MAP
                        .indexOf(data.entity)
                        .inc()
                        .toFloat()
                } else 0F
            }
        }
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (stack != null && tooltip != null) {
            stack.dataModel.let { data ->
                if (data.isBound()) {
                    tooltip.add(TranslatableText("tooltip.deepmoblearning.data_model.bound_to", data.entity?.name))
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
                    if (entity is Monster && !data.isBound()) {
                        data.entity = entity.type
                    }
                }
            }
        }
        return super.useOnEntity(_stack, user, entity, hand)
    }
}
