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

import dev.nathanpb.dml.armor.modular.core.ModularEffect;
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext;
import dev.nathanpb.dml.armor.modular.core.ModularEffectRegistry;
import dev.nathanpb.dml.armor.modular.effects.PlentyEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

    @Inject(at = @At("HEAD"), method = "getDeathMessage", cancellable = true)
    public void getDeathMessage(LivingEntity entity, CallbackInfoReturnable<Text> cir) {

        if (entity instanceof PlayerEntity && this.equals(DamageSource.STARVE)) {
            Optional<ModularEffect<?>> effectOpt = ModularEffectRegistry.Companion
                    .getINSTANCE()
                    .getAll()
                    .stream()
                    .filter((it) -> it instanceof PlentyEffect)
                    .findFirst();

            if (effectOpt.isPresent()) {
                ModularEffect<?> effect = effectOpt.get();
                boolean any = ModularEffectContext.Companion.from((PlayerEntity) entity)
                        .stream()
                        .anyMatch((context) -> effect.getCategory() == context.getDataModel().getCategory() && effect.acceptTier(context.getTier()));
                if (any) {
                    cir.setReturnValue(new TranslatableText(
                            "death.dml-refabricated.starvedWithPlenty",
                            entity.getDisplayName()
                    ));
                    cir.cancel();
                }
            }

        }
    }
}
