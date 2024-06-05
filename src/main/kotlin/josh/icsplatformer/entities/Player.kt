package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import josh.icsplatformer.KeyListener
import josh.icsplatformer.PlayerConstants
import josh.icsplatformer.lib.Vec2
import kotlin.io.path.Path
import kotlin.math.min
import java.awt.geom.Rectangle2D.Double as Rect
import josh.icsplatformer.DRAW_HITBOXES
import josh.icsplatformer.map.TileMap
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property pos This players Hitbox
 */
class Player(gc: GraphicsContext, val tileMap: TileMap, pos: Rect, private var vel: Vec2 = Vec2(), private val keyListener: KeyListener, val tileMapScroll: Double) : Entity(gc, pos) {
    private var onGround: Boolean = false
    private var lastOnGround: Long = System.nanoTime()
    private var jumped = false

    //grappling variables
    private var grappling = false
    private var throwingGrapple = false
    private var grapplePos = Vec2(0.0, 0.0)
    private var lastGrapple = System.nanoTime()
    private var grappleVel = Vec2(0.0, 0.0)
    private val grappleSpeed = 20.0
    private var grappleTargetPos = Vec2(0.0, 0.0)

    private var curAnimation = 0
    private val animations: List<SpriteAnimation> = listOf(
        SpriteAnimation(
            Image(Path("src/main/resources/sprites/red-hood-sheet.png").toAbsolutePath().toUri().toURL().toString()),
            30.0, 36.0,
            0, 11,
            0, 1,
            0.0, 0.0,
            20.0, false
        ),
        SpriteAnimation(
            Image(Path("src/main/resources/sprites/red-hood-sheet.png").toAbsolutePath().toUri().toURL().toString()),
            30.0, 36.0,
            0, 11,
            0, 1,
            0.0, 0.0,
            20.0, true
        ),
        SpriteAnimation(
            Image(Path("src/main/resources/sprites/red-hood-sheet.png").toAbsolutePath().toUri().toURL().toString()),
            30.0, 36.0,
            0, 3,
            4, 4,
            0.0, 0.0,
            10.0, false
        ),
        SpriteAnimation(
            Image(Path("src/main/resources/sprites/red-hood-sheet.png").toAbsolutePath().toUri().toURL().toString()),
            30.0, 36.0,
            0, 3,
            4, 4,
            0.0, 0.0,
            10.0, true
        ),
    )

    /**
     * Draws this player on the given GraphicsContext
     */
    override fun show() {
        if (DRAW_HITBOXES) {
            gc.fill = Color.LIGHTGREEN
            gc.strokeRect(pos.x, pos.y, pos.width, pos.height)
        }
        animations[curAnimation].show(gc, pos.x, pos.y, pos.width, pos.height)

        if (grappling) {
            gc.stroke = Color.BLUE
            gc.strokeLine(pos.x, pos.y, grapplePos.x, grapplePos.y)
            gc.fill = Color.RED
            gc.fillRect(grapplePos.x, grapplePos.y, 5.0, 5.0)
        }
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
        val d = Vec2(tileMapScroll, 0.0)

        if (keyListener.keyDown("W")) {
            if (onGround && !jumped) {
                vel.y = PlayerConstants.JUMP_STRENGTH
                jumped = true
            }
        }
        if (keyListener.keyDown("D")) {
            vel.x += if (onGround) 13.0 else { 6.0 }
        }
        if (keyListener.keyDown("A")) {
            vel.x -= if (onGround) 13.0 else { 6.0 }
        }

        if(keyListener.keyDown(KeyCode.SPACE.toString())) {
            if (throwingGrapple) {
                grapplePos.plusAssign(grappleVel)
                if (checkGrappleCollisions()) {
                    throwingGrapple = false
                }
                lastGrapple = System.nanoTime()
            } else if ((System.nanoTime() - lastGrapple) / 1e9 > 0.3) {
                grappleTargetPos = Vec2(keyListener.mouseX, keyListener.mouseY)
                val dirVec = Vec2(grappleTargetPos.x - pos.x, grappleTargetPos.y - pos.y)
                dirVec.scalarMultAssign(1.0/sqrt((grappleTargetPos.x - pos.x).pow(2) + (grappleTargetPos.y - pos.y).pow(2)))
                throwingGrapple = true
                grappling = true
                grapplePos = Vec2(pos.x, pos.y)
                grappleVel = dirVec.scalarMult(grappleSpeed)
            } else if (grappling) {
                grappleTargetPos.x += tileMapScroll * dt
                lastGrapple = System.nanoTime()
                grappling = true
                throwingGrapple = false
                val dirVec = Vec2(grappleTargetPos.x - pos.x, grappleTargetPos.y - pos.y)
                dirVec.scalarMultAssign(1.0/sqrt((grappleTargetPos.x - pos.x).pow(2) + (grappleTargetPos.y - pos.y).pow(2)))
                dirVec.scalarMultAssign(50.0)
                if(vel.y < 0) {
                    dirVec.y *= -0.75
                } else {
                    dirVec.y *= -0.4
                }
                vel.plusAssign(dirVec)
            }
        } else if(grappling) {
            grappling = false
        }

        if (onGround) {
            //limit velocity on ground
            vel.clamp(Vec2(-200.0, -400.0), Vec2(200.0, 400.0))
        } else {
            //limit velocity in air
            vel.clamp(Vec2(-400.0, -400.0), Vec2(400.0, 400.0))
        }

        //add the velocity to the displacement
        d.plusAssign(vel)
        d.scalarMultAssign(dt)

        //change position of player to reflect velocity changes
        pos.setRect(pos.x + d.x, pos.y - d.y, pos.width, pos.height)

        //change state
        val newAnim = getAnimationState()
        if (curAnimation != newAnim) {
            animations[curAnimation].reset()
            curAnimation = newAnim
        }
        animations[curAnimation].update()

        //reset state
        onGround = false
    }

    override fun collide(e: Entity) {
        //do stuff based on which entity we have collided with
        //TODO("add logic for entity-entity collision")
    }

    fun getAnimationState(): Int {
        return if (!onGround) { //is in the air
            if (vel.x > 0) {
                3
            } else {
                2
            }
        } else {
            if (vel.x > 0) {
                1
            } else {
                0
            }
        }
    }

    fun checkGrappleCollisions(): Boolean {
        for (hb in tileMap.getHitboxes()) {
            if (hb.intersectsLine(pos.x, pos.y, grapplePos.x, grapplePos.y)) {
                grappleTargetPos = grapplePos
                return true
            }
        }
        return false
    }

    override fun collideWithMap(other: Rect) {
        val collisionRect = pos.createIntersection(other)

        //collision on top or bottom of other
        if (collisionRect.width + 3 >= collisionRect.height) {
            //collision on top side of other
            if (collisionRect.centerY > pos.centerY) {
                if (collisionRect.width >= collisionRect.height) {
                    //set player state
                    onGround = true
                    lastOnGround = System.nanoTime()
                    jumped = false
                    vel.y = 0.0
                }
                pos.setRect(pos.x, other.minY - pos.height, pos.width, pos.height)
            } else {
                vel.y = min(vel.y, 0.0)
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