package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class AttachComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<AttachComponent>()
    }

    lateinit var entity: Entity
    val offset = Vector2()

    override fun reset() {
        offset.set(0f, 0f)
    }
}