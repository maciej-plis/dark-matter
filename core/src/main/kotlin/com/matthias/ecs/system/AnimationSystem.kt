package com.matthias.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import com.matthias.ecs.component.Animation2D
import com.matthias.ecs.component.AnimationComponent
import com.matthias.ecs.component.AnimationType
import com.matthias.ecs.component.AnimationType.NONE
import com.matthias.ecs.component.GraphicComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import java.util.*

private val LOG = logger<AnimationSystem>()

class AnimationSystem(private val atlas: TextureAtlas) :
    IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {

    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }


    override fun entityAdded(entity: Entity) {
        val animation = entity[AnimationComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!

        animation.animation = getAnimation(animation.type)
        val frame = animation.animation.getKeyFrame(animation.stateTime)
        graphic.setSpriteRegion(frame)
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animation = entity[AnimationComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!

        if (animation.type == NONE) {
            LOG.error { "No type specified for animation component $animation for $entity" }
            return
        }

        if (animation.type == animation.animation.type) {
            animation.stateTime += deltaTime
        } else {
            animation.stateTime = 0f
            animation.animation = getAnimation(animation.type)
        }

        val frame = animation.animation.getKeyFrame(animation.stateTime)
        graphic.setSpriteRegion(frame)
    }

    private fun getAnimation(type: AnimationType): Animation2D {

        if (animationCache[type] == null) {
            var regions = atlas.findRegions(type.atlasKey)
            if (regions.isEmpty) {
                LOG.error { "No regions found for ${type.atlasKey}" }
                regions = atlas.findRegions("error")
                if (regions.isEmpty) throw GdxRuntimeException("Error region is missing in the atlas")
            }
            LOG.debug { "Adding animation of type $type with ${regions.size} regions to cache" }
            animationCache[type] = Animation2D(type, regions, type.playMode, type.speedRate)
        }

        return animationCache[type]!!
    }
}