package com.matthias.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.*
import com.matthias.ecs.component.PlayerComponent
import com.matthias.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import java.lang.Float.max
import kotlin.math.min

private val LOG = logger<DebugSystem>()

private const val WINDOW_INFO_UPDATE_RATE = 0.25f
private const val ADD_SHIELD_AMOUNT = 25f
private const val REMOVE_SHIELD_AMOUNT = 25f


class DebugSystem : IntervalIteratingSystem(allOf(TransformComponent::class, PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {

    init {
        setProcessing(true)
    }

    override fun processEntity(entity: Entity) {
        val player = entity[PlayerComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!

        when {
            Gdx.input.isKeyPressed(NUM_1) -> killPlayer(player, transform)
            Gdx.input.isKeyPressed(NUM_2) -> addShield(player)
            Gdx.input.isKeyPressed(NUM_3) -> removeShield(player)
        }

        Gdx.graphics.setTitle("DM Debug - pos:${transform.position}, life:${player.life}, shield:${player.shield}")
    }

    private fun killPlayer(player: PlayerComponent, transform: TransformComponent) {
        LOG.info { "Killing player" }
        transform.position.y = 1f
        player.life = 1f
        player.shield = 0f
    }

    private fun addShield(player: PlayerComponent) {
        LOG.info { "Adding $ADD_SHIELD_AMOUNT shield" }
        player.shield = min(player.maxShield, player.shield + ADD_SHIELD_AMOUNT)
    }

    private fun removeShield(player: PlayerComponent) {
        LOG.info { "Removing $REMOVE_SHIELD_AMOUNT shield" }
        player.shield = max(0f, player.shield - REMOVE_SHIELD_AMOUNT)
    }
}