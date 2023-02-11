package com.matthias.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool.Poolable
import com.matthias.ecs.component.AnimationType.NONE
import ktx.ashley.mapperFor
import ktx.collections.GdxArray

private const val DEFAULT_FRAME_DURATION = 1 / 20f

enum class AnimationType(
    val atlasKey: String,
    val playMode: PlayMode = LOOP,
    val speedRate: Float = 1f
) {
    NONE(""),
    DARK_MATTER("dark_matter"),
    FIRE("fire"),
    SPEED_1("orb_blue", speedRate = 0.5f),
    SPEED_2("orb_yellow", speedRate = 0.5f),
    LIFE("life", speedRate = 0.75f),
    SHIELD("shield", speedRate = 0.5f)
}

class Animation2D(
    val type: AnimationType,
    keyFrames: GdxArray<out TextureRegion>,
    playMode: PlayMode = LOOP,
    speedRate: Float = 1f
) : Animation<TextureRegion>(DEFAULT_FRAME_DURATION / speedRate, keyFrames, playMode)

class AnimationComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }

    var type = NONE
    var stateTime = 0f
    lateinit var animation: Animation2D

    override fun reset() {
        type = NONE
        stateTime = 0f
    }
}