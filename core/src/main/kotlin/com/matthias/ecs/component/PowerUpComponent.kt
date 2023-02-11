package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool.Poolable
import com.matthias.assets.SoundAsset
import com.matthias.ecs.component.PowerUpType.NONE
import ktx.ashley.mapperFor

enum class PowerUpType(val animationType: AnimationType, val soundAsset: SoundAsset) {
    NONE(AnimationType.NONE, SoundAsset.BLOCK),
    SPEED_1(AnimationType.SPEED_1, SoundAsset.BOOST_1),
    SPEED_2(AnimationType.SPEED_2, SoundAsset.BOOST_2),
    LIFE(AnimationType.LIFE, SoundAsset.LIFE),
    SHIELD(AnimationType.SHIELD, SoundAsset.SHIELD)
}

class PowerUpComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }

    var type = NONE

    override fun reset() {
        type = NONE
    }
}