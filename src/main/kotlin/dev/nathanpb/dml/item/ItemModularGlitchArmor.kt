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

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.armor.GlitchArmorMaterial
import dev.nathanpb.dml.armor.modular.core.ModularEffectRegistry
import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.mixin.IArmorItemMixin
import dev.nathanpb.dml.screen.handler.ModularArmorScreenHandlerFactory
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
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
                tooltip += TranslatableText(
                    "tooltip.${MOD_ID}.data_model.data_amount",
                    data.dataAmount,
                    data.dataRemainingToNextTier()
                )
            }
            tooltip += TranslatableText(
                "tooltip.${MOD_ID}.data_model.tier",
                data.tier().text
            )
            MinecraftClient.getInstance().player?.let { player ->
                if (player.isCreative) {
                    tooltip.add(TranslatableText("tooltip.${MOD_ID}.data_model.cheat"))
                }
            }
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    fun getAttributeModifiers(stack: ItemStack, slot: EquipmentSlot?): Multimap<EntityAttribute, EntityAttributeModifier> {
        return super.getAttributeModifiers(slot).let { multimap ->
            val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
            builder.putAll(multimap)

            if (slot != null && slot == this.slot) {
                (material as? GlitchArmorMaterial)?.let { material ->

                    val data = ModularArmorData(stack)
                    val uuid = IArmorItemMixin.dmlRefGetModifierUUIDs()[slot.entitySlotId]

                    val protection = material.getProtectionAmount(slot, data.tier()).toDouble()
                    val toughness = material.getToughness(data.tier()).toDouble()
                    val knockback = material.getKnockbackResistance(data.tier()).toDouble()

                    builder.put(EntityAttributes.GENERIC_ARMOR, EntityAttributeModifier(uuid, "Armor modifier", protection, EntityAttributeModifier.Operation.ADDITION))
                    builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, EntityAttributeModifier(uuid, "Armor toughness", toughness, EntityAttributeModifier.Operation.ADDITION))
                    builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, EntityAttributeModifier(uuid, "Armor knockback resistance", knockback, EntityAttributeModifier.Operation.ADDITION))

                    data.dataModel?.let { dataModel ->
                        if (dataModel.category != null) {
                            builder.putAll(appendModularEffectModifiers(data, dataModel))
                        }
                    }
                }
            }

            builder.build()
        }
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand): TypedActionResult<ItemStack> {
        if (user != null) {
            val stack = user.getStackInHand(hand)
            if (user.isSneaking && user.isCreative) {
                if (world?.isClient == false) {
                    val data = ModularArmorData(stack)
                    val tier = data.tier()

                    data.dataAmount = ModularArmorData.amountRequiredTo(
                        if (tier.isMaxTier()) DataModelTier.FAULTY else tier.nextTierOrCurrent()
                    )
                }
                return TypedActionResult.success(stack)
            }

            if (world?.isClient == false) {
                user.openHandledScreen(ModularArmorScreenHandlerFactory(hand, this))
            }
            return TypedActionResult.success(stack)
        }
        return super.use(world, user, hand)
    }

    override fun isDamageable() = false

    fun appendModularEffectModifiers(armor: ModularArmorData, dataModel: DataModelData): Multimap<EntityAttribute, EntityAttributeModifier> {
        val multimap = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()

        if (dataModel.category != null) {
            ModularEffectRegistry.INSTANCE.allMatching(dataModel.category, armor.tier())
                .filter { it.id !in armor.disabledEffects }
                .forEach {
                    multimap.put(it.entityAttribute, it.createEntityAttributeModifier(armor))
                }
        }

        return multimap.build()
    }

    override fun inventoryTick(stack: ItemStack, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (stack.hasEnchantments()) {
            stack.enchantments.firstOrNull {
                (it as? CompoundTag)?.getString("id") == "minecraft:mending"
            }?.let(stack.enchantments::remove)
        }
    }
}
