package dev.nathanpb.dml.mixin;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.accessor.ITrialWorldPersistenceAccessor;
import dev.nathanpb.dml.trial.Trial;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public class WorldMixin implements ITrialWorldPersistenceAccessor {
    private final List<Trial> dmlRefabricatedRunningTrials = new ArrayList<>();

    @Override
    public List<Trial> getRunningTrials() {
        return dmlRefabricatedRunningTrials;
    }
}
