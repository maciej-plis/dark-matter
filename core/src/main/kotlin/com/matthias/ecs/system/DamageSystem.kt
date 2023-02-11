package com.matthias.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.matthias.ecs.component.PlayerComponent
import com.matthias.ecs.component.RemoveComponent
import com.matthias.ecs.component.TransformComponent
import com.matthias.event.GameEvent.PlayerDeath
import com.matthias.event.GameEvent.PlayerHit
import com.matthias.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

const val DAMAGE_AREA_HEIGHT = 2f
private const val DAMAGE_PER_SECOND = 25f
private const val DEATH_EXPLOSION_DURATION = 0.9f

class DamageSystem(private val gameEventManager: GameEventManager) :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun processEntity(playerEntity: Entity, deltaTime: Float) {
        val transform = playerEntity[TransformComponent.mapper]!!
        val player = playerEntity[PlayerComponent.mapper]!!

        if (transform.position.y > DAMAGE_AREA_HEIGHT) return

        var damage = DAMAGE_PER_SECOND * deltaTime

        if (player.shield > 0f) {
            val blockAmount = player.shield
            player.shield = max(0f, player.shield - damage)
            damage -= blockAmount
        }

        if (damage <= 0) return

        player.life -= damage
        gameEventManager.dispatchEvent(PlayerHit.apply {
            this.player = playerEntity
            life = player.life
            maxLife = player.maxLife
        })

        if (player.life <= 0) {
            gameEventManager.dispatchEvent(PlayerDeath.apply { this.distance = player.distance })
            playerEntity.addComponent<RemoveComponent>(engine) {
                delay = DEATH_EXPLOSION_DURATION
            }
        }
    }
}