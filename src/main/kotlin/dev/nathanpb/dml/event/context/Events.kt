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

package dev.nathanpb.dml.event.context

import com.mojang.datafixers.util.Pair
import dev.nathanpb.dml.utils.event
import dev.nathanpb.dml.utils.firstNonNullMapping
import dev.nathanpb.dml.utils.firstOrNullMapping
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d

val PlayerEntityDamageEvent = event<(PlayerEntityDamageContext)->PlayerEntityDamageContext?> { listeners ->
    { context: PlayerEntityDamageContext ->
        listeners.fold(context) { acc, listener ->
            listener(acc) ?: acc
        }
    }
}

// Doing it just with PlayerEntity to prevent server overload
val PlayerEntityTickEvent = event<(PlayerEntity)->Unit> { listeners ->
    { entity ->
        listeners.forEach {
            it(entity)
        }
    }
}

val PlayerStareEndermanEvent = event<(PlayerEntity)->ActionResult> { listeners ->
    { entity ->
        val succeeded = listeners.none {
            it(entity) == ActionResult.FAIL
        }

        if (succeeded) ActionResult.SUCCESS else ActionResult.FAIL
    }
}

val FindTotemOfUndyingCallback = event<(PlayerEntity)->ItemStack?> { listeners ->
    { entity ->
        listeners.toList().firstNonNullMapping {
            it(entity)
        }
    }
}

val TeleportEffectRequestedEvent = event<(PlayerEntity, Vec3d, Vec3d)->Boolean> { listeners ->
    { player, pos, rotation ->
        listeners.any {
            it(player, pos, rotation)
        }
    }
}

val CanTargetEntityEvent = event<(MobEntity, LivingEntity)->ActionResult> { listeners ->
    { mob, target ->
        listeners.toList().firstOrNullMapping({ it(mob, target) }) {
            it == ActionResult.FAIL
        } ?: ActionResult.PASS
    }
}

val PlayerTakeHungerEvent = event<(PlayerEntity, Int)->Int> { listeners ->
    { player, amount ->
        listeners.toList().fold(amount) { acc, function ->
            if (acc > 0) {
                function(player, acc)
            } else acc
        }
    }
}

val FoodStatusEffectsCallback = event<(LivingEntity, ItemStack, List<Pair<StatusEffectInstance, Float>>)->List<Pair<StatusEffectInstance, Float>>> { listeners ->
    { entity, stack, effects ->
        listeners.toList().fold(effects) { acc, function ->
            function(entity, stack, acc)
        }
    }
}

val LivingEntityEatEvent = event<(LivingEntity, ItemStack)->Unit> { listeners ->
    { entity, stack ->
        listeners.forEach {
            it(entity, stack)
        }
    }
}
