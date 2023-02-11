package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class TransformComponent : Component, Poolable, Comparable<TransformComponent> {

    companion object {
        val mapper = mapperFor<TransformComponent>()
    }

    val position = Vector3()
    val prevPosition = Vector3()
    val interpolatedPosition = Vector3()

    val size = Vector2(1f, 1f)
    var rotationDeg = 0f

    override fun reset() {
        setInitialPosition(0f, 0f, 0f)
        size.set(1f, 1f)
        rotationDeg = 0f
    }

    fun setInitialPosition(x: Float, y: Float, z: Float) {
        position.set(x, y, z)
        prevPosition.set(x, y, z)
        interpolatedPosition.set(x, y, z)
    }

    override fun compareTo(other: TransformComponent): Int {
        val zDiff = position.z.compareTo(other.position.z)
        return if (zDiff != 0) zDiff else position.y.compareTo(other.position.y)
    }
}