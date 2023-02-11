package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class MoveComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<MoveComponent>()
    }

    val speed = Vector2()

    override fun reset() {
        speed.set(0f, 0f)
    }
}