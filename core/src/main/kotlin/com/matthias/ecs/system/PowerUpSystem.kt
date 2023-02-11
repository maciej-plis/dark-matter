package com.matthias.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Rectangle
import com.matthias.V_WIDTH
import com.matthias.audio.AudioService
import com.matthias.ecs.component.*
import com.matthias.ecs.component.PowerUpType.*
import com.matthias.event.GameEvent.CollectPowerUp
import com.matthias.event.GameEventManager
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.lastIndex
import ktx.log.logger
import java.lang.Float.min

private val LOG = logger<PowerUpSystem>()
private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -8.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.75f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

private class SpawnPattern(
    type1: PowerUpType = NONE,
    type2: PowerUpType = NONE,
    type3: PowerUpType = NONE,
    type4: PowerUpType = NONE,
    type5: PowerUpType = NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem(private val gameEventManager: GameEventManager, val audioService: AudioService) :
    IteratingSystem(allOf(PowerUpComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    private val playerBoundingRect = Rectangle()
    private val powerUpBoundingRect = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class, TransformComponent::class).exclude(
                RemoveComponent::class
            ).get()
        )
    }

    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(type1 = SPEED_1, type2 = SPEED_2, type5 = LIFE),
        SpawnPattern(type2 = LIFE, type3 = SHIELD, type4 = SPEED_2)
    )
    private val currentSpawnPattern = GdxArray<PowerUpType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime > 0) return

        spawnTime = random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

        if (currentSpawnPattern.isEmpty) {
            currentSpawnPattern.addAll(spawnPatterns[random(0, spawnPatterns.lastIndex)].types)
            LOG.debug { "Next pattern: $currentSpawnPattern" }
        }

        val powerUpType = currentSpawnPattern.removeIndex(0)
        if (powerUpType == NONE) return

        spawnPowerUp(powerUpType, 1f * random(0f, V_WIDTH - 1), 16f)
    }

    private fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float) {
        engine.entity {
            with<TransformComponent> { setInitialPosition(x, y, 0f) }
            with<PowerUpComponent> { type = powerUpType }
            with<AnimationComponent> { type = powerUpType.animationType }
            with<GraphicComponent>()
            with<MoveComponent> { speed.y = POWER_UP_SPEED }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!

        if (transform.position.y <= 1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        powerUpBoundingRect.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y
        )
        playerEntities.forEach { player ->
            val playerTransform = player[TransformComponent.mapper]!!
            playerBoundingRect.set(
                playerTransform.position.x,
                playerTransform.position.y,
                playerTransform.size.x,
                playerTransform.size.y
            )

            if (playerBoundingRect.overlaps(powerUpBoundingRect)) {
                collectPowerUp(player, entity)
            }
        }
    }

    private fun collectPowerUp(playerEntity: Entity, powerUpEntity: Entity) {
        val powerUp = powerUpEntity[PowerUpComponent.mapper]!!

        LOG.debug { "Picking up power up of type ${powerUp.type}" }

        when (powerUp.type) {
            SPEED_1 -> playerEntity[MoveComponent.mapper]?.let { it.speed.y += BOOST_1_SPEED_GAIN }
            SPEED_2 -> playerEntity[MoveComponent.mapper]?.let { it.speed.y += BOOST_2_SPEED_GAIN }
            LIFE -> playerEntity[PlayerComponent.mapper]?.let { it.life = min(it.maxLife, it.life + LIFE_GAIN) }
            SHIELD -> playerEntity[PlayerComponent.mapper]?.let { it.shield = min(it.maxShield, it.shield + SHIELD_GAIN) }
            else -> LOG.error { "Unsupported power up of type ${powerUp.type}" }
        }

        audioService.play(powerUp.type.soundAsset)

        gameEventManager.dispatchEvent(CollectPowerUp.apply {
            this.player = playerEntity
            this.type = powerUp.type
        })

        powerUpEntity.addComponent<RemoveComponent>(engine)
    }
}