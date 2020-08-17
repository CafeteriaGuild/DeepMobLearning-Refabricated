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

package dev.nathanpb.dml.armor.modular.core

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.item.ItemModularGlitchArmor
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import org.jetbrains.annotations.ApiStatus
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

abstract class ModularEffect<T: ModularEffectTriggerPayload>(
    val id: Identifier,
    val category: EntityCategory,
    val isEnabled: ()->Boolean,
    applyCost: ()->Float
) {

    val maxApplyCost = {
        ceil(applyCost()).toInt()
    }

    val getApplyCost = {
        val cost = applyCost()
        val integer = floor(cost).toInt()
        val float = cost % 1

        if (float == 0F) integer else {
            integer + (if (Random.nextFloat() < float) 1 else 0)
        }
    }

    open val name = if (id.namespace == MOD_ID) {
        TranslatableText("modulareffect.${MOD_ID}.${id.path}")
    } else {
        TranslatableText("modulareffect.${MOD_ID}.${id.namespace}.${id.path}")
    }

    lateinit var entityAttribute:  EntityAttribute

    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    abstract fun registerEvents()
    protected abstract fun shouldConsumeData(context: ModularEffectContext): Boolean
    abstract fun acceptTier(tier: DataModelTier): Boolean

    @ApiStatus.OverrideOnly
    protected open fun createEntityAttribute(): EntityAttribute {
        return ClampedEntityAttribute(name.key, 0.0, 0.0, DataModelTier.values().size.dec() * 4.0)
            .setTracked(true)
    }

    open fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), armor.tier().ordinal.inc().toDouble(), EntityAttributeModifier.Operation.ADDITION)
    }

    @ApiStatus.Internal
    fun registerEntityAttribute() {
        entityAttribute = Registry.register(Registry.ATTRIBUTE, id, createEntityAttribute())
    }

    protected open fun canApply(context: ModularEffectContext, payload: T): Boolean {
        return isEnabled()
            && acceptTier(context.tier)
            && context.dataModel.dataAmount >= maxApplyCost()
    }

    private fun attemptConsumeData(context: ModularEffectContext) {
        if (shouldConsumeData(context)) {
            context.dataModel.dataAmount -= getApplyCost()
        }
    }

    fun <R>attemptToApply(context: ModularEffectContext, payload: T, body: (ModularEffectContext, T) -> R): TypedActionResult<R> {
        if (canApply(context, payload)) {
            attemptConsumeData(context)
            return TypedActionResult.success(body(context, payload))
        }
        return TypedActionResult.fail(null)
    }

    fun sumLevelsOf(stack: ItemStack): Int {
        return (stack.item as? ItemModularGlitchArmor)?.let { item ->
            item.getAttributeModifiers(stack, item.slotType)
                .get(entityAttribute)
                .sumByDouble(EntityAttributeModifier::getValue)
                .roundToInt()
        } ?: 0
    }

    fun sumLevelsOf(stacks: List<ItemStack>): Int {
        return stacks.map(this::sumLevelsOf).sum()
    }
}
