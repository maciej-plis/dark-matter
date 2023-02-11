package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool.Poolable
import com.matthias.ecs.component.FacingDirection.DEFAULT
import ktx.ashley.mapperFor

class FacingComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<FacingComponent>()
    }

    var direction = DEFAULT

    override fun reset() {
        direction = DEFAULT
    }
}

enum class FacingDirection {
    DEFAULT, LEFT, RIGHT
}