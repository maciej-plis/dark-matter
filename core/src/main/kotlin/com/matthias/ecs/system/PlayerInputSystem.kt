package com.matthias.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.matthias.ecs.component.FacingComponent
import com.matthias.ecs.component.FacingDirection.*
import com.matthias.ecs.component.PlayerComponent
import com.matthias.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerInputSystem(
    private val viewport: Viewport
) : IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, FacingComponent::class).get()) {

    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!

        viewport.unproject(tmpVec.set(Gdx.input.x.toFloat(), 0f))
        val diffX = tmpVec.x - transform.position.x - transform.size.x * 0.5f

        facing.direction = when {
            diffX < transform.size.x * -0.5f -> LEFT
            diffX > transform.size.x * 0.5f -> RIGHT
            else -> DEFAULT
        }
    }
}