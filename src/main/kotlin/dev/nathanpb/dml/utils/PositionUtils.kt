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

package dev.nathanpb.dml.utils

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.random.Random

fun BlockPos.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
fun BlockPos.toVec3i() = Vec3i(x, y, z)
fun Vec3d.toBlockPos() = BlockPos(x.toInt(), y.toInt(), z.toInt())
fun BlockPos.randomAround(radiusX: Int, radiusY: Int, radiusZ: Int) = add(
    Random.nextInt(radiusX*2+1) - radiusX,
    Random.nextInt(radiusY*2+1) - radiusY,
    Random.nextInt(radiusZ*2+1) - radiusZ
)
