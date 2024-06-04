package josh.icsplatformer.entities

import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.transform.Rotate.Y_AXIS
import josh.icsplatformer.GAME_STOPPED
import josh.icsplatformer.PlayerConstants

class SpriteAnimation(val img: Image, val spriteGroup: StackPane, val spriteWidth: Double, val spriteHeight: Double,
                      val startX: Int, val endX: Int, val startY: Int, var posX: Double, var posY: Double, val offsetX: Double, val offsetY: Double,
                      val fps: Double, val reflect: Boolean) {
    private var frame: ImageView = ImageView(img)
    private var curX = startX
    private val timePerTick = 1e9 / fps
    private var lastTime = System.nanoTime()

    init {
        frame.viewport =
            Rectangle2D(startX * spriteWidth + offsetX, startY * spriteHeight + offsetY, spriteWidth, spriteHeight)
        if (reflect) {
            frame.rotationAxis = Y_AXIS
            frame.rotate = 180.0
        }
        frame.translateX = posX
        frame.translateY = posY
        frame.isVisible = false
        spriteGroup.children.add(frame)
    }

    fun start() {
        lastTime = System.nanoTime()
        frame.isVisible = true
    }

    fun reset() {
        curX = startX
        frame.isVisible = false
    }

    fun setCurFrame() {
        val dTick = System.nanoTime() - lastTime
        if (dTick > timePerTick) {
            curX++
            if (curX > endX) curX = startX
            frame.viewport =
                Rectangle2D(curX * spriteWidth + offsetX, startY * spriteHeight + offsetY, spriteWidth, spriteHeight)
            lastTime = System.nanoTime()
        }
    }

    fun setPos(posX: Double, posY: Double) {
        frame.translateX = posX
        frame.translateY = posY
    }
}