package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

const val MAX_LIFE = 100f
const val MAX_SHIELD = 100f

class PlayerComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }

    var life = MAX_LIFE
    var maxLife = MAX_LIFE
    var shield = 0f
    var maxShield = MAX_SHIELD
    var distance = 0f

    override fun reset() {
        life = MAX_LIFE
        maxLife = MAX_LIFE
        shield = 0f
        maxShield = MAX_SHIELD
        distance = 0f
    }
}