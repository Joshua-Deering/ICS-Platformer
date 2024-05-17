package josh.icsplatformer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.lib.Hitbox
import josh.icsplatformer.lib.Vec2

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property pos This players Hitbox
 */
class Player(private val gc: GraphicsContext, private var pos: Hitbox = Hitbox()) {

    /**
     * Draws this player on the given GraphicsContext
     */
    fun show() {
        gc.fill = Color.RED
        gc.fillRect(pos.min.x, pos.min.y, pos.width, pos.height)

        TODO("sprite/sprite animation")
    }

    /**
     * Updates position and performs physics checks for this player
     *
     * @param dt Millisecond(s) since last frame/update
     */
    fun update(dt: Double) {
        pos.move(Vec2(5.0, 0.0).scalarMult(dt/1e3))

        TODO("Physics")
    }
}