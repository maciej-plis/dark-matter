package com.matthias.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import com.matthias.ecs.component.PowerUpType.NONE
import ktx.collections.GdxSet
import ktx.collections.getOrPut
import kotlin.reflect.KClass

typealias GameEventType = KClass<out GameEvent>

sealed interface GameEvent {
    object PlayerDeath : GameEvent {
        var distance = 0f

        override fun toString() = "PlayerDeath(distance=$distance)"
    }

    object CollectPowerUp : GameEvent {
        lateinit var player: Entity
        var type = NONE

        override fun toString() = "GameEventCollectPowerUp(player=$player, type=$type)"
    }

    object PlayerHit : GameEvent {
        lateinit var player: Entity
        var life = 0f
        var maxLife = 0f

        override fun toString() = "PlayerHit(player=$player, life=$life, maxLife=$maxLife)"
    }
}

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {

    private val listeners = ObjectMap<GameEventType, GdxSet<GameEventListener>>()

    fun addListener(type: GameEventType, listener: GameEventListener) {
        listeners.getOrPut(type, { GdxSet() }).add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) {
        listeners[type]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values().forEach { it.remove(listener) }
    }

    fun dispatchEvent(event: GameEvent) {
        listeners[event::class].forEach { it.onEvent(event) }
    }
}