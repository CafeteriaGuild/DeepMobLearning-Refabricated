package dev.nathanpb.dml.mixin;/*
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
import dev.nathanpb.dml.accessor.IFlightBurnoutManagerAccessor;
import dev.nathanpb.dml.accessor.IUndyingCooldown;
import dev.nathanpb.dml.armor.modular.cooldown.FlightBurnoutManager;
import dev.nathanpb.dml.event.context.EventsKt;
import dev.nathanpb.dml.event.context.LivingEntityDamageContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IFlightBurnoutManagerAccessor, IUndyingCooldown {

    private FlightBurnoutManager dmlRefFlightManager;
    private Long dmlRefUndyingLastUsage;

    @Override
    public FlightBurnoutManager getDmlFlightBurnoutManager() {
        if (dmlRefFlightManager == null) {
            dmlRefFlightManager = new FlightBurnoutManager((PlayerEntity) (Object) this);
        }
        return dmlRefFlightManager;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;applyEnchantmentsToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), method = "applyDamage")
    private float applyDamage(DamageSource source, float amount) {
        PlayerEntity dis = (PlayerEntity) (Object) this;
        return EventsKt.getLivingEntityDamageEvent()
            .invoker()
            .invoke(new LivingEntityDamageContext(dis, source, amount))
            .getDamage();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        getDmlFlightBurnoutManager().tick();
        EventsKt.getPlayerEntityTickEvent()
            .invoker()
            .invoke((PlayerEntity) (Object) this);
    }

    @Override
    public @Nullable Long getDmlRefUndyingLastUsage() {
        return dmlRefUndyingLastUsage;
    }

    @Override
    public void setDmlRefUndyingLastUsage(@Nullable Long time) {
        dmlRefUndyingLastUsage = time;
    }


    @Inject(at = @At("RETURN"), method = "readCustomDataFromTag")
    public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(DeepMobLearningKt.MOD_ID + ":undyingLastUsage")) {
            setDmlRefUndyingLastUsage(tag.getLong(DeepMobLearningKt.MOD_ID + ":undyingLastUsage"));
        } else {
            setDmlRefUndyingLastUsage(null);
        }
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToTag")
    public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        if (getDmlRefUndyingLastUsage() == null) {
            tag.remove(DeepMobLearningKt.MOD_ID + ":undyingLastUsage");
        } else {
            tag.putLong(DeepMobLearningKt.MOD_ID + ":undyingLastUsage", getDmlRefUndyingLastUsage());
        }
    }
}
