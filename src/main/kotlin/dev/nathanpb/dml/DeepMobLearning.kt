package dev.nathanpb.dml

import dev.nathanpb.dml.item.registerItems
import net.minecraft.util.Identifier

@Suppress("unused")
fun init() {
    registerItems()
    println("Deep Mob Learning good to go")
}

fun identifier(path: String) = Identifier("deepmoblearning", path)
