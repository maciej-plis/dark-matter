package com.matthias.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.matthias.ecs.component.AttachComponent
import com.matthias.ecs.component.GraphicComponent
import com.matthias.ecs.component.RemoveComponent
import com.matthias.ecs.component.TransformComponent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AttachSystem : IteratingSystem(allOf(AttachComponent::class, TransformComponent::class, GraphicComponent::class).get()),
    EntityListener {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach { entity ->
            val attach = entity[AttachComponent.mapper]!!
            if (attach.entity == removedEntity) {
                entity.addComponent<RemoveComponent>(engine)
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attach = entity[AttachComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!

        attach.entity[TransformComponent.mapper]?.let { entityTransform ->
            transform.interpolatedPosition.set(
                entityTransform.interpolatedPosition.x + attach.offset.x,
                entityTransform.interpolatedPosition.y + attach.offset.y,
                transform.position.z
            )
        }

        attach.entity[GraphicComponent.mapper]?.let { entityGraphic ->
            graphic.sprite.setAlpha(entityGraphic.sprite.color.a)

        }
    }
}