package com.matthias.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader.ShaderProgramParameter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram

enum class TextureAsset(
    fileName: String,
    directory: String = "graphics",
    val descriptor: AssetDescriptor<Texture> = AssetDescriptor("$directory/$fileName")
) {
    BACKGROUND("background.png")
}

enum class TextureAtlasAsset(
    fileName: String,
    directory: String,
    val isSkinAtlas: Boolean = false,
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$fileName")
) {
    GAME_GRAPHICS("graphics.atlas", "textures"),
    UI("ui.atlas", "ui", true)
}

enum class SoundAsset(
    fileName: String,
    directory: String = "sfx",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$fileName")
) {
    BOOST_1("boost1.wav"),
    BOOST_2("boost2.wav"),
    LIFE("life.wav"),
    SHIELD("shield.wav"),
    DAMAGE("damage.wav"),
    BLOCK("block.wav")
}

enum class MusicAsset(
    fileName: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName")
) {
    GAME("game.mp3")
}

enum class ShaderProgramAsset(
    vertexFileName: String,
    fragmentFileName: String,
    directory: String = "shader",
    val descriptor: AssetDescriptor<ShaderProgram> = AssetDescriptor(
        "$directory/$vertexFileName/$fragmentFileName",
        ShaderProgramParameter().apply {
            vertexFile = "$directory/$vertexFileName"
            fragmentFile = "$directory/$fragmentFileName"
        }
    )
) {
    OUTLINE("default.vert", "outline.frag")
}

enum class BitmapFontAsset(
    fileName: String,
    directory: String = "fonts",
    val descriptor: AssetDescriptor<BitmapFont> = AssetDescriptor(
        "$directory/$fileName",
        BitmapFontParameter().apply {
            atlasName = TextureAtlasAsset.UI.descriptor.fileName
        }
    )
) {
    FONT_LARGE_GRADIENT("font11_gradient.fnt"),
    FONT_DEFAULT("font8.fnt")
}

private inline fun <reified T> AssetDescriptor(fileName: String, params: AssetLoaderParameters<T>? = null) = AssetDescriptor(fileName, T::class.java, params)