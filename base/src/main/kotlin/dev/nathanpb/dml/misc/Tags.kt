package dev.nathanpb.dml.misc

import dev.nathanpb.dml.identifier
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys

import net.minecraft.registry.tag.TagKey


val TRIAL_GRIEF_WHITELIST: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, identifier("trial_grief_whitelist"))

val ATTUNED_DATA_MODELS: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, identifier("attuned_data_models"))
val PRISTINE_MATTER: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, identifier("pristine_matter"))