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

package dev.nathanpb.dml.armor.modular

import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.LivingEntityDamageEvent
import dev.nathanpb.dml.item.ItemModularGlitchArmor
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import kotlin.math.roundToInt

abstract class ProtectionLikeEffect(
    id: Identifier,
    category: EntityCategory,
    isEnabled: ()->Boolean,
    applyCost: ()->Float
) : ModularEffect(id, category, isEnabled, applyCost) {

    override fun registerEvents() {
        LivingEntityDamageEvent.register { context ->
            if (protectsAgainst(context.source)) {
                val protection = getProtectionAmount(context.entity.armorItems.toList())
                context.copy(damage = DamageUtil.getInflictedDamage(context.damage, protection.toFloat()))
            } else null
        }
    }

    abstract fun protectsAgainst(source: DamageSource): Boolean

    open fun getProtectionAmount(stack: ItemStack): Int {
        return (stack.item as? ItemModularGlitchArmor)?.let { item ->
            item.getAttributeModifiers(stack, item.slotType)
                .get(entityAttribute)
                .sumByDouble(EntityAttributeModifier::getValue)
                .roundToInt()
        } ?: 0
    }

    open fun getProtectionAmount(stacks: List<ItemStack>): Int {
        return stacks.map(this::getProtectionAmount).sum()
    }

}
