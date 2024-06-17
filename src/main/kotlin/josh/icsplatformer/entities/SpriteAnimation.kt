/*
 * SpriteAnimation.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * helper class to render animated sprites to the screen
 */


package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

/**
 * @property img the spritesheet for this animation
 * @property spriteWidth width of the sprite in the source image
 * @property spriteHeight height of the sprite in the source image
 * @property startX starting x-index in the source image
 * @property endX end x-index in the source image
 * @property startY starting y-index in the source image
 * @property endY end y-index in the source image
 * @property offsetX offset in the source image of the first sprite X
 * @property offsetY offset in the source image of the first sprite Y
 * @property fps frames per second for this animation to play at
 * @property reflect whether this animation should be reflected horizontally
 * @property playOnce whether this animation continues indefinetly or just plays once and stops
 */
class SpriteAnimation(val img: Image, val spriteWidth: Double, val spriteHeight: Double,
                      val startX: Int, val endX: Int, val startY: Int, val endY: Int, val offsetX: Double, val offsetY: Double,
                      val fps: Double, val reflect: Boolean, val playOnce: Boolean) {
    var curX = startX
    var curY = startY
    val timePerTick = 1e9 / fps
    var lastTime = System.nanoTime()
    var finished = false

    /**
     * @param gc the graphicsContext to draw this animation to
     * @param x x position to draw at
     * @param y y position to draw at
     * @param width width to draw sprite onto gc
     * @param height height to draw sprite onto gc
     */
    fun show(gc: GraphicsContext, x: Double, y: Double, width: Double, height: Double) {
        gc.drawImage(img,
            if(!finished) {curX} else {endX} * spriteWidth + offsetX,
            if(!finished) {curY} else {endY} * spriteHeight + offsetY,
            spriteWidth, spriteHeight,
            if(reflect) {x + width} else {x}, y,
            if(reflect) {-width} else {width}, height
        )
    }

    /**
     * updates the state of this animation,
     * advances the frame if necessary
     */
    fun update() {
        val dTick = System.nanoTime() - lastTime
        //if the time passed is longer than the time per frame, advance the frame to the next one
        if (dTick > timePerTick) {
            curX++
            //if we've reached the end of the frames, go back to the beginning
            if (curX > endX) {
                curX = startX
                curY++
                if (curY > endY) {
                    curY = startY
                    if(playOnce) finished = true
                }
            }
            lastTime = System.nanoTime()
        }
    }

    /**
     * resets this animation to its default state
     */
    fun reset() {
        curX = startX
        curY = startY
        finished = false
    }
}