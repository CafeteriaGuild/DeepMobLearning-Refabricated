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

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GLITCH_INGOT
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.modular_armor.core.ModularEffectRegistry
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.mixin.IArmorItemMixin
import dev.nathanpb.dml.modular_armor.screen.ModularArmorScreenHandlerFactory
import dev.nathanpb.dml.utils.*
import dev.nathanpb.dml.utils.RenderUtils.Companion.ALT_STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.TITLE_COLOR
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyItem


class ItemModularGlitchArmor(type: Type, settings: Settings) : ArmorItem(
        GlitchArmorMaterial.INSTANCE,
        type,
        settings.fireproof()
), SimpleEnergyItem {

    companion object {
        val GLITCH_HELMET = ItemModularGlitchArmor(Type.HELMET, FabricItemSettings().fireproof())
        val GLITCH_CHESTPLATE = ItemModularGlitchArmor(Type.CHESTPLATE, FabricItemSettings().fireproof())
        val GLITCH_LEGGINGS = ItemModularGlitchArmor(Type.LEGGINGS, FabricItemSettings().fireproof())
        val GLITCH_BOOTS = ItemModularGlitchArmor(Type.BOOTS, FabricItemSettings().fireproof())

        fun register() {
            linkedMapOf(
                "glitch_boots" to GLITCH_BOOTS,
                "glitch_leggings" to GLITCH_LEGGINGS,
                "glitch_chestplate" to GLITCH_CHESTPLATE,
                "glitch_helmet" to GLITCH_HELMET,
            ).forEach { (id, item) ->
                Registry.register(Registries.ITEM, identifier(id), item)

                ITEM_PRISTINE.registerForItems(::getEnergyStorage, item)

                ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
                    it.addAfter(ItemStack(ITEM_GLITCH_INGOT), item)
                }
            }

        }

    }

    override fun getEnergyCapacity(stack: ItemStack): Long = getEnergyProperties()!!.capacity

    override fun getEnergyMaxInput(stack: ItemStack): Long = getEnergyProperties()!!.input

    override fun getEnergyMaxOutput(stack: ItemStack): Long = getEnergyProperties()!!.output

    override fun getRarity(stack: ItemStack) = Rarity.EPIC

    override fun getItemBarStep(stack: ItemStack): Int = getEnergyBarStep(stack)

    override fun getItemBarColor(stack: ItemStack) = TITLE_COLOR

    override fun isItemBarVisible(stack: ItemStack): Boolean = true



    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        val data = ModularArmorData(stack)
        if(data.dataAmount > 0) { // TODO remove in 1.21
            tooltip.add(Text.translatable("tooltip.dml-refabricated.glitch_armor_upgrade.1").formatted(Formatting.RED, Formatting.BOLD))
            tooltip.add(Text.translatable("tooltip.dml-refabricated.glitch_armor_upgrade.2").formatted(Formatting.RED, Formatting.BOLD))
            return
        }

        tooltip.add(getPristineEnergyTooltipText(stack))

        val dataModelText: Text
        @Suppress("LiftReturnOrAssignment")
        if(data.dataModel != null) {
            val tierText = getParenthesisText(
                Text.translatable("tooltip.${MOD_ID}.tier.2", data.dataModel!!.category!!.displayName),
                data.dataModel!!.tier().text

            )
            dataModelText = getInfoText(
                Text.translatable("tooltip.${MOD_ID}.data_model_header"),
                tierText,
                STYLE,
                ALT_STYLE
            )
        } else {
            dataModelText = Text.translatable("tooltip.${MOD_ID}.no_data_model").formatted(Formatting.DARK_RED)
        }
        tooltip.add(dataModelText)

        /*Text.translatable("tooltip.${MOD_ID}.tier.1").setStyle(RenderUtils.STYLE).append(
        Text.translatable("tooltip.${MOD_ID}.tier.2", data.tier().text)).let {
            tooltip.add(it)
        }

        MinecraftClient.getInstance().player?.let { player ->
            if(player.isCreative) {
                tooltip.add(Text.translatable("tooltip.${MOD_ID}.cheat").formatted(Formatting.GRAY, Formatting.ITALIC))
            }
        }
        super.appendTooltip(stack, world, tooltip, context)*/
    }

    override fun getAttributeModifiers(stack: ItemStack, slot: EquipmentSlot?): Multimap<EntityAttribute, EntityAttributeModifier> {
        return super.getAttributeModifiers(slot).let {
            val builder = MultimapBuilder.ListMultimapBuilder
                .hashKeys()
                .arrayListValues()
                .build<EntityAttribute, EntityAttributeModifier>()

            if (slot != null && slot == type.equipmentSlot) {
                (material as? GlitchArmorMaterial)?.let { material ->
                    val data = ModularArmorData(stack)
                    val uuid = IArmorItemMixin.dmlRefGetModifierUUIDs()[type]

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

            builder
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if(world.isClient()) return TypedActionResult.pass(stack)

        /*if(user.isCreative && user.isSneaking) { // TODO add energy cheat
            val data = ModularArmorData(stack)

            data.dataAmount = ModularArmorData.amountRequiredTo(
                if(tier.isMaxTier()) DataModelTier.FAULTY else tier.nextTierOrCurrent()
            )
            return TypedActionResult.success(stack)
        }*/

        user.openHandledScreen(ModularArmorScreenHandlerFactory(hand, this))
        return TypedActionResult.success(stack)
    }

    override fun isDamageable() = false

    private fun appendModularEffectModifiers(armor: ModularArmorData, dataModel: DataModelData): Multimap<EntityAttribute, EntityAttributeModifier> {
        val multimap = MultimapBuilder.ListMultimapBuilder
            .hashKeys()
            .arrayListValues()
            .build<EntityAttribute, EntityAttributeModifier>()

        dataModel.category?.let { category ->
            if (dataModel.category != null) {
                ModularEffectRegistry.INSTANCE.allMatching(category, armor.tier())
                    .filter { it.id !in armor.disabledEffects }
                    .forEach {
                        multimap.put(it.entityAttribute, it.createEntityAttributeModifier(armor))
                    }
            }
        }

        return multimap
    }

    override fun inventoryTick(stack: ItemStack, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (stack.hasEnchantments()) {
            stack.enchantments.firstOrNull {
                (it as? NbtCompound)?.getString("id") == "minecraft:mending"
            }?.let(stack.enchantments::remove)
        }
    }

    fun getEnergyProperties() = energyProperties[type]

    // TODO add config values
    private val energyProperties: HashMap<Type, EnergyProperties> = hashMapOf(
        Type.HELMET to EnergyProperties(625000L, 3125L, 0L),
        Type.CHESTPLATE to EnergyProperties(1000000L, 5000L, 0L),
        Type.LEGGINGS to EnergyProperties(875000L, 4375L, 0L),
        Type.BOOTS to EnergyProperties(500000L, 2500L, 0L)
    )

    data class EnergyProperties(val capacity: Long, val input: Long, val output: Long)

}