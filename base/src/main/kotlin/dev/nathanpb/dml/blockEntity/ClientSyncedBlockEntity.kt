package dev.nathanpb.dml.blockEntity

import com.google.common.base.Preconditions
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.world.ClientWorld
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos


/* A special implementation of BlockEntity, syncing nbt data to client, as a fix to old BEs using the now removed BlockEntityClientSerializable.
*
* This was made by Technici4n, so props there :)
*/
abstract class ClientSyncedBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {

    @JvmOverloads
    fun sync(shouldRemesh: Boolean = true) {
        Preconditions.checkNotNull(world) // Maintain distinct failure case from below
        check(world is ServerWorld) { "Cannot call sync() on the logical client! Did you check world.isClient first?" }
        shouldClientRemesh = shouldRemesh or shouldClientRemesh
        (world as ServerWorld).chunkManager.markForUpdate(pos)
    }

    abstract fun toTag(nbt: NbtCompound)
    abstract fun fromTag(nbt: NbtCompound)

    abstract fun toClientTag(nbt: NbtCompound)
    abstract fun fromClientTag(nbt: NbtCompound)

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = super.toInitialChunkDataNbt()
        toClientTag(nbt)
        nbt.putBoolean("#c", shouldClientRemesh) // mark client tag
        shouldClientRemesh = false
        return nbt
    }

    override fun writeNbt(nbt: NbtCompound) {
        toTag(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        if(nbt.contains("#c")) {
            fromClientTag(nbt)
            if(nbt.getBoolean("#c")) {
                remesh()
            }
        } else {
            fromTag(nbt)
        }
    }

    fun remesh() {
        Preconditions.checkNotNull(world)
        check(world is ClientWorld) { "Cannot call remesh() on the server!" }
        (world as ClientWorld).updateListeners(pos, null, null, 0)
    }

    protected val isClientSide: Boolean
        get() {
            checkNotNull(world) { "Cannot determine if the BE is client-side if it has no world yet" }
            return world!!.isClient()
        }

    private var shouldClientRemesh = true

}