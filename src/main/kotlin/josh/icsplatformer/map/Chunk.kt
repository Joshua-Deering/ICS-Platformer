package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.DRAW_HITBOXES
import java.awt.geom.Rectangle2D.Double as Rect

class Chunk(private val gc: GraphicsContext, var offset: Double, val tiles: MutableList<Tile>, val hitboxes: MutableList<Rect> = loadHitboxes(tiles)) {

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
        if(DRAW_HITBOXES) {
            gc.stroke = Color.BLACK
            for (hb in hitboxes) {
                gc.strokeRect(hb.x, hb.y, hb.width, hb.height)
            }
        }
    }

    companion object {
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