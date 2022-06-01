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

package dev.nathanpb.dml.entity.renderer

import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.entity.model.SystemGlitchModel
import dev.nathanpb.dml.identifier
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayer

class SystemGlitchRenderer(context: EntityRendererFactory.Context, layer: EntityModelLayer) :
    MobEntityRenderer<SystemGlitchEntity, SystemGlitchModel>(
        context, SystemGlitchModel(context.getPart(layer)), 0.5F
    )
{

    override fun getTexture(entity: SystemGlitchEntity) = identifier("textures/entity/system_glitch.png")

    init {
        addFeature(HeadFeatureRenderer(this, context.modelLoader, 1.15F, 1.05F, 1.15F, null)) // TODO [1.19] check if null HeldItemRenderer causes issues
    }
}
