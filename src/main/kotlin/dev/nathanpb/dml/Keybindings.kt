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

package dev.nathanpb.dml

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

val CATEGORY_MODULAR_ARMOR = identifier("modular_armor")

val TELEPORT_KEYBINDING = register(identifier("teleport"), GLFW.GLFW_KEY_V, CATEGORY_MODULAR_ARMOR)
val SOUL_VISION_KEYBINDING = register(identifier("soul_vision"), GLFW.GLFW_KEY_B, CATEGORY_MODULAR_ARMOR)

private fun register(
    id: Identifier,
    default: Int,
    category: Identifier,
    type: InputUtil.Type = InputUtil.Type.KEYSYM
): KeyBinding {
    return KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.${id.namespace}.${id.path}",
            type,
            default,
            "category.${category.namespace}.${category.path}"
        )
    )
}

fun registerKeybindings() {
    TELEPORT_KEYBINDING
    SOUL_VISION_KEYBINDING
}

