package com.matthias.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Align.center
import com.matthias.DarkMatterGame
import com.matthias.assets.*
import com.matthias.common.fadeInOut
import com.matthias.common.textSequence
import com.matthias.ui.LabelStyles.GRADIENT
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.scene2d.*

class LoadingScreen(game: DarkMatterGame) : ScreenBase(game) {

    private lateinit var progressBar: ProgressBar
    private lateinit var touchToBeginLabel: Label
    private lateinit var progressBarLabel: Label

    private var loadingFinished = false

    override fun show() {
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) },
            MusicAsset.values().map { assets.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { assets.loadAsync(it.descriptor) },
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            assetsLoaded()
        }

        setupUI()
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
        if (loadingFinished && Gdx.input.justTouched()) {
            game.setScreen<GameScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        updateProgressBar()

        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))

        touchToBeginLabel.isVisible = true
        setProgressBarAsLoaded()

        loadingFinished = true
    }

    private fun setupUI() {
        stage.actors {

            table {
                setFillParent(true)

                defaults().fillX().expandX()

                label("Dark Matter", GRADIENT.id) {
                    wrap = true
                    setAlignment(center)
                }

                row().pad(5f)

                touchToBeginLabel = label("Touch to Begin") {
                    wrap = true
                    setAlignment(center)
                    addAction(forever(fadeInOut(0.5f, 0.5f)))
                    isVisible = false
                }

                row()

                stack {
                    progressBar = progressBar() {
                        style.background.minHeight = 20f
                        style.background.minWidth = 1f
                        style.knob.minHeight = 20f
                        style.knob.minWidth = 1f
                    }
                    progressBarLabel = label("Loading...") {
                        setAlignment(center)
                        addAction(forever(textSequence(0.5f, "Loading.", "Loading..", "Loading...")))
                    }
                }
            }
        }
//        stage.isDebugAll = true
    }

    private fun updateProgressBar() {
        progressBar.value = assets.progress.percent
    }

    private fun setProgressBarAsLoaded() {
        progressBarLabel.clearActions()
        progressBarLabel.setText("loaded")
    }
}