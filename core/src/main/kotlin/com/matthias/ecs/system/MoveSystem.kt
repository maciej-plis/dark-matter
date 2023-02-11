package com.matthias.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.MathUtils.lerp
import com.matthias.V_HEIGHT
import com.matthias.V_WIDTH
import com.matthias.ecs.component.*
import com.matthias.ecs.component.FacingDirection.*
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val UPDATE_RATE = 1 / 25f
private const val HOR_ACCELERATION = 16.5f
private const val VER_ACCELERATION = 2.25f
private const val MAX_VER_NEG_PLAYER_SPEED = -0.75f
private const val MAX_VER_POS_PLAYER_SPEED = 5f
private const val MAX_HOR_SPEED = 5.5f

class MoveSystem : IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).exclude(RemoveComponent::class).get()) {

    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
            accumulator -= UPDATE_RATE

            entities.forEach { entity ->
                entity[TransformComponent.mapper]!!.let { transform ->
                    transform.prevPosition.set(transform.position)
                }
            }

            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.mapper]!!.let { transform ->
                transform.interpolatedPosition.set(
                    lerp(transform.prevPosition.x, transform.position.x, alpha),
                    lerp(transform.prevPosition.y, transform.position.y, alpha),
                    transform.position.z
                )
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val move = entity[MoveComponent.mapper]!!
        val player = entity[PlayerComponent.mapper]

        if (player != null) {
            entity[FacingComponent.mapper]?.let { facing -> movePlayer(transform, move, player, facing, deltaTime) }
        } else {
            moveEntity(transform, move, deltaTime)
        }
    }

    private fun movePlayer(
        transform: TransformComponent,
        move: MoveComponent,
        player: PlayerComponent,
        facing: FacingComponent,
        deltaTime: Float
    ) {
        move.speed.x = when (facing.direction) {
            LEFT -> min(0f, move.speed.x - HOR_ACCELERATION * deltaTime)
            RIGHT -> max(0f, move.speed.x + HOR_ACCELERATION * deltaTime)
            DEFAULT -> 0f
        }
        move.speed.x = clamp(move.speed.x, -MAX_HOR_SPEED, MAX_HOR_SPEED)
        move.speed.y = clamp(move.speed.y - VER_ACCELERATION * deltaTime, MAX_VER_NEG_PLAYER_SPEED, MAX_VER_POS_PLAYER_SPEED)

        val oldY = transform.position.y
        moveEntity(transform, move, deltaTime)
        player.distance += abs(transform.position.y - oldY)
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        transform.position.x = clamp(transform.position.x + move.speed.x * deltaTime, 0f, V_WIDTH - transform.size.x)
        transform.position.y = clamp(transform.position.y + move.speed.y * deltaTime, 1f, V_HEIGHT - transform.size.y + 1f)
    }
}