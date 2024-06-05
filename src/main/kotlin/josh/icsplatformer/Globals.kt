package josh.icsplatformer

const val SCREEN_WIDTH = 736.0
const val SCREEN_HEIGHT = 448.0

/*
 * THREAD GLOBALS
 */
var GAME_STOPPED = false

/*
 * DEBUG GLOBALS
 */
const val DRAW_HITBOXES = true
const val DRAW_GRID = false

/*
 * PLAYER GLOBALS
 */
object PlayerConstants {
    const val GRAVITY = -9.8
    const val FALLING_GRAVITY = -18.0
    const val JUMP_STRENGTH = 400.0
    const val GROUND_DRAG = 0.92
    const val AIR_DRAG = 0.98
}

/*
 * TILE MAP GLOBALS
 */
const val TILE_WIDTH: Double = 32.0
const val TILE_HEIGHT: Double = 32.0