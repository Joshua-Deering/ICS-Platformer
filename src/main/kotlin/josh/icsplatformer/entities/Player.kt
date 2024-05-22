package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.KeyListener
import josh.icsplatformer.lib.Vec2
import java.awt.geom.Rectangle2D.Double as Rect

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property pos This players Hitbox
 */
class Player(gc: GraphicsContext, pos: Rect, private var vel: Vec2 = Vec2(), private val keyListener: KeyListener) : Entity(gc, pos) {
    private var onGround: Boolean = false

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
        val d = Vec2(0.0, 0.0)

        //adjust the velocity based on physics
        vel.y -= 9.8

        if (keyListener.keyDown("SPACE")) {
            if (onGround) {
                vel.y = 250.0
            }
        }
        if (keyListener.keyDown("D")) {
            vel.x += 10.0
        }
        if (keyListener.keyDown("A")) {
            vel.x -= 10.0
        }

        vel.clamp(Vec2(-150.0, -250.0), Vec2(150.0, 250.0))
        //add the velocity to the displacement
        d.plusAssign(vel)
        d.scalarMult(dt)

        pos.setRect(pos.x + d.x, pos.y - d.y, pos.width, pos.height)

        //temporarily to keep player onscreen
        if (pos.maxY > 450) {
            pos.setRect(pos.x, 450.0 - pos.height, pos.width, pos.height)
        }

        //reset state
        onGround = false
    }

    override fun collide(e: Entity) {
        //do stuff based on which entity we have collided with
        //TODO("add logic for entity-entity collision")
    }

    override fun collideWithMap(other: Rect) {
        val collisionRect = pos.createIntersection(other)

        //collision on top or bottom of other
        if (collisionRect.width + 5 >= collisionRect.height) {
            //set player state
            onGround = true

            vel.y = 0.0
            //collision on top side of other
            if (collisionRect.centerY > pos.centerY) {
                pos.setRect(pos.x, other.minY - pos.height, pos.width, pos.height)
            } else {
                //collision on bottom side of other
                pos.setRect(pos.x, other.maxY, pos.width, pos.height)
            }
        } else { //collision on sides
            vel.x = 0.0
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