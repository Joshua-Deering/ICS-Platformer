package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.TILE_HEIGHT
import josh.icsplatformer.TILE_WIDTH
import java.awt.geom.Rectangle2D.Double as Rect

class TileMap(val chunks: MutableList<Chunk>, var scrollVel: Double = -1.0) {
    init {
        println("")
        //TODO("change this to use instances of chunks instead of one tilemap")
        //TODO("add automatic generation of hitboxes based on tiles on map")
    }

    fun update(dt: Double) {
        //do stuff
    }

    fun show() {
        for (chunk in chunks) {
            chunk.show()
        }
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
        gc.drawImage(MapTextures.textures[0], posx, posy, TILE_WIDTH, TILE_HEIGHT)
    }
}