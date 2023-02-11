package com.matthias.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.matthias.ecs.component.GraphicComponent
import com.matthias.ecs.component.PlayerComponent
import com.matthias.ecs.component.PowerUpType.SPEED_1
import com.matthias.ecs.component.PowerUpType.SPEED_2
import com.matthias.ecs.component.RemoveComponent
import com.matthias.ecs.component.TransformComponent
import com.matthias.event.GameEvent
import com.matthias.event.GameEvent.CollectPowerUp
import com.matthias.event.GameEventListener
import com.matthias.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import java.lang.Float.min

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture,
    private val gameEventManager: GameEventManager,
    private val outlineShader: ShaderProgram
) : GameEventListener, SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {

    private val background = Sprite(backgroundTexture.apply { setWrap(Repeat, Repeat) })
    private val backgroundScrollSpeed = Vector2(0.03f, -0.25f)

    private val textureSizeLoc = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLoc = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColor = Color(0f, 113f / 255f, 214f / 255f, 1f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(CollectPowerUp::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(CollectPowerUp::class, this)
    }

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            background.run {
                backgroundScrollSpeed.y = min(-0.25f, backgroundScrollSpeed.y + deltaTime * (1f / 10f))
                scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)
                draw(batch)
            }
        }

        forceSort()
        gameViewport.apply()
        batch.use(gameViewport.camera.combined) {
            super.update(deltaTime)
        }

        renderEntityOutlines()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!

        if (graphic.sprite.texture == null) {
            return LOG.error { "Entity has no texture for rendering. |$entity|" }
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(transform.interpolatedPosition.x, transform.interpolatedPosition.y, transform.size.x, transform.size.y)
            draw(batch)
        }
    }

    override fun onEvent(event: GameEvent) {
        if (event is CollectPowerUp) {
            LOG.debug { "Speeding up" }
            when (event.type) {
                SPEED_1 -> backgroundScrollSpeed.y -= 0.25f
                SPEED_2 -> backgroundScrollSpeed.y -= 0.5f
                else -> {}
            }
        }
    }

    private fun renderEntityOutlines() {
        batch.use(gameViewport.camera.combined) {
            it.shader = outlineShader
            playerEntities.forEach { playerEntity -> renderPlayerOutline(playerEntity, it) }
            it.shader = null
        }
    }

    private fun renderPlayerOutline(playerEntity: Entity, it: Batch) {
        val player = playerEntity[PlayerComponent.mapper]!!

        if (player.shield > 0f) {
            outlineColor.a = MathUtils.clamp(player.shield / player.maxShield, 0f, 1f)
            outlineShader.setUniformf(outlineColorLoc, outlineColor)

            playerEntity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    outlineShader.setUniformf(textureSizeLoc, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
        }
    }

}