/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GLITCH_INGOT
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor.Companion.GLITCH_BOOTS
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor.Companion.GLITCH_HELMET
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity
import net.minecraft.world.World


class ItemGlitchSword() : SwordItem(
        ToolMaterials.NETHERITE, // FIXME use unique ToolMaterial
        3,
        -2.4f,
        FabricItemSettings().fireproof()
) {

    companion object {
        val GLITCH_SWORD = ItemGlitchSword()

        fun register() {
            Registry.register(Registries.ITEM, identifier("glitch_sword"), GLITCH_SWORD)

            ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
                it.addBefore(ItemStack(GLITCH_HELMET), GLITCH_SWORD)
            }
        }

    }

    override fun getRarity(stack: ItemStack?) = Rarity.EPIC

    override fun getItemBarColor(stack: ItemStack?) = 0x00FFC0

    /*override fun isItemBarVisible(stack: ItemStack): Boolean {
        val data = ModularArmorData(stack)
        return data.dataModel != null
    }

    override fun getItemBarStep(stack: ItemStack): Int {
        val data = ModularArmorData(stack)
        val max = data.tier().dataAmount
        val current = data.dataModel?.dataAmount ?: 0

        return if (max == 0) 0 else if (current > max) 13 else ((13f * current) / max).roundToInt()
    }*/

    /*override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (world?.isClient == true && stack != null && tooltip != null) {
            val data = ModularArmorData(stack)
            if (!data.tier().isMaxTier()) {
                Text.translatable("tooltip.${MOD_ID}.data_amount.1").setStyle(RenderUtils.STYLE).append(
                Text.translatable("tooltip.${MOD_ID}.data_amount.2", data.dataAmount, ModularArmorData.amountRequiredTo(data.tier().nextTierOrCurrent()))
                    .setStyle(RenderUtils.ALT_STYLE)).let {
                        tooltip.add(it)
                    }
            }
            Text.translatable("tooltip.${MOD_ID}.tier.1").setStyle(RenderUtils.STYLE).append(
            Text.translatable("tooltip.${MOD_ID}.tier.2", data.tier().text)).let {
                tooltip.add(it)
            }

            MinecraftClient.getInstance().player?.let { player ->
                if (player.isCreative) {
                    tooltip.add(Text.translatable("tooltip.${MOD_ID}.cheat").formatted(Formatting.GRAY, Formatting.ITALIC))
                }
            }
        }
        super.appendTooltip(stack, world, tooltip, context)
    }*/

    override fun inventoryTick(stack: ItemStack, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (stack.hasEnchantments()) {
            stack.enchantments.firstOrNull {
                (it as? NbtCompound)?.getString("id") == "minecraft:mending"
            }?.let(stack.enchantments::remove)
        }
    }

}
