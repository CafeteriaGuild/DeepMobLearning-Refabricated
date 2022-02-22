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

package dev.nathanpb.dml.modular_armor.effects

import dev.nathanpb.dml.config
import dev.nathanpb.dml.entityCategory.EntityCategory
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.TELEPORT_KEYBINDING
import dev.nathanpb.dml.modular_armor.core.ModularEffect
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.event.ModularArmorEvents
import dev.nathanpb.dml.modular_armor.net.C2S_TELEPORT_EFFECT_REQUESTED
import dev.nathanpb.dml.modular_armor.payload.TeleportEffectPayload
import dev.nathanpb.dml.utils.toVec3d
import dev.nathanpb.dml.utils.writeVec3d
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.RaycastContext

class TeleportEffect : ModularEffect<TeleportEffectPayload>(
    identifier("teleport"),
    EntityCategory.END,
    config.glitchArmor.costs::teleports
) {

    @Environment(EnvType.CLIENT)
    private var clientLastUse = 0L

    override fun registerEvents() {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
                if (TELEPORT_KEYBINDING.isPressed) {
                    client.player?.let { player ->
                        val world = player.world
                        if (world.time - clientLastUse >= 20) {
                            clientLastUse = world.time

                            PacketByteBuf(Unpooled.buffer()).apply {
                                writeVec3d(player.pos.add(0.0, player.getEyeHeight(player.pose).toDouble(), 0.0))
                                writeVec3d(player.rotationVecClient)
                            }.let { buf ->
                                ClientPlayNetworking.send(C2S_TELEPORT_EFFECT_REQUESTED, buf)
                            }
                        }
                    }
                }
            })
        }

        ModularArmorEvents.TeleportEffectRequestedEvent.register { player, pos, looking ->
            if (!player.world.isClient) {
                return@register ModularEffectContext.from(player)
                    .any {
                        attemptToApply(it, TeleportEffectPayload(pos, looking)) { _, _ ->
                            val lookingAt = looking.multiply(10.0).add(pos)
                            val hitResult = player.world.raycast(RaycastContext(
                                pos,
                                lookingAt,
                                RaycastContext.ShapeType.OUTLINE,
                                RaycastContext.FluidHandling.NONE,
                                player
                            ))

                            val positionToTeleport = if (hitResult.type == HitResult.Type.MISS) {
                                lookingAt.add(0.0, -1.0 ,0.0)
                            } else {
                                hitResult.blockPos.offset(hitResult.side).toVec3d().run {
                                    if (hitResult.side != Direction.UP) {
                                        add(0.0, -1.0, 0.0)
                                    } else this
                                }.add(0.5, 0.0, 0.5)
                            }

                            positionToTeleport.apply {
                                player.requestTeleport(x, y, z)
                            }

                        }.result == ActionResult.SUCCESS
                    }
            }
            return@register false
        }
    }

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier >= DataModelTier.SUPERIOR
    }

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), 1.0, EntityAttributeModifier.Operation.ADDITION)
    }

}
