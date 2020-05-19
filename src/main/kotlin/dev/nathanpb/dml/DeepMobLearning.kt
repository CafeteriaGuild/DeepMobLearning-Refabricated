package dev.nathanpb.dml

import dev.nathanpb.dml.item.registerItems
import net.minecraft.util.Identifier

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

@Suppress("unused")
fun init() {
    registerItems()
    println("Deep Mob Learning good to go")
}

fun identifier(path: String) = Identifier("deepmoblearning", path)
