package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.KeyListener
import josh.icsplatformer.PlayerConstants
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
    private var lastOnGround: Long = System.nanoTime()
    private var jumped = false

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
        //velocity adjustments (i.e drag, etc)
        if(onGround) {
            vel.x *= PlayerConstants.GROUND_DRAG
        } else {
            vel.x *= PlayerConstants.AIR_DRAG
        }

        val timeSinceOnGround = (System.nanoTime() - lastOnGround)/1e9
        onGround = onGround || timeSinceOnGround < 0.083
        if (timeSinceOnGround < 0.3) {
            vel.y += PlayerConstants.GRAVITY
        } else {
            vel.y += PlayerConstants.FALLING_GRAVITY
        }

        //displacement for this frame
        val d = Vec2(0.0, 0.0)

        if (keyListener.keyDown("W")) {
            if (onGround && !jumped) {
                vel.y = PlayerConstants.JUMP_STRENGTH
                jumped = true
            }
        }
        if (keyListener.keyDown("D")) {
            vel.x += 13.0
        }
        if (keyListener.keyDown("A")) {
            vel.x -= 13.0
        }

        //limit velocity
        vel.clamp(Vec2(-150.0, -300.0), Vec2(150.0, 300.0))

        //add the velocity to the displacement
        d.plusAssign(vel)
        d.scalarMult(dt)

        //change position of player to reflect velocity changes
        pos.setRect(pos.x + d.x, pos.y - d.y, pos.width, pos.height)

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
            //collision on top side of other
            if (collisionRect.centerY > pos.centerY) {
                vel.y = 0.0
                pos.setRect(pos.x, other.minY - pos.height, pos.width, pos.height)
                //set player state
                onGround = true
                lastOnGround = System.nanoTime()
                jumped = false
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