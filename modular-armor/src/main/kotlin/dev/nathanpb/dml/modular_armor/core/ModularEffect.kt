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

package dev.nathanpb.dml.modular_armor.core

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.utils.RenderUtils
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import dev.nathanpb.dml.utils.getPipeText
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import org.jetbrains.annotations.ApiStatus

abstract class ModularEffect<T: ModularEffectTriggerPayload>(
    val id: Identifier,
    val category: EntityCategory,
    val applyCost: ()->Long
) {

    var modIdFormat = "modulareffect.${MOD_ID}.${id.path}"
    var otherModIdFormat = "modulareffect.${MOD_ID}.${id.namespace}.${id.path}"

    val isEnabled = {
        applyCost() >= 0L
    }

    open val name = if(id.namespace == MOD_ID) {
        Text.translatable(modIdFormat)
    } else {
        Text.translatable(otherModIdFormat)
    }
    open val description = if(id.namespace == MOD_ID) {
        Text.translatable("$modIdFormat.description")
    } else {
        Text.translatable("$otherModIdFormat.description")
    }


    lateinit var entityAttribute: EntityAttribute

    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    abstract fun registerEvents()
    abstract fun acceptTier(tier: DataModelTier): Boolean

    /* Effect Info stuff - used only for display */
    abstract fun minimumTier(): DataModelTier
    open fun isScaled(): Boolean = false // true on effects that use the data model's tier ordinal value for odds or increased effects
    open fun getEnergyConsumptionType(): EffectInfo.EnergyConsumptionType = EffectInfo.EnergyConsumptionType.SECOND
    fun getEffectInfo(): EffectInfo {
        return EffectInfo(
            minimumTier(),
            isScaled(),
            applyCost(),
            getEnergyConsumptionType()
        )
    }

    @ApiStatus.OverrideOnly
    protected open fun createEntityAttribute(): EntityAttribute {
        return ClampedEntityAttribute((name.content as TranslatableTextContent).key, 0.0, 0.0, DataModelTier.values().size.dec() * 4.0)
            .setTracked(true)
    }

    open fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        val level = DataModelTier.values().count {
            acceptTier(it) && it <= armor.tier()
        }
        return EntityAttributeModifier(id.toString(), level.toDouble(), EntityAttributeModifier.Operation.ADDITION)
    }

    @ApiStatus.Internal
    fun registerEntityAttribute() {
        entityAttribute = Registry.register(Registries.ATTRIBUTE, id, createEntityAttribute())
    }

    protected open fun canApply(context: ModularEffectContext, payload: T): Boolean {
        return isEnabled()
            && context.dataModel.category == category
            && acceptTier(context.tier)
            && context.armor.pristineEnergy >= applyCost()
            && id !in context.armor.disabledEffects
    }

    private fun attemptToConsumeEnergy(context: ModularEffectContext) {
        if(shouldConsumeEnergy(context)) {
            context.armor.pristineEnergy -= applyCost()
        }
    }

    fun <R>attemptToApply(context: ModularEffectContext, payload: T, body: (ModularEffectContext, T) -> R): TypedActionResult<R> {
        if(canApply(context, payload)) {
            attemptToConsumeEnergy(context)
            return TypedActionResult.success(body(context, payload))
        }
        return TypedActionResult.fail(null)
    }

    fun attemptToApply(context: ModularEffectContext, payload: T): ActionResult {
        return attemptToApply(context, payload) { _, _ -> }.result
    }

    open fun shouldConsumeEnergy(context: ModularEffectContext) = true

    fun sumLevelsOf(stack: ItemStack): Double {
        return (stack.item as? ItemModularGlitchArmor)?.let { item ->
            item.getAttributeModifiers(stack, item.slotType)
                .get(entityAttribute)
                .sumOf(EntityAttributeModifier::getValue)
        } ?: 0.0
    }

    fun sumLevelsOf(stacks: List<ItemStack>): Double {
        return stacks.sumOf(this::sumLevelsOf)
    }


    data class EffectInfo(
        val minimumTier: DataModelTier,
        val scaled: Boolean,
        val energyCost: Long,
        val energyConsumptionType: EnergyConsumptionType
    ) {

        fun text(): Text {
            val tierText = if(!scaled) {
                minimumTier.text
            } else {
                Text.translatable(
                    "tooltip.${MOD_ID}.data_tier_scales"
                ).formatted(Formatting.YELLOW)
            }

            val energyConsumptionText = if(energyCost > 0) {
                Text.translatable(
                    "tooltip.${MOD_ID}.data_amount.2",
                    energyCost,
                    energyConsumptionType.text
                )
            } else {
                Text.translatable("tooltip.${MOD_ID}.free")
            }.also {
                it.style = RenderUtils.ALT_STYLE
            }

            return getPipeText(
                tierText,
                energyConsumptionText,
                STYLE
            )
        }


        enum class EnergyConsumptionType(val text: Text) {

            SECOND(Text.translatable("energyConsumption.${MOD_ID}.second")),
            USE(Text.translatable("energyConsumption.${MOD_ID}.use"))

        }
    }
}
