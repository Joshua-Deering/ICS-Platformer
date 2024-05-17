package josh.icsplatformer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.lib.Hitbox
import josh.icsplatformer.lib.Vec2

class Platform(private val gc: GraphicsContext, private var pos: Hitbox) {

    fun show() {
        gc.fill = Color.TAN
        gc.fillRect(pos.min.x, pos.min.x, pos.width, pos.height)
    }

    fun update(dt: Double) {
        pos.move(Vec2(-1.0, 0.0).scalarMult(dt/1e3))
    }
}