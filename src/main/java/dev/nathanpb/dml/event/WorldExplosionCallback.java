package dev.nathanpb.dml.event;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public interface WorldExplosionCallback {
    Event<WorldExplosionCallback> EVENT = EventFactory.createArrayBacked(WorldExplosionCallback.class, listeners ->
        (World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, BlockPos pos, float power, boolean createFire, Explosion.DestructionType destructionType) -> {
            for (WorldExplosionCallback listener : listeners) {
                ActionResult result = listener.explode(world, entity, damageSource, behavior, pos, power, createFire, destructionType);
                if (result != ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        }
    );

    ActionResult explode(World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, BlockPos pos, float power, boolean createFire, Explosion.DestructionType destructionType);
}
