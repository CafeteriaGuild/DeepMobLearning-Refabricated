/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.listener

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.event.EndermanTeleportCallback
import dev.nathanpb.dml.event.WorldExplosionCallback
import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.TrialWave
import dev.nathanpb.dml.utils.toBlockPos
import dev.nathanpb.dml.utils.toVec3i
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion

class TrialGriefPrevention :
    AttackBlockCallback,
    UseBlockCallback,
    WorldExplosionCallback,
    EndermanTeleportCallback
{

    private fun isInArea(trialPos: BlockPos, pos: BlockPos): Boolean {
        return trialPos.getSquaredDistance(pos.toVec3i()) <= BlockEntityTrialKeystone.EFFECTIVE_AREA_RADIUS_SQUARED
    }

    private fun isBlockProtected(pos: BlockPos): Boolean {
        return Trial.RUNNING_TRIALS.any {
            isInArea(it.pos, pos) && pos.y >= it.pos.y - 1
        }
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, pos: BlockPos, direction: Direction): ActionResult {
        return if (!world.isClient && isBlockProtected(pos)) {
            ActionResult.FAIL
        } else ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        val posOfPlacedBlock = hitResult.blockPos.offset(hitResult.side)
        return if (!world.isClient && isBlockProtected(posOfPlacedBlock)) {
            ActionResult.FAIL
        } else ActionResult.PASS
    }

    override fun explode(
        world: World,
        entity: Entity?,
        damageSource: DamageSource?,
        pos: BlockPos,
        power: Float,
        createFire: Boolean,
        destructionType: Explosion.DestructionType?
    ): ActionResult {
        if (!world.isClient && destructionType != Explosion.DestructionType.NONE && isBlockProtected(pos)) {
            world.createExplosion(entity, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), power, createFire, Explosion.DestructionType.NONE)
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }

    override fun onEndermanTeleport(entity: EndermanEntity, pos: Vec3d): ActionResult {
        val belongsToTrial = Trial.RUNNING_TRIALS.any { trial ->
            trial.waves
                .asSequence()
                .filter(TrialWave::isSpawned)
                .filter { !it.isFinished() }
                .map(TrialWave::spawnedEntities)
                .flatten()
                .any(entity::equals)
        }
        if (belongsToTrial && !isBlockProtected(pos.toBlockPos())) {
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }
}
