package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.awt.geom.Rectangle2D.Double as Rect

class Chunk(private val gc: GraphicsContext, var offset: Double, val tiles: MutableList<Tile>, val hitboxes: MutableList<Rect>) {

    fun move(x: Double) {
        offset += x
    }

    fun show() {
        gc.fill = Color.BLUE
        for(tile in tiles) {
            gc.fillRect(tile.x * TILE_WIDTH + offset, tile.y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT)
        }
    }
}