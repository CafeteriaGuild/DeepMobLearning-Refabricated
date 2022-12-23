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

package dev.nathanpb.dml.blockEntity

import com.google.common.base.Preconditions
import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.TrialData
import dev.nathanpb.dml.data.serializers.TrialDataSerializer
import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.event.ModEvents
import dev.nathanpb.dml.inventory.TrialKeystoneInventory
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.trial.*
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import dev.nathanpb.dml.utils.*
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess
import kotlin.math.min
import kotlin.random.Random


class BlockEntityTrialKeystone(pos: BlockPos, state: BlockState) :
    BlockEntity(BLOCKENTITY_TRIAL_KEYSTONE, pos, state),
    InventoryProvider
{

    companion object {
        val BORDERS_RANGE
            get() = (config.trial.arenaRadius.squared() - 9).toDouble() .. (config.trial.arenaRadius.squared() + 9).toDouble()

        val ticker = BlockEntityTicker<BlockEntityTrialKeystone> { world, pos, _, blockEntity ->
            if (world?.isClient == true) {
                when (blockEntity.clientTrialState) {
                    TrialState.NOT_STARTED -> {
                        if (!config.trial.allowStartInWrongTerrain) {
                            MinecraftClient.getInstance().player?.let { clientPlayer ->
                                if (clientPlayer.squaredDistanceTo(pos.toVec3d()) <= config.trial.arenaRadius.squared()) {
                                    blockEntity.spawnParticlesInWrongTerrain()
                                }
                            }
                        }
                    }

                    TrialState.RUNNING -> {
                        if (!config.trial.allowPlayersLeavingArena) {
                            blockEntity.pullMobsInBorders(listOf(MinecraftClient.getInstance().player as LivingEntity))
                        }
                    }
                    else -> {}
                }
                return@BlockEntityTicker
            }

            // lord forgive me for what i'm about to do
            // update Nov 11, 2020: what the fuck
            // update Feb 26, 2021: what the fuck
            // update Jun 09, 2021: what the fuck
            if (world?.isClient == false && blockEntity.currentTrial == null) {
                blockEntity.trialToLoad?.let { trialToLoad ->
                    try {
                        val recipe = world.recipeManager?.get(trialToLoad.recipeId)?.orElse(null) as? TrialKeystoneRecipe
                        if (recipe != null) {
                            blockEntity.currentTrial = Trial(blockEntity, recipe, trialToLoad)
                        }
                    } catch (e: Exception) {
                        dev.nathanpb.dml.LOGGER.error("Failed to load trial at $pos: ${e.message}")
                    }
                    blockEntity.trialToLoad = null
                    blockEntity.sync()
                }
            }

            blockEntity.currentTrial?.let { trial ->
                val state = blockEntity.currentTrial?.state ?: TrialState.NOT_STARTED
                if (state != TrialState.NOT_STARTED && state != TrialState.FINISHED) {
                    if (state == TrialState.RUNNING) {
                        if (!config.trial.allowMobsLeavingArena) {
                            blockEntity.pullMobsInBorders(trial.getMonstersInArena())
                        }
                        if (!config.trial.allowPlayersLeavingArena) {

                            // Attempt to get the PlayerEntities of all the Players UUIDs participating the trial.
                            // If the entity list is empty but the list of Players UUIDs participating is not empty
                            // so it means that everyone is logged off, the trial should just keep "idling"

                            // If at least one PlayerEntity is found, so it checks the distance of the list of found PlayerEntities
                            // and finalizes the trial if this one player is not near

                            // If the list of Player UUIDs participating the Trial is empty
                            // it means that some kind of failure occurred and the trial is finalized too

                            val playerEntities = trial.world.getPlayersByUUID(trial.players)
                            if (trial.players.isEmpty() || (playerEntities.isNotEmpty() && !blockEntity.arePlayersAround(playerEntities))) {
                                trial.end(TrialEndReason.NO_ONE_IS_AROUND)
                            }
                        }
                    }
                    trial.tick()
                }
            }
        }
    }

    var currentTrial: Trial? = null

    var clientTrialState = TrialState.NOT_STARTED

    private val internalInventory = TrialKeystoneInventory()

    private var trialToLoad: TrialData? = null

    init {
        ModEvents.TrialEndEvent.register { trial, reason ->
            if (currentTrial == trial && !trial.world.isClient) {
                currentTrial = null
                clientTrialState = TrialState.NOT_STARTED

                if (reason == TrialEndReason.SUCCESS) {
                    internalInventory.dropAll(trial.world, pos)
                } else {
                    internalInventory.clear()
                }

                sync()
            }
        }

        ModEvents.TrialStateChanged.register {
            // Keystones do not have finished state, instead this is handled in onTrialEnd
            if (world?.isClient == false && it.state != TrialState.FINISHED) {
                clientTrialState = it.state
                sync()
            }
        }
    }

    fun createTrial(recipe: TrialKeystoneRecipe, affixes: List<TrialAffix>): Trial {
        val players = world?.getEntitiesAroundCircle(
            EntityType.PLAYER,
            pos, config.trial.arenaRadius.toDouble()
        ).orEmpty()

        if (players.isNotEmpty()) {
            return Trial(this, recipe, players.map(PlayerEntity::getUuid), affixes)
        } else throw TrialKeystoneNoPlayersAround(this)
    }

    fun startTrial(trial: Trial, key: ItemStack?) {
        world?.let { world ->
            if (currentTrial?.state in listOf(TrialState.WARMUP, TrialState.RUNNING)) {
                throw TrialKeystoneIllegalStartException(trial)
            }
            internalInventory.dropAll(world, pos)
            checkTerrain().let { wrongTerrain ->
                if (wrongTerrain.isEmpty()) {
                    currentTrial = trial
                    trial.start()

                    if (key != null) {
                        internalInventory.addStack(key)
                    }
                } else throw TrialKeystoneWrongTerrainException(this, wrongTerrain)
            }
        }
    }

    /**
     * Spawns fire particles above the blocks in wrong place
     */
    private fun spawnParticlesInWrongTerrain() {
        checkTerrain().let { wrongTerrain ->
            if (wrongTerrain.isNotEmpty()) {
                (0 .. min(wrongTerrain.size / 4, 1)).let { _ ->
                    wrongTerrain.random().let {
                        world?.addParticle(
                            ParticleTypes.FLAME,
                            true,
                            it.x + Random.nextDouble() - .1,
                            it.y + 1.0,
                            it.z + Random.nextDouble() - .1,
                            0.0, 0.0, 0.0
                        )
                    }
                }
            }
        }
    }

    /**
     * Pulls the mobs spawned by the waves back to the center
     */
    private fun pullMobsInBorders(mobs: List<LivingEntity>) {
        val posVector = pos.toVec3d()
        mobs.filter(LivingEntity::isAlive)
            .filterNot {
                config.trial.allowPlayersLeavingArena
                        && currentTrial != null
                        && (it as? SystemGlitchEntity)?.trial == currentTrial
            }.filter {
                val squaredDistance = it.squaredDistanceTo(posVector.x, posVector.y, posVector.z)
                squaredDistance in BORDERS_RANGE
            }.forEach {
                val vector = it.pos.subtract(pos.toVec3d()).multiply(-0.1)
                it.addVelocity(vector.x, vector.y, vector.z)
            }
    }

    private fun arePlayersAround(players: List<PlayerEntity>) = pos.toVec3d().let { posVec ->
        players.any {
            it.squaredDistanceTo(posVec.x, posVec.y, posVec.z) <= config.trial.arenaRadius.squared()
        }
    }

    /**
     * Checks the effective terrain and returns a list of wrong blocks
     *
     * @return the list of erroneous blocks
     */
    fun checkTerrain(): List<BlockPos> {
        return if (!config.trial.allowStartInWrongTerrain) {
            mutableListOf<BlockPos>().also { list ->
                val radInt = config.trial.arenaRadius
                (pos.x - radInt .. pos.x + radInt).forEach { x ->
                    (pos.z - radInt .. pos.z + radInt).forEach { z ->

                        // Searching for non-solid blocks bellow the circle
                        val floorPos = BlockPos(x, pos.y - 1, z)
                        if (floorPos.getSquaredDistance(pos) <= config.trial.arenaRadius.squared()) {
                            world?.getBlockState(floorPos)?.let { floorBlock ->
                                if (!floorBlock.isSideSolidFullSquare(world, floorPos, Direction.UP)) {
                                    list += floorPos
                                }
                            }
                        }

                        // Searching for non-air blocks inside the dome
                        (pos.y .. pos.y + radInt).forEach { y ->
                            val innerBlockPos = BlockPos(x, y, z)
                            if (
                                innerBlockPos != pos
                                && innerBlockPos.getSquaredDistance(pos) <= config.trial.arenaRadius.squared()
                                && world?.getBlockState(innerBlockPos)?.isAir != true
                            ) {
                                list += innerBlockPos
                            }
                        }
                    }
                }
            }.toList()
        } else emptyList()
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityTrialKeystone).internalInventory
    }

    override fun writeNbt(tag: NbtCompound?) {
        return super.writeNbt(tag).also {
            if (tag != null) {
                Inventories.writeNbt(tag, internalInventory.items())
                currentTrial?.also { trial ->
                    if (trial.state in arrayOf(TrialState.WARMUP, TrialState.RUNNING)) {
                        TrialDataSerializer().write(tag, "trial", TrialData(trial))
                    }
                }
            }
        }
    }

    override fun readNbt(tag: NbtCompound?) {
        if(tag?.contains("#c") == true) {
            fromClientTag(tag)
            if(tag.getBoolean("#c")) {
                remesh()
            }
        } else {
            super.readNbt(tag)
            if (tag != null && currentTrial == null) {
                val stacks = DefaultedList.ofSize(internalInventory.size(), ItemStack.EMPTY)
                Inventories.readNbt(tag, stacks)
                internalInventory.setStacks(stacks)
                if (tag.contains("trial")) {
                    trialToLoad = TrialDataSerializer().read(tag, "trial")
                }
            }
        }
    }

    private fun toClientTag(tag: NbtCompound) = tag.also {
        it.putString("${MOD_ID}:state", (currentTrial?.state ?: TrialState.NOT_STARTED).name)
    }

    private fun fromClientTag(tag: NbtCompound) {
        clientTrialState = tag.getString("${MOD_ID}:state").let { name ->
            if (name.isNotEmpty()) TrialState.valueOf(name) else TrialState.NOT_STARTED
        }
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = super.toInitialChunkDataNbt()
        toClientTag(nbt)
        nbt.putBoolean("#c", shouldClientRemesh) // mark client tag
        shouldClientRemesh = false
        return nbt
    }

    private fun sync(shouldRemesh : Boolean) { // Thanks, Technici4n :happy_tater:
        Preconditions.checkNotNull(world)
        check(world is ServerWorld) { "Cannot call sync() on the logical client!" }
        shouldClientRemesh = shouldRemesh or shouldClientRemesh
        (world as ServerWorld).chunkManager.markForUpdate(getPos())
    }

    private fun sync() {
        sync(true)
    }

    private fun remesh() {
        Preconditions.checkNotNull(world)
        check(world is ClientWorld) { "Cannot call remesh() on the server!" }
        world!!.updateListeners(pos, null, null, 0)
    }

    private var shouldClientRemesh = true
}