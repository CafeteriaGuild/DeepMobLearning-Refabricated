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
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.SOUL_VISION_EFFECT
import dev.nathanpb.dml.modular_armor.SOUL_VISION_KEYBINDING
import dev.nathanpb.dml.modular_armor.core.EffectStackOption
import dev.nathanpb.dml.modular_armor.core.ModularEffect
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.event.SoulVisionEffectRequestedEvent
import dev.nathanpb.dml.modular_armor.net.C2S_SOUL_VISION_REQUESTED
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult

class SoulVisionEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("soul_vision"),
    EntityCategory.GHOST,
    config.glitchArmor.costs::soulVision
) {

    override val name = TranslatableText("effect.dml-refabricated.soul_vision")

    override fun registerEvents() {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
                if (MinecraftClient.getInstance().world != null && SOUL_VISION_KEYBINDING.isPressed) {
                    SOUL_VISION_KEYBINDING.isPressed = false
                    ClientPlayNetworking.send(C2S_SOUL_VISION_REQUESTED, PacketByteBuf(Unpooled.buffer()))
                }
            })
        }

        SoulVisionEffectRequestedEvent.register { player ->
            if (!player.world.isClient && !player.hasStatusEffect(SOUL_VISION_EFFECT)) {
                ModularEffectContext.from(player)
                    .run(EffectStackOption.RANDOMIZE.apply)
                    .any { context ->
                        attemptToApply(context, ModularEffectTriggerPayload.EMPTY) { _, _ ->
                            player.addStatusEffect(StatusEffectInstance(SOUL_VISION_EFFECT, 16*20))
                        }.result == ActionResult.SUCCESS
                    }
            }
        }
    }

    override fun acceptTier(tier: DataModelTier) = true

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), 1.0, EntityAttributeModifier.Operation.ADDITION)
    }

}
