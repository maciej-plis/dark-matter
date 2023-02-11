package com.matthias.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.matthias.ecs.component.FacingComponent
import com.matthias.ecs.component.FacingDirection.*
import com.matthias.ecs.component.GraphicComponent
import com.matthias.ecs.component.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).get()), EntityListener {

    private var lastDirection = DEFAULT

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        val graphic = entity[GraphicComponent.mapper]!!
        graphic.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!

        if (facing.direction == lastDirection && graphic.sprite.texture != null) return

        lastDirection = facing.direction
        val region = when (facing.direction) {
            LEFT -> leftRegion
            RIGHT -> rightRegion
            DEFAULT -> defaultRegion
        }
        graphic.setSpriteRegion(region)
    }

}