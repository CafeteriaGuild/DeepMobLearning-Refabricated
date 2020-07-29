package dev.nathanpb.dml.event;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.trial.Trial;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public interface TrialWaveSpawnCallback {
    Event<TrialWaveSpawnCallback> EVENT = EventFactory.createArrayBacked(TrialWaveSpawnCallback.class, listeners ->
        (trial, waveEntities) -> {
            for (TrialWaveSpawnCallback listener : listeners) {
                listener.onWaveSpawned(trial, waveEntities);
            }
        }
    );

    void onWaveSpawned(Trial trial, List<LivingEntity> waveEntities);
}
