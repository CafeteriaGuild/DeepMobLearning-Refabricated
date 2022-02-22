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

package dev.nathanpb.dml.data

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.NotDataModelException
import dev.nathanpb.dml.entityCategory.EntityCategory
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.ktdatatag.data.MutableCompoundData
import dev.nathanpb.ktdatatag.serializer.Serializers
import net.minecraft.item.ItemStack

class DataModelData(val stack: ItemStack, val category: EntityCategory?) : MutableCompoundData(stack.orCreateNbt) {

    companion object {
        const val DATA_AMOUNT_TAG_KEY = "${MOD_ID}.datamodel.dataAmount"
    }

    var dataAmount by persistentDefaulted(0, Serializers.INT, DATA_AMOUNT_TAG_KEY)

    fun tier() = DataModelTier.fromDataAmount(dataAmount)

}

val ItemStack.dataModel: DataModelData
    get() {
        item.let { item ->
            if (item is ItemDataModel) {
                return DataModelData(this, item.category)
            }
        }
        throw NotDataModelException()
    }
