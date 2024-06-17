/*
 * TileMap.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * manages the map for the game, including rendering tiles
 * and storing hitboxes for collisions
 */

package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.TILE_HEIGHT
import josh.icsplatformer.TILE_WIDTH
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import java.awt.geom.Rectangle2D.Double as Rect

/**
 * class to manage the map, uses chunks to update and render tiles
 * @param chunkLoader the chunk loader
 * @param chunks the current chunks in the tilemap
 * @param scrollVel the velocty to move the tilemap by
 */
class TileMap(val chunkLoader: ChunkLoader, val chunks: MutableList<Chunk>, var scrollVel: Double) {
    var scrollDist = 0.0
    val start = System.nanoTime()
    val initialSpeed = scrollVel

    /**
     * updates the position of each chunk in this tilemap,
     * removes chunks that are offscreen,
     * and generates new chunks when needed
     */
    fun update(dt: Double) {
        scrollVel = initialSpeed - sqrt((System.nanoTime() - start).toDouble())/20 * dt
        scrollDist += scrollVel * dt
        var chunksToAdd = mutableListOf<Chunk>()
        var chunksToRemove = mutableListOf<Chunk>()
        for (c in chunks) {
            //chunk.move returns a boolean of whether it is off the screen
            //if it is offscreen, remove it, and add another chunk
            if(c.move(scrollVel * dt)) {
                chunksToRemove.add(c)
                chunksToAdd.add(chunkLoader.genChunk(scrollDist.absoluteValue).clone())
            }
        }
        //add and remove appropriate chunks
        chunks.removeAll(chunksToRemove)
        chunks.addAll(chunksToAdd)
    }

    /**
     * renders each chunk associated with this tilemap
     */
    fun show() {
        for (chunk in chunks) {
            chunk.show()
        }
    }

    /**
     * returns all of the hitboxes associated with this tilemap
     */
    fun getHitboxes(): List<Rect> {
        var hbs = mutableListOf<Rect>()
        //get all hitboxes in each chunk, and add them to the list
        chunks.forEach{chunk -> hbs.addAll(chunk.hitboxes)}
        //return the hitboxes
        return hbs
    }
}

/**
 * helper class, stores information for a single tile
 * @property x x index of this tile
 * @property y y index of this tile
 * @property type tile type
 */
data class Tile(val x: Int, val y: Int, val type: Int) : Comparable<Tile> {
    //compareto function, used for sorting tiles
    override fun compareTo(other: Tile) = compareValuesBy(this, other, {it.x}, {it.y})

    /**
     * renders this tile to the given graphics context
     * @param gc the graphics context to draw to
     * @param posx the x-position to draw to
     * @param posy the y-position to draw to
     */
    fun show(gc: GraphicsContext, posx: Double, posy: Double) {
        //use the maptextures class to draw this tile
        MapTextures.drawTexture(gc, type, posx, posy, TILE_WIDTH, TILE_HEIGHT)
    }
}