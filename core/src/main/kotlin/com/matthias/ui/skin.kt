package com.matthias.ui

import com.matthias.assets.BitmapFontAsset
import com.matthias.assets.TextureAtlasAsset
import com.matthias.ui.LabelStyles.DEFAULT
import com.matthias.ui.LabelStyles.GRADIENT
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

enum class LabelStyles { DEFAULT, GRADIENT }

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label(DEFAULT.name) {
            font = defaultFont
        }
        label(GRADIENT.name) {
            font = gradientFont
        }
    }
}