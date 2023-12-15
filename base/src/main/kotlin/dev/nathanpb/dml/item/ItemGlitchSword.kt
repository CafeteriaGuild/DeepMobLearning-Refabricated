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
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_STYLE
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
import net.minecraft.util.Formatting
import net.minecraft.util.Rarity
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyItem
import kotlin.math.roundToInt


class ItemGlitchSword : SwordItem(
        ToolMaterials.NETHERITE, // FIXME use unique ToolMaterial
        3,
        -2.4f,
        FabricItemSettings().fireproof()
), SimpleEnergyItem {

    private val energyCost = 200L // TODO add config


    override fun getAttributeModifiers(stack: ItemStack, slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier> {
        if(getStoredEnergy(stack) >= energyCost) {
            return super.getAttributeModifiers(stack, slot)
        }
        return ImmutableMultimap.of()
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if(getStoredEnergy(stack) >= energyCost) {
            tryUseEnergy(stack, energyCost)
        }
        return true
    }

    override fun getEnergyCapacity(stack: ItemStack) = 12500L

    override fun getEnergyMaxInput(stack: ItemStack) = 2048L

    override fun getEnergyMaxOutput(stack: ItemStack) = 0L

    override fun isEnchantable(stack: ItemStack) = false

    override fun getItemBarStep(stack: ItemStack): Int {
        val max = getEnergyCapacity(stack)
        val current = getStoredEnergy(stack)

        return if(current > max) 13 else ((13f * current) / max).roundToInt()
    }

    override fun getItemBarColor(stack: ItemStack) = RenderUtils.ENERGY_COLOR

    override fun isItemBarVisible(stack: ItemStack) = true

    override fun getRarity(stack: ItemStack) = Rarity.EPIC

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        val energyText = Text.translatable("text.dml-refabricated.energy").append(Text.of(" "))
        energyText.style = ENERGY_STYLE.withBold(true)

        val energyAmountText = Text.translatable(
            "tooltip.dml-refabricated.data_amount.2",
            getStoredEnergy(stack),
            getEnergyCapacity(stack)
        ).formatted(Formatting.YELLOW)


        tooltip.add(energyText.append(energyAmountText))
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