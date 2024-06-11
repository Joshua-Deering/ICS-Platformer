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
import josh.icsplatformer.SCREEN_HEIGHT
import josh.icsplatformer.map.TileMap
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property pos This players Hitbox
 */
class Player(gc: GraphicsContext, val tileMap: TileMap, pos: Rect, private var vel: Vec2 = Vec2(), private val keyListener: KeyListener, val tileMapScroll: Double) : Entity(gc, pos) {

    //public for gameloop to keep track of player
    var alive = true
    var dying = false

    private var onGround: Boolean = false
    private var lastOnGround: Long = System.nanoTime()
    private var timeSinceOnGround: Double = 0.0
    private var lastInAir = System.nanoTime()
    private var justLanded: Boolean = false
    private var jumped = false
    private var lastDir = false //left false, right true

    //grappling variables
    private var grappling = false
    private var throwingGrapple = false
    private var grapplePos = Vec2(0.0, 0.0)
    private var lastGrapple = System.nanoTime()
    private var grappleVel = Vec2(0.0, 0.0)
    private val grappleSpeed = 20.0
    private var grappleTargetPos = Vec2(0.0, 0.0)
    private var grappleOffset = Vec2(0.0, 0.0)

    //animation transitions
    private var transitioning = false
    private var transitionSource = 0
    private var transitionTarget = 0

