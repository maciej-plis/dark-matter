package com.matthias.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.matthias.DarkMatterGame

fun main() {
    Lwjgl3Application(DarkMatterGame(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("DarkMatter")
        useVsync(true)
        setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
        setWindowedMode(9 * 32, 16 * 32)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}