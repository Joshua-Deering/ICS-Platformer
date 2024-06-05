package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

class SpriteAnimation(val img: Image, val spriteWidth: Double, val spriteHeight: Double,
                      val startX: Int, val endX: Int, val startY: Int, val endY: Int, val offsetX: Double, val offsetY: Double,
                      val fps: Double, val reflect: Boolean) {
    var curX = startX
    var curY = startY
    val timePerTick = 1e9 / fps
    var lastTime = System.nanoTime()

    fun show(gc: GraphicsContext, x: Double, y: Double, width: Double, height: Double) {
        gc.drawImage(img,
            curX * spriteWidth + offsetX,
            curY * spriteHeight + offsetY,
            spriteWidth, spriteHeight,
            if(reflect) {x + spriteWidth} else {x}, y,
            if(reflect) {-width} else {width}, height
        )
    }

    fun update() {
        val dTick = System.nanoTime() - lastTime
        if (dTick > timePerTick) {
            curX++
            if (curX > endX) {
                curX = startX
                curY++
                if (curY > endY) curY = startY
            }
            lastTime = System.nanoTime()
        }
    }

    fun reset() {
        curX = startX
        curY = startY
    }
}