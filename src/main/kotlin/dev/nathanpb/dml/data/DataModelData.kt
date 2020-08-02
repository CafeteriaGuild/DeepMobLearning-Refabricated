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
import dev.nathanpb.dml.item.ItemDataModel
import net.minecraft.item.ItemStack

class DataModelData(val stack: ItemStack, val category: EntityCategory?) {

    companion object {
        const val DATA_AMOUNT_TAG_KEY = "${MOD_ID}.datamodel.dataAmount"
    }

    init {
        if (stack.item !is ItemDataModel) {
            throw NotDataModelException()
        }
    }

    var dataAmount: Int
        get() = stack.orCreateTag.getInt(DATA_AMOUNT_TAG_KEY)
        set(value) = stack.orCreateTag.putInt(DATA_AMOUNT_TAG_KEY, value)

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
