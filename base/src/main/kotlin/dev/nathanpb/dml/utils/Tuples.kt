package dev.nathanpb.dml.utils

import net.minecraft.util.Rarity

data class ItemTuple(val identifier: String, val enabled: Boolean = true)

data class RarityTuple(val identifier: String, val rarity: Rarity = Rarity.COMMON)