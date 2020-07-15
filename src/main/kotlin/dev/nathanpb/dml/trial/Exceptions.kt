/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import net.minecraft.util.math.BlockPos

open class TrialKeystoneException(val keystone: BlockEntityTrialKeystone): Exception()
open class TrialException(val trial: Trial): Exception()

class TrialKeystoneIllegalStartException(trial: Trial) : TrialException(trial) {
    override val message = "Could not start the Trial at ${trial.pos}. Is it finished or running?"
}

class TrialKeystoneIllegalEndException(trial: Trial) : TrialException(trial) {
    override val message = "Could not end the trial at ${trial.pos}. Is it already finished or not started?"
}

class TrialKeystoneWrongTerrainException(entity: BlockEntityTrialKeystone, val pos: List<BlockPos>) : TrialKeystoneException(entity) {
    override val message = "The trial keystone cannot be started due wrong terrain"
}

class TrialKeystoneNoPlayersAround(entity: BlockEntityTrialKeystone) : TrialKeystoneException(entity) {
    override val message = "Could not start the Trial because no players are around"
}
