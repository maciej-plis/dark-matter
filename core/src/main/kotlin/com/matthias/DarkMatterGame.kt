package com.matthias

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.matthias.assets.BitmapFontAsset.FONT_DEFAULT
import com.matthias.assets.BitmapFontAsset.FONT_LARGE_GRADIENT
import com.matthias.assets.ShaderProgramAsset
import com.matthias.assets.TextureAsset.BACKGROUND
import com.matthias.assets.TextureAtlasAsset.GAME_GRAPHICS
import com.matthias.assets.TextureAtlasAsset.REQUIRED
import com.matthias.audio.AudioService
import com.matthias.audio.DefaultAudioService
import com.matthias.ecs.system.*
import com.matthias.event.GameEventManager
import com.matthias.screen.LoadingScreen
import com.matthias.ui.setupDefaultSkin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger

private val LOG = logger<DarkMatterGame>()

const val UNIT_SCALE = 1 / 8f
const val V_WIDTH = 9f
const val V_HEIGHT = 16f
const val V_WIDTH_PIXELS = 135f
const val V_HEIGHT_PIXELS = 240f

class DarkMatterGame : KtxGame<KtxScreen>() {

    val gameEventManager = GameEventManager()

    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }

    val audioService: AudioService by lazy { DefaultAudioService(assets) }

    val uiViewport = FitViewport(V_WIDTH_PIXELS, V_HEIGHT_PIXELS)
    val gameViewport = FitViewport(V_WIDTH, V_HEIGHT)

    val stage by lazy {
        Stage(uiViewport, batch).also {
            Gdx.input.inputProcessor = it
        }
    }

    val preferences by lazy { Gdx.app.getPreferences("dark-matter") }

    val batch by lazy { SpriteBatch() }
    val engine by lazy {
        PooledEngine().apply {
            val graphicsAtlas = assets[GAME_GRAPHICS.descriptor]

            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager, audioService))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameEventManager, gameViewport.camera))
            addSystem(PlayerAnimationSystem(graphicsAtlas.findRegion("ship_base"), graphicsAtlas.findRegion("ship_left"), graphicsAtlas.findRegion("ship_right")))
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(RenderSystem(batch, gameViewport, uiViewport, assets[BACKGROUND.descriptor], gameEventManager, assets[ShaderProgramAsset.OUTLINE.descriptor]))
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        LOG.debug { "Creating DarkMatterGame" }

        val requiredAssets = listOf(
            assets.loadAsync(REQUIRED.descriptor),
            assets.loadAsync(FONT_LARGE_GRADIENT.descriptor),
            assets.loadAsync(FONT_DEFAULT.descriptor)
        )

        KtxAsync.launch {
            requiredAssets.joinAll()

            setupDefaultSkin(assets)

            addScreen(LoadingScreen(this@DarkMatterGame))
            setScreen<LoadingScreen>()
        }
    }

    override fun dispose() {
        super.dispose()

        LOG.debug { "Sprites in batch: ${batch.maxSpritesInBatch}" }
        batch.dispose()
        assets.dispose()
        stage.dispose()
    }
}