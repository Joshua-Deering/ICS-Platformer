package josh.icsplatformer.entities

import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.transform.Rotate.Y_AXIS
import josh.icsplatformer.GAME_STOPPED

class SpriteAnimation(val img: Image, val spriteGroup: StackPane, val spriteWidth: Double, val spriteHeight: Double,
                      val startX: Int, val endX: Int, val startY: Int, var posX: Double, var posY: Double, val offsetX: Double, val offsetY: Double,
                      val fps: Double, val reflect: Boolean) {
    var frame: ImageView = ImageView(img)
    var stopped: Boolean = false
    var curX = startX

    init {
        frame.viewport = Rectangle2D(startX * spriteWidth + offsetX, startY * spriteHeight + offsetY, spriteWidth, spriteHeight)
        if (reflect) {
            frame.rotationAxis = Y_AXIS
            frame.rotate = 180.0
        }
    }

    fun start() {
        Thread {
            spriteGroup.children.add(frame)
            var lastTime = System.nanoTime()
            val timePerTick = 1e9/fps

            while(!stopped && !GAME_STOPPED) {
                val dTick = System.nanoTime() - lastTime

                if (dTick > timePerTick) {
                    curX++
                    if (curX > endX) curX = startX
                    frame.viewport = Rectangle2D(curX * spriteWidth + offsetX, startY * spriteHeight + offsetY, spriteWidth, spriteHeight)
                    frame.x = posX
                    frame.y = posY
                    lastTime = System.nanoTime()
                }
            }
        }.start()
    }

    fun stop() {
        stopped = true
        curX = startX
    }
}