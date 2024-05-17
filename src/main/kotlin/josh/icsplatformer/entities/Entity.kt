package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.lib.Hitbox

abstract class Entity(private var gc: GraphicsContext, private var pos: Hitbox) {
    abstract fun show()
    abstract fun update(dt: Double)
}