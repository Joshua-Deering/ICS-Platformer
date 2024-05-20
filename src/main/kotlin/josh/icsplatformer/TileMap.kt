package josh.icsplatformer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.awt.geom.Rectangle2D.Double as Rect

//constants for tile dimensions
const val TILE_WIDTH: Double = 50.0
const val TILE_HEIGHT: Double = 50.0

class TileMap(private val gc: GraphicsContext, private val tiles: MutableList<Tile> = mutableListOf(), val hitboxes: MutableList<Rect> = mutableListOf(), var scrollVel: Double = -1.0) {
    //x-offset for this tilemap
    private var offsetX: Double = 0.0

    fun update(dt: Double) {
        //offsetX += scrollVel*dt

    }

    fun show() {
        gc.fill = Color.BLUE
        for (tile in tiles) {
            gc.fillRect(tile.x * TILE_WIDTH + offsetX, tile.y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT)
        }
        //TODO("implement sprites for tiles")
    }
}

data class Tile(val x: Int, val y: Int, val type: Int)