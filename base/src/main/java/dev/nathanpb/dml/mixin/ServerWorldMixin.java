package dev.nathanpb.dml.mixin;

/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

import dev.nathanpb.dml.event.VanillaEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(
            at = @At("HEAD"),
            method = "createExplosion",
            cancellable = true
    )
    public void explode(
            Entity entity,
            DamageSource damageSource,
            ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSource,
            CallbackInfoReturnable<Explosion> ci
    ) {
        if (power > 0F) {
            World world = (World) (Object) this;
            BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
            ActionResult result = VanillaEvents.INSTANCE.getWorldExplosionEvent()
                .invoker()
                .invoke(world, entity, damageSource, behavior, pos, power, createFire, explosionSource);

            if (result == ActionResult.FAIL) {
                Explosion explosion = new Explosion(world, entity, damageSource, behavior, x, y, z, power, createFire, Explosion.DestructionType.KEEP);
                ci.setReturnValue(explosion);
                ci.cancel();
            }
        }
    }
}
