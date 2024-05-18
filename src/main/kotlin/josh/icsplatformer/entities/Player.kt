package josh.icsplatformer.entities

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
class Player(private val gc: GraphicsContext, pos: Hitbox, private val vel: Vec2 = Vec2()) : Entity(pos) {

    /**
     * Draws this player on the given GraphicsContext
     */
    override fun show() {
        gc.fill = Color.RED
        gc.fillRect(pos.min.x, pos.min.y, pos.width, pos.height)

        //TODO("sprite/sprite animation")
    }

    /**
     * Updates position and performs physics checks for this player
     *
     * @param dt Millisecond(s) since last frame/update
     */
    override fun update(dt: Double) {
        //displacement for this frame
        val d = Vec2(5.0, 0.0)

        //adjust the velocity based on physics
        vel.plusAssign(Vec2(0.0, 9.8))

        //add the velocity to the displacement
        d.plusAssign(vel)

        pos.move(d.scalarMult(dt/1e3))

        //temporarily keep player onscreen
        if (pos.max.y > 450) {
            pos.setPos(Vec2(pos.min.x, 450-pos.height))
        }

        //TODO("Physics")
    }

    override fun collide(other: Entity) {
        TODO("Not yet implemented")
    }
}