package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.block.BlockDisruptionsCore
import dev.nathanpb.dml.screen.handler.DisruptionsCoreScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.MovementType
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.*
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BlockEntityDisruptionsCore(
    pos: BlockPos,
    state: BlockState
): LootableContainerBlockEntity(BLOCKENTITY_DISRUPTIONS_CORE, pos, state) {


    var inventory: DefaultedList<ItemStack>
    var viewerCount = 0
    var animationStage: AnimationStage
    var animationProgress = 0f
    var prevAnimationProgress = 0f


    init {
        inventory = DefaultedList.ofSize(27, ItemStack.EMPTY)
        animationStage = AnimationStage.CLOSED
    }



    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntityDisruptionsCore) {
            blockEntity.updateAnimation(world, pos, state)
        }
    }


    fun updateAnimation(world: World, pos: BlockPos, state: BlockState) {
        prevAnimationProgress = animationProgress
        when(animationStage) {
            AnimationStage.CLOSED -> animationProgress = 0.0f
            AnimationStage.OPENING -> {
                animationProgress += 0.1f
                if(animationProgress >= 1.0f) {
                    animationStage = AnimationStage.OPENED
                    animationProgress = 1.0f
                    updateNeighborStates(world, pos, state)
                }
                pushEntities(world, pos, state)
            }

            AnimationStage.CLOSING -> {
                animationProgress -= 0.1f
                if(animationProgress <= 0.0f) {
                    animationStage = AnimationStage.CLOSED
                    animationProgress = 0.0f
                    updateNeighborStates(world, pos, state)
                }
            }

            AnimationStage.OPENED -> animationProgress = 1.0f
        }
    }

    fun getBoundingBox(): Box {
        return ShulkerEntity.calculateBoundingBox(
            Direction.UP,
            0.5f * getAnimationProgress(1.0f)
        )
    }

    private fun pushEntities(world: World, pos: BlockPos, state: BlockState) {
        if(state.block !is BlockDisruptionsCore) return;
        val box = ShulkerEntity.calculateBoundingBox(
            Direction.UP,
            prevAnimationProgress,
            animationProgress
        ).offset(pos)

        val list = world.getOtherEntities(null, box)
        if(list.isEmpty()) return

        for(i in list.indices) {
            val entity = list[i]
            if(entity.pistonBehavior != PistonBehavior.IGNORE) {
                entity.move(
                    MovementType.SHULKER_BOX,
                    Vec3d(
                        0.0,
                        (box.yLength + 0.01),
                        0.0
                    )
                )
            }
        }
    }

    override fun size(): Int {
        return inventory.size
    }

    override fun onSyncedBlockEvent(type: Int, data: Int): Boolean {
        return if(type == 1) {
            viewerCount = data
            if(data == 0) {
                animationStage = AnimationStage.CLOSING
                updateNeighborStates(world!!, pos, cachedState)
            }
            if(data == 1) {
                animationStage = AnimationStage.OPENING
                updateNeighborStates(world!!, pos, cachedState)
            }
            true
        } else {
            super.onSyncedBlockEvent(type, data)
        }
    }

    fun updateNeighborStates(world: World, pos: BlockPos, state: BlockState) {
        state.updateNeighbors(world, pos, 3)
    }

    override fun onOpen(player: PlayerEntity) {
        if(!removed && !player.isSpectator) {
            if(viewerCount < 0) {
                viewerCount = 0
            }
            ++viewerCount
            world!!.addSyncedBlockEvent(pos, cachedState.block, 1, viewerCount)
            if(viewerCount == 1) {
                world!!.emitGameEvent(player, GameEvent.CONTAINER_OPEN, pos)
                world!!.playSound(
                    null as PlayerEntity?,
                    pos,
                    SoundEvents.BLOCK_SHULKER_BOX_OPEN,
                    SoundCategory.BLOCKS,
                    0.5f,
                    world!!.random.nextFloat() * 0.1f + 0.9f
                )
            }
        }
    }

    override fun onClose(player: PlayerEntity) {
        if(!removed && !player.isSpectator) {
            --viewerCount
            world!!.addSyncedBlockEvent(pos, cachedState.block, 1, viewerCount)
            if(viewerCount <= 0) {
                world!!.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, pos)
                world!!.playSound(
                    null as PlayerEntity?,
                    pos,
                    SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
                    SoundCategory.BLOCKS,
                    0.5f,
                    world!!.random.nextFloat() * 0.1f + 0.9f
                )
            }
        }
    }

    override fun getContainerName(): Text {
        return Text.empty()
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        readInventoryNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
        if(!serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, inventory, false)
        }
    }

    fun readInventoryNbt(nbt: NbtCompound) {
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        if(!deserializeLootTable(nbt) && nbt.contains("Items", 9)) {
            Inventories.readNbt(nbt, inventory)
        }
    }

    override fun getInvStackList(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun setInvStackList(list: DefaultedList<ItemStack>) {
        inventory = list
    }

    fun getAnimationProgress(delta: Float): Float {
        return MathHelper.lerp(delta, prevAnimationProgress, animationProgress)
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory): ScreenHandler {
        return DisruptionsCoreScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    fun suffocates(): Boolean {
        return animationStage == AnimationStage.CLOSED
    }


    enum class AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING
    }

}
