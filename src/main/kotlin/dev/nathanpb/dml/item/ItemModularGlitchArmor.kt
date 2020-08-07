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
import dev.nathanpb.dml.armor.GlitchArmorMaterial
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ItemModularGlitchArmor(slot: EquipmentSlot, settings: Settings) : ArmorItem(
        GlitchArmorMaterial.INSTANCE,
        slot,
        settings.fireproof()
) {

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (world?.isClient == true && stack != null && tooltip != null) {
            val data = ModularArmorData(stack)
            if (!data.tier().isMaxTier()) {
                tooltip.add(
                    TranslatableText(
                        "tooltip.${MOD_ID}.data_model.data_amount",
                        data.dataAmount,
                        data.dataRemainingToNextTier()
                    )
                )
            }
            tooltip.add(
                TranslatableText(
                    "tooltip.${MOD_ID}.data_model.tier",
                    data.tier().text
                )
            )
            MinecraftClient.getInstance().player?.let { player ->
                if (player.isCreative) {
                    tooltip.add(TranslatableText("tooltip.${MOD_ID}.data_model.cheat"))
                }
            }
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun postProcessTag(tag: CompoundTag?): Boolean {
        tag?.putBoolean("Unbreakable", true)
        return super.postProcessTag(tag)
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (user?.isCreative == true && user.isSneaking && hand != null) {
            val stack = user.getStackInHand(hand)
            if (stack.item is ItemModularGlitchArmor) {
                val data = ModularArmorData(stack)
                val tier = data.tier()

                data.dataAmount = ModularArmorData.amountRequiredTo(
                    if (tier.isMaxTier()) DataModelTier.FAULTY else tier.nextTierOrCurrent()
                )
                return TypedActionResult.success(stack)
            }
        }
        return super.use(world, user, hand)
    }

    override fun isDamageable() = false
}
