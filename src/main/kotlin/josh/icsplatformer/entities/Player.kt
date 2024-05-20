package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.lib.Vec2
import java.awt.geom.Rectangle2D.Double as Rect

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property pos This players Hitbox
 */
class Player(private val gc: GraphicsContext, pos: Rect, private var vel: Vec2 = Vec2()) : Entity(pos) {

    /**
     * Draws this player on the given GraphicsContext
     */
    override fun show() {
        gc.fill = Color.RED
        gc.fillRect(pos.minX, pos.minY, pos.getWidth(), pos.getHeight())
        //TODO("sprite/sprite animation")
    }

    /**
     * Updates position and performs physics checks for this player
     *
     * @param dt Second(s) since last frame/update
     */
    override fun update(dt: Double) {
        //displacement for this frame
        val d = Vec2(3.0, 0.0)

        //adjust the velocity based on physics
        vel.plusAssign(Vec2(0.0, 9.8))

        //add the velocity to the displacement
        d.plusAssign(vel)
        d.scalarMult(dt)

        pos.setRect(pos.x + d.x, pos.y + d.y, pos.width, pos.height)

        //temporarily to keep player onscreen
        if (pos.maxY > 450) {
            pos.setRect(pos.x, 450.0 - pos.height, pos.width, pos.height)
        }

        //TODO("Physics")
    }

    override fun collide(e: Entity) {
        //do stuff based on which entity we have collided with
        //TODO("Not yet implemented")
    }

    override fun collideWithMap(other: Rect) {
        vel = Vec2(0.0, 0.0)
        val collisionRect = pos.createIntersection(other)

        //collision on top or bottom of other
        if (collisionRect.width > collisionRect.height) {
            //collision on top side of other
            if (collisionRect.centerY > pos.centerY) {
                pos.setRect(pos.x, other.minY - pos.height, pos.width, pos.height)
            } else {
                //collision on bottom side of other
                pos.setRect(pos.x, other.maxY, pos.width, pos.height)
            }
        } else { //collision on sides
            //collision on right side of other
            if (collisionRect.centerX < pos.centerX) {
                pos.setRect(other.maxX, pos.y, pos.width, pos.height)
            } else {
                //collision on left side of other
                pos.setRect(other.minX - pos.width, pos.y, pos.width, pos.height)
            }
        }
    }
}