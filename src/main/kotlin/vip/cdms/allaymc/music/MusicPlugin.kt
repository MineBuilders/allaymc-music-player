package vip.cdms.allaymc.music

import org.allaymc.api.plugin.Plugin

@Suppress("unused")
class MusicPlugin : Plugin() {
    override fun onLoad() = pluginLogger.info("MusicPlayer loaded!")
    override fun onEnable() = pluginLogger.info("MusicPlayer enabled!")
    override fun onDisable() = pluginLogger.info("MusicPlayer disabled!")
}
