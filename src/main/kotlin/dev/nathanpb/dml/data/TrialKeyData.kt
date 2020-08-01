package dev.nathanpb.dml.data

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.InvalidTrialKeyBase
import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.config
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemTrialKey
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import dev.nathanpb.dml.trial.affix.core.TrialAffixRegistry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.util.Identifier
import kotlin.random.Random

data class TrialKeyData (
    val category: EntityCategory,
    val dataAmount: Int = 0,
    val affixes: List<TrialAffix>
) {

    companion object {
        const val DATA_TAG_KEY = "${MOD_ID}.trialkey"
        const val CATEGORY_TAG_KEY = "category"
        const val DATA_AMOUNT_TAG_KEY = "dataAmount"
        const val AFFIX_TAG_KEY = "affixes"

        fun fromStack(stack: ItemStack) = when (stack.item) {
            is ItemTrialKey -> stack.getSubTag(DATA_TAG_KEY)?.let {
                val affixes = it.getList(AFFIX_TAG_KEY, 8).mapNotNull { affixId ->
                    TrialAffixRegistry.INSTANCE.findById(Identifier(affixId.asString()))
                }
                TrialKeyData(
                    EntityCategory.valueOf(it.getString(CATEGORY_TAG_KEY) ?: throw InvalidTrialKeyBase()),
                    it.getInt(DATA_AMOUNT_TAG_KEY),
                    affixes
                )
            }
            is ItemDataModel -> fromDataModelData(stack.dataModel)
            else -> throw InvalidTrialKeyBase()
        }

        fun fromDataModelData(data: DataModelData) = data.category?.let {
            TrialKeyData(it, data.dataAmount, emptyList())
        }

        fun createRandomAffixes(): List<TrialAffix> {
            return (0..Random.nextInt(config.affix.maxAffixesInKey.inc()))
                .filter { it > 0 }
                .mapNotNull {
                    TrialAffixRegistry.INSTANCE.pickRandomEnabled()
                }.distinctBy { it.id.toString() }
        }
    }

    fun tier() = DataModelTier.fromDataAmount(dataAmount)
    fun toTag() = CompoundTag().apply {
        putString(CATEGORY_TAG_KEY, category.name)
        putInt(DATA_AMOUNT_TAG_KEY, dataAmount)
        put(AFFIX_TAG_KEY,
            ListTag().also { list ->
                list.addAll(affixes.map {
                    StringTag.of(it.id.toString())
                })
            }
        )
    }
}

var ItemStack.trialKeyData : TrialKeyData?
    get() = TrialKeyData.fromStack(this)
    set(value) = if (value != null) {
        this.putSubTag(TrialKeyData.DATA_TAG_KEY, value.toTag())
    } else {
        this.removeSubTag(TrialKeyData.DATA_TAG_KEY)
    }
