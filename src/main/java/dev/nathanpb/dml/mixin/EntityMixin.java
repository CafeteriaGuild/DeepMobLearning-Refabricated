package dev.nathanpb.dml.mixin;

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

import dev.nathanpb.dml.DeepMobLearningKt;
import dev.nathanpb.dml.entity.effect.StatusEffectsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public World world;

    @Inject(at = @At("RETURN"), method = "isGlowing", cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (this.world.isClient && !cir.getReturnValue()) {
            Entity dis = (Entity) (Object) this;
            if (dis instanceof LivingEntity) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null && player.hasStatusEffect(StatusEffectsKt.getSOUL_VISION_EFFECT())) {
                    if (player.distanceTo((Entity) (Object) this) <= DeepMobLearningKt.getConfig().getGlitchArmor().getSoulVisionRange()) {
                        cir.setReturnValue(true);
                        cir.cancel();
                    }
                }
            }
        }
    }
}
