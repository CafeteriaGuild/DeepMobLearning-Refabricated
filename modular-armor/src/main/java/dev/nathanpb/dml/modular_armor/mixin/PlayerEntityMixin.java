package dev.nathanpb.dml.modular_armor.mixin;/*
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

import dev.nathanpb.dml.DeepMobLearningKt;
import dev.nathanpb.dml.modular_armor.accessor.IFlightBurnoutManagerAccessor;
import dev.nathanpb.dml.modular_armor.accessor.IUndyingCooldown;
import dev.nathanpb.dml.modular_armor.cooldown.FlightBurnoutManager;
import dev.nathanpb.safer.Safer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Override
    public @Nullable Long getDmlRefUndyingLastUsage() {
        return dmlRefUndyingLastUsage;
    }

    @Override
    public void setDmlRefUndyingLastUsage(@Nullable Long time) {
        dmlRefUndyingLastUsage = time;
    }


    @Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
    public void readCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
        Safer.run(() -> {
            if (tag.contains(DeepMobLearningKt.MOD_ID + ":undyingLastUsage")) {
                setDmlRefUndyingLastUsage(tag.getLong(DeepMobLearningKt.MOD_ID + ":undyingLastUsage"));
            } else {
                setDmlRefUndyingLastUsage(null);
            }
        });
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
    public void writeCustomDataToTag(NbtCompound tag, CallbackInfo ci) {
        Safer.run(() -> {
            if (getDmlRefUndyingLastUsage() == null) {
                tag.remove(DeepMobLearningKt.MOD_ID + ":undyingLastUsage");
            } else {
                tag.putLong(DeepMobLearningKt.MOD_ID + ":undyingLastUsage", getDmlRefUndyingLastUsage());
            }
        });
    }
}
