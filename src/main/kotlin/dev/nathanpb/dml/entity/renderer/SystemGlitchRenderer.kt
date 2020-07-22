/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.entity.renderer

import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.entity.model.SystemGlitchModel
import dev.nathanpb.dml.identifier
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer

class SystemGlitchRenderer(dispatcher: EntityRenderDispatcher) : MobEntityRenderer<SystemGlitchEntity, SystemGlitchModel>(dispatcher, SystemGlitchModel(), 0.5F) {
    override fun getTexture(entity: SystemGlitchEntity) = identifier("textures/entity/system_glitch.png")
}
