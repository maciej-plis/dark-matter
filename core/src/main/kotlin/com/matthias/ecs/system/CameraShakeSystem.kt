package com.matthias.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pool.Poolable
import com.matthias.event.GameEvent
import com.matthias.event.GameEvent.PlayerHit
import com.matthias.event.GameEventListener
import com.matthias.event.GameEventManager
import ktx.collections.GdxArray
import ktx.log.logger

private val LOG = logger<CameraShakeSystem>()

private class CameraShake : Poolable {
    var maxDistortion = 0f
    var duration = 0f
    lateinit var camera: Camera
    private var storeCameraPos = true
    private val origCamPosition = Vector3()
    private var currentDuration = 0f

    fun update(deltaTime: Float): Boolean {
        if (storeCameraPos) {
            storeCameraPos = false
            origCamPosition.set(camera.position)
        }

        if (currentDuration < duration) {
            val currentPower = maxDistortion * ((duration - currentDuration) / duration)

            camera.position.x = origCamPosition.x + random(-1f, 1f) * currentPower
            camera.position.y = origCamPosition.y + random(-1f, 1f) * currentPower
            camera.update()

            currentDuration += deltaTime
            return false
        }

        camera.position.set(origCamPosition)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        storeCameraPos = true
        origCamPosition.set(0f, 0f, 0f)
        currentDuration = 0f
    }
}

private class CameraShakePool(private val gameCamera: Camera) : Pool<CameraShake>() {

    override fun newObject() = CameraShake().apply {
        this.camera = gameCamera
    }

}

class CameraShakeSystem(
    private val gameEventManager: GameEventManager,
    camera: Camera
) : EntitySystem(), GameEventListener {

    private val shakePool = CameraShakePool(camera)
    private val activeShakes = GdxArray<CameraShake>()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        gameEventManager.addListener(PlayerHit::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun update(deltaTime: Float) {
        if (!activeShakes.isEmpty) {
            val shake = activeShakes.first()
            if (shake.update(deltaTime)) {
                activeShakes.removeIndex(0)
                shakePool.free(shake)
            }
        }
    }

    override fun onEvent(event: GameEvent) {
        if (activeShakes.size < 4) {
            activeShakes.add(shakePool.obtain().apply {
                duration = 0.25f
                maxDistortion = 0.25f
            })
        }
    }
}