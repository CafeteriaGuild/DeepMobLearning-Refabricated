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

package dev.nathanpb.dml.item

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.getEnergyBarStep
import dev.nathanpb.dml.utils.getEnergyTooltipText
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterials
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyItem


class ItemGlitchSword : SwordItem(
        ToolMaterials.NETHERITE, // FIXME use unique ToolMaterial
        3,
        -2.4f,
        FabricItemSettings().fireproof()
), SimpleEnergyItem {

    private val usageCost = baseConfig.misc.glitchSword.usageCost

    override fun getAttributeModifiers(stack: ItemStack, slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier> {
        if(getStoredEnergy(stack) >= usageCost) {
            return super.getAttributeModifiers(stack, slot)
        }
        return ImmutableMultimap.of()
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if(getStoredEnergy(stack) >= usageCost) {
            tryUseEnergy(stack, usageCost)
        }
        return true
    }

    override fun getEnergyCapacity(stack: ItemStack) = baseConfig.misc.glitchSword.energyCapacity

    override fun getEnergyMaxInput(stack: ItemStack) = baseConfig.misc.glitchSword.energyInput

    override fun getEnergyMaxOutput(stack: ItemStack) = 0L

    override fun isEnchantable(stack: ItemStack) = false

    override fun getItemBarStep(stack: ItemStack): Int = getEnergyBarStep(stack)

    override fun getItemBarColor(stack: ItemStack) = RenderUtils.ENERGY_COLOR

    override fun isItemBarVisible(stack: ItemStack) = true

    override fun getRarity(stack: ItemStack) = Rarity.EPIC

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(getEnergyTooltipText(stack))
    }

    companion object {

        fun getIncreasedDamage(source: DamageSource, amount: Float): Float {
            var finalAmount = amount
            if(source.typeRegistryEntry.key.get().value == DamageTypes.PLAYER_ATTACK.value && source.attacker is PlayerEntity) {
                val attacker = source.attacker as PlayerEntity
                if(attacker.getStackInHand(attacker.activeHand).isOf(ITEM_GLITCH_SWORD)) {
                    finalAmount *= 2F
                }
            }
            return finalAmount
        }
    }

}