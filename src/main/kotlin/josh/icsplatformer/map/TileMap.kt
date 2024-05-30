package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.awt.geom.Rectangle2D.Double as Rect

//constants for tile dimensions
const val TILE_WIDTH: Double = 50.0
const val TILE_HEIGHT: Double = 50.0

class TileMap(private val gc: GraphicsContext, val chunks: MutableList<Chunk>, var scrollVel: Double = -1.0) {
    //x-offset for this tilemap
    private var offsetX: Double = 0.0

    init {
        println("")
        //TODO("change this to use instances of chunks instead of one tilemap")
        //TODO("add automatic generation of hitboxes based on tiles on map")
    }

    fun update(dt: Double) {
        //offsetX += scrollVel*dt
    }

    fun show() {
        for (chunk in chunks) {
            chunk.show()
        }
        //TODO("implement sprites for tiles")
    }

    fun getHitboxes(): List<Rect> {
        var hbs = mutableListOf<Rect>()
        chunks.forEach{chunk -> hbs.addAll(chunk.hitboxes)}
        return hbs
    }
}

data class Tile(val x: Int, val y: Int, val type: Int) : Comparable<Tile> {
    override fun compareTo(other: Tile) = compareValuesBy(this, other, {it.x}, {it.y})
    fun show(gc: GraphicsContext, posx: Double, posy: Double) {
        gc.drawImage(MapTextures.textures[0].image, posx, posy, TILE_WIDTH, TILE_HEIGHT)
    }
}