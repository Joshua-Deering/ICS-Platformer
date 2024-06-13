package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.TILE_HEIGHT
import josh.icsplatformer.TILE_WIDTH
import java.awt.geom.Rectangle2D.Double as Rect

class TileMap(val chunkLoader: ChunkLoader, val chunks: MutableList<Chunk>, var scrollVel: Double, val scrollScaling: Double) {
    var scrollDist = 0.0

    fun update(dt: Double) {
        scrollVel -= scrollScaling * dt
        scrollDist += scrollVel * dt
        var chunksToAdd = mutableListOf<Chunk>()
        var chunksToRemove = mutableListOf<Chunk>()
        for (c in chunks) {
            //chunk.move returns a boolean of whether it is off the screen
            if(c.move(scrollVel * dt)) {
                chunksToRemove.add(c)
                chunksToAdd.add(chunkLoader.genChunk(scrollDist).clone())
            }
        }
        chunks.removeAll(chunksToRemove)
        chunks.addAll(chunksToAdd)
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
        MapTextures.drawTexture(gc, type, posx, posy, TILE_WIDTH, TILE_HEIGHT)
    }
}