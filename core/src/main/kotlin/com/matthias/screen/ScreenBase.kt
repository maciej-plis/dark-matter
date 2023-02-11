package com.matthias.screen

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.matthias.DarkMatterGame
import com.matthias.audio.AudioService
import com.matthias.event.GameEventManager
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class ScreenBase(
    protected val game: DarkMatterGame,
    protected val gameViewport: Viewport = game.gameViewport,
    protected val uiViewport: Viewport = game.uiViewport,
    protected val batch: Batch = game.batch,
    protected val gameEventManager: GameEventManager = game.gameEventManager,
    protected val assets: AssetStorage = game.assets,
    protected val audioService: AudioService = game.audioService,
    protected val preferences: Preferences = game.preferences,
    protected val stage: Stage = game.stage
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}