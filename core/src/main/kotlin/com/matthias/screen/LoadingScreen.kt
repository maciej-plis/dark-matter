package com.matthias.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align.center
import com.matthias.DarkMatterGame
import com.matthias.assets.*
import com.matthias.ui.LabelStyles.DEFAULT
import com.matthias.ui.LabelStyles.GRADIENT
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.scene2d.*

class LoadingScreen(game: DarkMatterGame) : ScreenBase(game) {

    private lateinit var progressBar: Image
    private lateinit var touchToBeginLabel: Label

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
        if (assets.progress.isFinished && Gdx.input.justTouched() && game.containsScreen<GameScreen>()) {
            game.setScreen<GameScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        progressBar.scaleX = assets.progress.percent
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        touchToBeginLabel += forever(sequence(fadeIn(0.5f) + fadeOut(0.5f)))
    }

    private fun setupUI() {
        stage.actors {
            table {
                defaults().fillX().expandX()

                label("Loading Screen", GRADIENT.name) {
                    wrap = true
                    setAlignment(center)
                }

                row()

                touchToBeginLabel = label("Touch to Begin", DEFAULT.name) {
                    wrap = true
                    setAlignment(center)
                    color.a = 0f
                }

                row()

                stack { cell ->
                    progressBar = image("life_bar") {
                        scaleX = 0f
                    }
                    label("Loading...", DEFAULT.name) {
                        setAlignment(center)
                    }
                    cell.padLeft(5f).padRight(5f)
                }

                setFillParent(true)
                pack()
            }
        }
//        stage.isDebugAll = true
    }
}
