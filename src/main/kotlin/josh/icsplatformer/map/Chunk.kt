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

class Chunk(private val gc: GraphicsContext, var offset: Double, val tiles: MutableList<Tile>, val hitboxes: MutableList<Rect> = loadHitboxes(tiles)) {

    //secondary constructor for creating a chunk from a string
    constructor(gc: GraphicsContext, offset: Double, str: String): this(gc, offset, genTiles(str))

    init {
        for (hb in hitboxes) {
            hb.x += offset
        }
    }

    fun move(x: Double) {
        offset += x

        for (hb in hitboxes) {
            hb.x += x
        }
    }

    fun show() {
        //gc.fill = Color.BLUE
        for(tile in tiles) {
//            gc.fillRect(tile.x * TILE_WIDTH + offset, tile.y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT)
            tile.show(gc, tile.x * TILE_WIDTH + offset, tile.y * TILE_HEIGHT)
        }
        if(DRAW_GRID) {
            gc.stroke = Color.CORNFLOWERBLUE
            for (i in 0..ceil(SCREEN_WIDTH / TILE_WIDTH).toInt()) {
                for (j in 0..ceil(SCREEN_HEIGHT / TILE_HEIGHT).toInt()) {
                    gc.strokeRect(i * TILE_WIDTH + offset, j * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT)
                }
            }
        }
        if(DRAW_HITBOXES) {
            gc.stroke = Color.BLACK
            for (hb in hitboxes) {
                gc.strokeRect(hb.x, hb.y, hb.width, hb.height)
            }
        }
    }

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

        fun createHitbox(start: Tile, end: Tile): Rect {
            return Rect(start.x * TILE_WIDTH, start.y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT * (end.y - start.y+1))
        }
    }
}