package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import kotlin.io.path.Path

object MapTextures {
    val spriteSheet: Image = Image(Path("src/main/resources/sprites/Game_tiles.png").toAbsolutePath().toUri().toURL().toString())
    const val SPRITE_DIMENSIONS = 8.0

    fun drawTexture(gc: GraphicsContext, sprite: Int, x: Double, y: Double, width: Double, height: Double) {
        gc.drawImage(spriteSheet,
            if(sprite == 5) {sprite * SPRITE_DIMENSIONS} else {(sprite - 1) * SPRITE_DIMENSIONS}, 0.0,
            if(sprite == 5) {-SPRITE_DIMENSIONS} else {SPRITE_DIMENSIONS}, SPRITE_DIMENSIONS,
            x, y, width, height)
    }
}