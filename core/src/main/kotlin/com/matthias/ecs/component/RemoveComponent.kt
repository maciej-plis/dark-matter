package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class RemoveComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<RemoveComponent>()
    }

    var delay = 0f

    override fun reset() {
        delay = 0f
    }
}