    private var curAnimation = 0
    private val animations: List<SpriteAnimation> = listOf(
        SpriteAnimation( //idle-left: 0
            Image(Path("src/main/resources/sprites/red-hood-idle.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 8,
            0, 1,
            0.0, 0.0,
            15.0, false, false
        ),
        SpriteAnimation( //idle-right: 1
            Image(Path("src/main/resources/sprites/red-hood-idle.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 8,
            0, 1,
            0.0, 0.0,
            15.0, true, false
        ),
        SpriteAnimation( //running-left: 2
            Image(Path("src/main/resources/sprites/red-hood-running.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 24,
            0, 0,
            0.0, 0.0,
            20.0, true, false
        ),
        SpriteAnimation( //running-right: 3
            Image(Path("src/main/resources/sprites/red-hood-running.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 24,
            0, 0,
            0.0, 0.0,
            20.0, false, false
        ),
        SpriteAnimation( //jumping-left: 4
            Image(Path("src/main/resources/sprites/red-hood-jumping.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 11,
            0, 0,
            0.0, 0.0,
            20.0, false, false
        ),
        SpriteAnimation( //jumping-right: 5
            Image(Path("src/main/resources/sprites/red-hood-jumping.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 11,
            0, 0,
            0.0, 0.0,
            20.0, true, false
        ),
        SpriteAnimation( //falling-left: 6
            Image(Path("src/main/resources/sprites/red-hood-falling.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 2,
            0, 0,
            0.0, 0.0,
            20.0, false, false
        ),
        SpriteAnimation( //falling-right: 7
            Image(Path("src/main/resources/sprites/red-hood-falling.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 2,
            0, 0,
            0.0, 0.0,
            20.0, true, false
        ),
        SpriteAnimation( //ground-to-grapple left: 8
            Image(Path("src/main/resources/sprites/red-hood-ground-to-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 4,
            0, 0,
            0.0, 0.0,
            20.0, false, true
        ),
        SpriteAnimation( //ground-to-grapple right: 9
            Image(Path("src/main/resources/sprites/red-hood-ground-to-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 4,
            0, 0,
            0.0, 0.0,
            20.0, true, true
        ),
        SpriteAnimation( //air-to-grapple left: 10
            Image(Path("src/main/resources/sprites/red-hood-falling-to-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 3,
            0, 0,
            0.0, 0.0,
            20.0, false, true
        ),
        SpriteAnimation( //air-to-grapple right: 11
            Image(Path("src/main/resources/sprites/red-hood-falling-to-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 3,
            0, 0,
            0.0, 0.0,
            20.0, true, true
        ),
        SpriteAnimation( //grappling left: 12
            Image(Path("src/main/resources/sprites/red-hood-in-air-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 2,
            0, 0,
            0.0, 0.0,
            20.0, false, false
        ),
        SpriteAnimation( //grappling right: 13
            Image(Path("src/main/resources/sprites/red-hood-in-air-grapple.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 2,
            0, 0,
            0.0, 0.0,
            20.0, true, false
        ),
        SpriteAnimation( //landing left: 14
            Image(Path("src/main/resources/sprites/red-hood-landing.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 3,
            0, 0,
            0.0, 0.0,
            12.0, false, true
        ),
        SpriteAnimation( //landing right: 15
            Image(Path("src/main/resources/sprites/red-hood-landing.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 3,
            0, 0,
            0.0, 0.0,
            12.0, true, true
        ),
        SpriteAnimation( //dying: 16
            Image(Path("src/main/resources/sprites/red-hood-slide.png").toAbsolutePath().toUri().toURL().toString()),
            50.0, 40.0,
            0, 3,
            0, 0,
            0.0, 0.0,
            2.0, true, true
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
        animations[curAnimation].show(gc, pos.x - 10.0, pos.y - 5.0, 50.0, 40.0)

        if (grappling) {
            gc.stroke = Color.BLUE
            gc.strokeLine(pos.x + grappleOffset.x, pos.y + grappleOffset.y, grapplePos.x, grapplePos.y)
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
        if(pos.x < 0.0 || pos.y > SCREEN_HEIGHT) {
            dying = true
        }

        //velocity adjustments (i.e drag, etc)
        if(onGround) {
            vel.x *= PlayerConstants.GROUND_DRAG
        } else {
            vel.x *= PlayerConstants.AIR_DRAG
        }

        //air/ground calculations
        timeSinceOnGround = (System.nanoTime() - lastOnGround)/1e9
        onGround = onGround || timeSinceOnGround < 0.083
        if(onGround && (System.nanoTime() - lastInAir)/1e9 < 0.01) {
            justLanded = true
        }
        if(!onGround) lastInAir = System.nanoTime()

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
            grappleOffset = Vec2(if(vel.x > 0.0) {30.0} else {5.0}, 5.0)
            if (throwingGrapple) {
                grapplePos.plusAssign(grappleVel)
                if (checkGrappleCollisions()) {
                    throwingGrapple = false
                }
                if(sqrt((grapplePos.x - pos.x).pow(2) + (pos.y - grapplePos.y).pow(2)) > 500.0) {
                    throwingGrapple = false
                    grappling = false
                    grapplePos = Vec2(pos.x + grappleOffset.x, pos.y + grappleOffset.y)
                }
                lastGrapple = System.nanoTime()
            } else if ((System.nanoTime() - lastGrapple) / 1e9 > 0.3) {
                grappleTargetPos = Vec2(keyListener.mouseX, keyListener.mouseY)
                val dirVec = Vec2(grappleTargetPos.x - pos.x - grappleOffset.x, grappleTargetPos.y - pos.y - grappleOffset.y)
                dirVec.scalarMultAssign(1.0/sqrt((grappleTargetPos.x - pos.x - grappleOffset.x).pow(2) + (grappleTargetPos.y - pos.y - grappleOffset.y).pow(2)))
                throwingGrapple = true
                grappling = true
                grapplePos = Vec2(pos.x + grappleOffset.x, pos.y + grappleOffset.y)
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
        } else if(grappling || throwingGrapple) {
            grappling = false
            throwingGrapple = false
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
        justLanded = false
    }

    override fun collide(e: Entity) {
        //do stuff based on which entity we have collided with
    }

    fun getAnimationState(): Int {
        val xMag = abs(vel.x)
        val xDir = vel.x > 0
        if (xMag <= 15.0 && xMag >= 0.1) {
            lastDir = (vel.x > 0.0)
        }

        if (dying) {
            if (animations[transitionSource].finished) {
                alive = false
            }
            transitioning = true
            transitionSource = 16
            transitionTarget = 16
            return transitionSource
        }

        if(justLanded) {
            transitioning = true
            if(xDir) {
                transitionSource = 15
                transitionTarget = 1
            } else {
                transitionSource = 14
                transitionTarget = 0
            }
        }

        if (transitioning) {
            if (animations[transitionSource].finished) {
                transitioning = false
                return transitionTarget
            } else {
                return transitionSource
            }
        }

        //grappling animations take priority
        if (throwingGrapple) {
            return if (onGround) {
                if (xDir) {
                    9
                } else {
                    8
                }
            } else {
                if (xDir) {
                    11
                } else {
                    10
                }
            }
        } else if (grappling) {
            return if (xDir) {
                13
            } else {
                12
            }
        }

        return if (!onGround) { //is in the air
            if (vel.y >= 0.0) {
                if (xDir) {
                    5
                } else {
                    4
                }
            } else {
                if (xDir) {
                    7
                } else {
                    6
                }
            }
        } else {
            if(xMag <= 15.0) {
                if (lastDir) {
                    1
                } else {
                    0
                }
            } else if (xDir) {
                2
            } else {
                3
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