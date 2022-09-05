package dev.nathanpb.dml.event

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import net.minecraft.world.explosion.ExplosionBehavior

object VanillaEvents {
    data class LivingEntityDamageContext(val entity: LivingEntity, val source: DamageSource, val damage: Float)

    val LivingEntityDamageEvent = event<(LivingEntityDamageContext)->LivingEntityDamageContext?> { listeners ->
        { context: LivingEntityDamageContext ->
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

    val PlayerTakeHungerEvent = event<(PlayerEntity, Int)->Int> { listeners ->
        { player, amount ->
            listeners.toList().fold(amount) { acc, function ->
                if (acc > 0) {
                    function(player, acc)
                } else acc
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

    val BowShotEvent = event<(LivingEntity, ItemStack)->Unit> { listeners ->
        { entity, stack ->
            listeners.forEach {
                it(entity, stack)
            }
        }
    }

    val CrossbowReloadedEvent = event<(LivingEntity, ItemStack)->Unit> { listeners ->
        { entity, stack ->
            listeners.forEach {
                it(entity, stack)
            }
        }
    }

    val EndermanTeleportEvent = event<(EndermanEntity, Vec3d)-> ActionResult> { listeners ->
        { entity, pos ->
            val shouldFail = listeners.any { it.invoke(entity, pos) == ActionResult.FAIL }
            if (shouldFail) ActionResult.FAIL else ActionResult.PASS
        }
    }

    val WorldExplosionEvent = event<(World, Entity?, DamageSource?, behavior: ExplosionBehavior?, pos: BlockPos, power: Float, createFire: Boolean, Explosion.DestructionType)-> ActionResult> { listeners ->
        { world, entity, damageSource, behavior, pos, power, createFire, destructionType ->
            val shouldFail = listeners.any {
                it.invoke(world, entity, damageSource, behavior, pos, power, createFire, destructionType) == ActionResult.FAIL
            }

            if (shouldFail) ActionResult.FAIL else ActionResult.PASS
        }
    }
}