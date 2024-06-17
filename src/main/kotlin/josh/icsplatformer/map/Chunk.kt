/*
 * Chunk.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * class to render and manage a screen width worth of the tile map
 */


package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.DRAW_GRID
import josh.icsplatformer.DRAW_HITBOXES
import josh.icsplatformer.SCREEN_HEIGHT
import josh.icsplatformer.SCREEN_WIDTH
import josh.icsplatformer.TILE_HEIGHT
import josh.icsplatformer.TILE_WIDTH
import kotlin.math.ceil
import java.awt.geom.Rectangle2D.Double as Rect

/**
 * class to render and manage one screen-width worth of a tile map
 * @param gc the graphics context this chunk lives on
 * @param offset the current x-offset of this chunk
 * @param tiles the tiles in this chunk
 * @param hitboxes the hitboxes for the tiles in this chunk
 */
class Chunk(private val gc: GraphicsContext, var offset: Double, val tiles: MutableList<Tile>, val hitboxes: MutableList<Rect> = loadHitboxes(tiles)): Cloneable {

    init {
        //add the offset to each hitbox
        for (hb in hitboxes) {
            hb.x += offset
        }
    }

    /**
     * moves this chunk
     * @param x the amound to move this chunk by
     * @return boolean, whether this chunk has moved off screen yet
     */
    fun move(x: Double): Boolean {
        offset += x

        //add the offset to each hitbox
        for (hb in hitboxes) {
            hb.x += x
        }

        //if the chunk has moved offscreen, return true
        if(offset < -SCREEN_WIDTH) {
            return true
        }

        //chunk is still onscreen, return false
        return false
    }

    fun show() {
        //render each tile in this chunk
        for(tile in tiles) {
            tile.show(gc, tile.x * TILE_WIDTH + offset, tile.y * TILE_HEIGHT)
        }
        //debug method to show the tilemap grid
        if(DRAW_GRID) {
            gc.stroke = Color.CORNFLOWERBLUE
            for (i in 0..ceil(SCREEN_WIDTH / TILE_WIDTH).toInt()) {
                for (j in 0..ceil(SCREEN_HEIGHT / TILE_HEIGHT).toInt()) {
                    gc.strokeRect(i * TILE_WIDTH + offset, j * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT)
                }
            }
        }
        //debug method to show this hitboxes in this chunk
        if(DRAW_HITBOXES) {
            gc.stroke = Color.BLACK
            for (hb in hitboxes) {
                gc.strokeRect(hb.x, hb.y, hb.width, hb.height)
            }
        }
    }

    //allows deep-cloning of chunks
    public override fun clone() = Chunk(gc, offset, tiles.toMutableList())

    companion object {
        //function to generate tiles from a string
        fun genTiles(str: String): MutableList<Tile> {
            val tiles = mutableListOf<Tile>()
            for ((i, s) in str.split("\n").withIndex()) {
                for ((j, c) in s.split(",").withIndex()) {
                    val num = c.toInt()
                    if (num != 0) {
                        tiles.add(Tile(j, i, num))
                    }
                }
            }

            return tiles
        }

        //function to load hitboxes based on the tiles in this chunk,
        //finds vertically-adjacent tiles and gives them a shared hitbox,
        //to minimize the amount of hitboxes in this chunk
        fun loadHitboxes(tiles: MutableList<Tile>): MutableList<Rect> {
            tiles.sort()

            var hbs = mutableListOf<Rect>()
            //find largest vertical hitboxes that fill all of the boxes
            var curX = tiles[0].x
            var hbStart: Tile = tiles[0]
            for (i in 1..tiles.lastIndex) {
                val tile = tiles[i]
                if (tile.x == curX && hbStart.x == curX) {
                    //the tiles are not vertically adjacent
                    if (tile.y-1 != tiles[i-1].y) {
                        hbs.add(createHitbox(hbStart, tiles[i-1]))
                        hbStart = tiles[i]
                    }
                } else {
                    hbs.add(createHitbox(hbStart, tiles[i-1]))
                    curX = tile.x
                    hbStart = tile
                }
            }
            hbs.add(createHitbox(hbStart, tiles[tiles.lastIndex]))

            return hbs
        }

        //helper method to create a hitbox based on a begin and end tile
        fun createHitbox(start: Tile, end: Tile): Rect {
            return Rect(start.x * TILE_WIDTH, start.y * TILE_HEIGHT, TILE_WIDTH * (end.x - start.x+1), TILE_HEIGHT * (end.y - start.y+1))
        }
    }
}