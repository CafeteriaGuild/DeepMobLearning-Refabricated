package dev.nathanpb.dml.misc

import dev.nathanpb.dml.identifier
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys

import net.minecraft.registry.tag.TagKey


val TRIAL_GRIEF_WHITELIST: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, identifier("trial_grief_whitelist"))