package com.matthias.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Label

fun fadeInOut(fadeInDuration: Float, fadeOutDuration: Float): Action = sequence(Actions.fadeIn(fadeInDuration), Actions.fadeOut(fadeOutDuration))

fun textSequence(delayDuration: Float, vararg texts: String): Action = texts.fold(sequence()) { seq, text ->
    seq.addAction(TextAction(text))
    seq.addAction(DelayAction(delayDuration))
    return@fold seq
}

class TextAction(private val text: String) : RunnableAction() {
    override fun run() {
        (target as? Label)?.setText(text)
    }
}