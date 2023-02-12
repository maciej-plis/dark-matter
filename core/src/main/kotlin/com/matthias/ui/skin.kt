package com.matthias.ui

import com.badlogic.gdx.graphics.Color
import com.matthias.assets.BitmapFontAsset
import com.matthias.assets.TextureAtlasAsset
import com.matthias.ui.LabelStyles.DEFAULT
import com.matthias.ui.LabelStyles.GRADIENT
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.progressBar
import ktx.style.skin


enum class LabelStyles(val id: String) {
    DEFAULT("default"),
    GRADIENT("gradient")
}

fun setupDefaultSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.REQUIRED.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]

    Scene2DSkin.defaultSkin = skin(atlas) { skin ->

        label(DEFAULT.id) {
            font = defaultFont
        }

        label(GRADIENT.id) {
            font = gradientFont
        }

        progressBar("default-horizontal") {
            background = skin.newDrawable("white", Color.DARK_GRAY)
            knob = skin.newDrawable("white", Color.RED)
            knobBefore = knob
        }
    }
}