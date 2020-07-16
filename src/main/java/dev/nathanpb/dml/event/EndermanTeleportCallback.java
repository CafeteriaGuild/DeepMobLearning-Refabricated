package dev.nathanpb.dml.event;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public interface EndermanTeleportCallback {

    Event<EndermanTeleportCallback> EVENT = EventFactory.createArrayBacked(EndermanTeleportCallback.class, listeners ->
        (entity, pos) -> {
            for(EndermanTeleportCallback listener : listeners) {
                if (listener.onEndermanTeleport(entity, pos) == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        }
    );

    ActionResult onEndermanTeleport(EndermanEntity entity, Vec3d pos);
}
