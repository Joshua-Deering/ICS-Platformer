/*
 * MapTextures.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * helper object to store and render all map textures
 */


package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import kotlin.io.path.Path

/**
 * helper object to store and render all map textures
 */
object MapTextures {
    //spritesheet for map textures
    val spriteSheet: Image = Image(Path("src/main/resources/sprites/Game_tiles.png").toAbsolutePath().toUri().toURL().toString())
    const val SPRITE_DIMENSIONS = 8.0

    /**
     * draws a tile texture to the given graphics context
     * @param gc graphics context to draw to
     * @param sprite tile type to render
     * @param x x position to render this tile at
     * @param y y position to render this tile at
     * @param width width to draw this tile
     * @param height height to draw this tile
     */
    fun drawTexture(gc: GraphicsContext, sprite: Int, x: Double, y: Double, width: Double, height: Double) {
        gc.drawImage(spriteSheet,
            if(sprite == 5 || sprite == 10) {sprite * SPRITE_DIMENSIONS} else {(sprite - 1) * SPRITE_DIMENSIONS}, 0.0,
            if(sprite == 5 || sprite == 10) {-SPRITE_DIMENSIONS} else {SPRITE_DIMENSIONS}, SPRITE_DIMENSIONS,
            x, y, width, height)
    }
}