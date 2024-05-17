package josh.icsplatformer

import josh.icsplatformer.entities.Platform

class PlatformManager(var platforms: MutableList<Platform> = mutableListOf()) {
    
    fun show() {
        for (platform in platforms) {
            platform.show()
        }
    }

    fun update(dt: Double) {
        for (platform in platforms) {
            platform.update(dt)
        }
    }
}