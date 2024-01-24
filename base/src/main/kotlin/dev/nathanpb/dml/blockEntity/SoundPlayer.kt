package dev.nathanpb.dml.blockEntity

import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SoundPlayer(
    private val soundEvent: SoundEvent,
    private val canPlay: Boolean
) {

    fun playSound(world: World, pos: BlockPos) {
        if(!canPlay) return
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1F, 1F)
    }

}