package dev.nathanpb.dml

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */


class NotDeepLearnerException : Exception("The current item stack is not a valid Deep Learner")
class NotDataModelException : Exception("The current item stack is not a valid Data Model")
class InvalidTrialKeyBase : IllegalArgumentException("The given argument cannot be converted into TrialKeyData")

open class TrialKeystoneException(blockEntity: BlockEntityTrialKeystone): Exception()
class TrialKeystoneAlreadyRunningException(
    blockEntity: BlockEntityTrialKeystone
) : TrialKeystoneException(blockEntity) {
    override val message = "The Trial Keystone at ${blockEntity.pos} is already running"
}
class TrialKeystoneNotRunningException(
    blockEntity: BlockEntityTrialKeystone
) : TrialKeystoneException(blockEntity) {
    override val message = "The Trial Keystone at ${blockEntity.pos} is not running"
}
