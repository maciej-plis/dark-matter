package com.matthias.screen

import com.badlogic.ashley.core.Engine
import com.matthias.DarkMatterGame
import com.matthias.UNIT_SCALE
import com.matthias.V_WIDTH
import com.matthias.assets.MusicAsset
import com.matthias.ecs.component.*
import com.matthias.ecs.component.AnimationType.DARK_MATTER
import com.matthias.ecs.component.AnimationType.FIRE
import com.matthias.ecs.system.DAMAGE_AREA_HEIGHT
import com.matthias.event.GameEvent
import com.matthias.event.GameEvent.PlayerDeath
import com.matthias.event.GameEventListener
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min

private val LOG = logger<GameScreen>()

private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(game: DarkMatterGame, val engine: Engine = game.engine) : ScreenBase(game), GameEventListener {

    override fun show() {
        LOG.debug { "First Screen is shown!" }

        LOG.debug { "High-Score: ${preferences["high-score", 0f]}" }

        gameEventManager.addListener(PlayerDeath::class, this)

        audioService.play(MusicAsset.GAME)
        spawnPlayer()

        engine.entity {
            with<TransformComponent> { size.set(V_WIDTH, DAMAGE_AREA_HEIGHT) }
            with<AnimationComponent> { type = DARK_MATTER }
            with<GraphicComponent>()
        }
    }

    override fun hide() {
        gameEventManager.removeListener(this)
    }

    private fun spawnPlayer() {
        val player = game.engine.entity {
            with<TransformComponent> { setInitialPosition(4.5f, 8f, -1f) }
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
            with<MoveComponent>()
        }

        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = player
                offset.set(1f * UNIT_SCALE, -6f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> { type = FIRE }
        }
    }

    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
        audioService.update()
    }

    override fun dispose() {
    }

    override fun onEvent(event: GameEvent) {
        if (event is PlayerDeath) {
            LOG.debug { "Player died with a distance of ${event.distance}" }
            if (event.distance > preferences["high-score", 0f]) {
                preferences.flush {
                    this["high-score"] = event.distance
                }
            }
            spawnPlayer()
        }
    }
}