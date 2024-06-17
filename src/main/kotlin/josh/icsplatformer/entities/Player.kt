/*
 * Player.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * Main player class, manages all things for the player
 * (physics, rendering, updating, etc)
 */


package josh.icsplatformer.entities

//imports
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import josh.icsplatformer.KeyListener
import josh.icsplatformer.PlayerConstants
import josh.icsplatformer.lib.Vec2
import kotlin.io.path.Path
import kotlin.math.min
import java.awt.geom.Rectangle2D.Double as Rect
import josh.icsplatformer.DRAW_HITBOXES
import josh.icsplatformer.SCREEN_HEIGHT
import josh.icsplatformer.map.TileMap
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Main Player class
 *
 * @property gc Graphics context to draw to
 * @property tileMap the tile map associated with this player
 * @property pos the position of this player
 * @property vel the velocity of this player (default 0.0)
 * @property keyListener the key listener associated with this scene, for keyboard input
 */
class Player(gc: GraphicsContext, val tileMap: TileMap, pos: Rect, private var vel: Vec2 = Vec2(), private val keyListener: KeyListener) : Entity(gc, pos) {

    //public for gameloop to keep track of player state
    var alive = true
    var dying = false

    //physics variables
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
    private var grappleStartTime = System.nanoTime()
    private var grappleVel = Vec2(0.0, 0.0)
    private val grappleSpeed = 10.0
    private var grappleTargetPos = Vec2(0.0, 0.0)
    private var grappleOffset = Vec2(0.0, 0.0)
    private var canGrapple = false
    private var grappleDir = false

    //animation transitions
    private var transitioning = false
    private var transitionSource = 0
    private var transitionTarget = 0

    //sprite animations
    private var curAnimation = 0
    private val animations: List<SpriteAnimation> = createAnimations()
    private val grappleImg = Image(Path("src/main/resources/sprites/grapple-hook.png").toAbsolutePath().toUri().toURL().toString())

    /**
     * Draws this player on the given GraphicsContextd
     */
    override fun show() {
        //debug method which shows the collision rectangle for the player
        if (DRAW_HITBOXES) {
            gc.fill = Color.LIGHTGREEN
            gc.strokeRect(pos.x, pos.y, pos.width, pos.height)
        }
        //draw the current animation frame
        animations[curAnimation].show(gc, pos.x - 10.0, pos.y - 5.0, 50.0, 40.0)

        //if the player is grappling, render the grapple hook/line
        if (grappling || throwingGrapple) {
            //grapple line
            gc.stroke = Color.SADDLEBROWN
            gc.strokeLine(pos.x + grappleOffset.x, pos.y + grappleOffset.y, grapplePos.x, grapplePos.y)
            //grapple hook
            gc.save()
            //get direction towards grapple target
            val dirVec = Vec2(grappleTargetPos.x - grappleOffset.x - pos.x, grappleTargetPos.y - grappleOffset.y - pos.y)
            val dirVecMagnitude = sqrt((grappleTargetPos.x - grappleOffset.x - pos.x).pow(2) + (grappleTargetPos.y - grappleOffset.y - pos.y).pow(2))
            dirVec.scalarMultAssign(1.0 / dirVecMagnitude)
            dirVec.scalarMultAssign(8.0)
            //find the angle between the player and the grappling hook
            val angle = atan2(grapplePos.y - (grappleOffset.y + pos.y), grapplePos.x - (grappleOffset.x + pos.x)) * (180.0 / PI) + 90.0
            //rotate the image by the angle
            rotate(gc, angle, grapplePos.x - dirVec.x, grapplePos.y - dirVec.y)
            //draw the rotated grapple image onto the canvas
            gc.drawImage(grappleImg, 0.0, 0.0, 16.0, 16.0, grapplePos.x - 5.0 - dirVec.x, grapplePos.y - 10.0 - dirVec.y, 10.0, 10.0)
            gc.restore()
        }
    }

    /**
     * Updates position and performs physics checks for this player
     *
     * @param dt Second(s) since last frame/update
     */
    override fun update(dt: Double) {
        //if the player is outside the left or bottom sides of the screen, they die
        if(pos.x < 0.0 - pos.width || pos.y > SCREEN_HEIGHT) {
            dying = true
        }

        //displacement for this frame
        val d = Vec2(tileMap.scrollVel, 0.0)

        //do all velocity/drag etc calculations
        if (!grappling) applyPhysics()

        //grappling checks
        if(keyListener.keyDown("W")) {
            //currently throwing grapple
            if (throwingGrapple) {
                //update the hooks position
                grapplePos.plusAssign(grappleVel)
                //check if the hook has hit something, if so, start grappling
                if (checkGrappleCollisions()) {
                    throwingGrapple = false
                    grappling = true
                }
                //check if the grapple has exceeded its maximum length
                if(sqrt((grapplePos.x - pos.x).pow(2) + (pos.y - grapplePos.y).pow(2)) > 380.0) {
                    throwingGrapple = false
                    grappling = false
                    grapplePos = Vec2(pos.x + grappleOffset.x, pos.y + grappleOffset.y)
                }
                lastGrapple = System.nanoTime()
            }
            //starting new grapple
            else if ((System.nanoTime() - lastGrapple) / 1e9 > 0.5 && canGrapple) {
                //set the offset & direction for rendering the grapple line
                grappleOffset = Vec2(if(vel.x > 0.0) {30.0} else {5.0}, 5.0)
                grappleDir = vel.x > 0.0
                //set the start time of this grapple and its target position
                grappleStartTime = System.nanoTime()
                grappleTargetPos = Vec2(keyListener.mouseX, keyListener.mouseY)
                //get a unit vector in the direction of the target position
                val dirVec = Vec2(grappleTargetPos.x - pos.x - grappleOffset.x, grappleTargetPos.y - pos.y - grappleOffset.y)
                dirVec.scalarMultAssign(1.0/sqrt((grappleTargetPos.x - pos.x - grappleOffset.x).pow(2) + (grappleTargetPos.y - pos.y - grappleOffset.y).pow(2)))
                //set the grappling variables appropriately
                throwingGrapple = true
                canGrapple = false
                //set the grapple position and velocity to be updated in the throwing animation
                grapplePos = Vec2(pos.x + grappleOffset.x, pos.y + grappleOffset.y)
                grappleVel = dirVec.scalarMult(grappleSpeed)
            }
            //currently grappling
            else if (grappling) {
                lastGrapple = System.nanoTime()
                //if the grapple has been attached for too long, detach it
                if((System.nanoTime() - grappleStartTime)/1e9 > 2.0) {
                    grappling = false
                    throwingGrapple = false
                } else {
                    //if the player is currently dying, do not update the grapple
                    if(!dying) grappleTargetPos.x += tileMap.scrollVel * dt
                    grappling = true
                    throwingGrapple = false
                    //find unit vector in direction of the target position
                    val dirVec = Vec2(grappleTargetPos.x - pos.x, grappleTargetPos.y - pos.y)
                    dirVec.scalarMultAssign(1.0 / sqrt((grappleTargetPos.x - pos.x).pow(2) + (grappleTargetPos.y - pos.y).pow(2)))
                    //multiply the vector by the strength of the grapple to get a force vector
                    dirVec.scalarMultAssign(25.0)
                    //force adjustments base on player velocty
                    if (vel.y < 0) {
                        dirVec.y *= -1.2
                    } else {
                        dirVec.y *= -0.4
                    }
                    //add the force of the grapple to the player velocity
                    vel.plusAssign(dirVec)
                }
            }
        }
        //not grappling, not throwing grapple, so reset variables
        else if(grappling || throwingGrapple) {
            grappling = false
            throwingGrapple = false
        }

        //apply velocity caps (different for different player states)
        if(grappling) {
            //limit velocity during grappling
            vel.clamp(Vec2(-500.0, -400.0), Vec2(PlayerConstants.MAX_VEL_X_A - tileMap.scrollVel + 100, 400.0))
        } else if(!canGrapple && (vel.x > PlayerConstants.MAX_VEL_X_G - tileMap.scrollVel || vel.x < -PlayerConstants.MAX_VEL_X_G)) {
            //limit velocity after grappling
            vel.x *= 0.97
        } else if (onGround) {
            //limit velocity on ground
            vel.clamp(Vec2(-PlayerConstants.MAX_VEL_X_G, -400.0), Vec2(PlayerConstants.MAX_VEL_X_G - tileMap.scrollVel, 400.0))
        } else {
            //limit velocity in air
            vel.clamp(Vec2(-PlayerConstants.MAX_VEL_X_A, -400.0), Vec2(PlayerConstants.MAX_VEL_X_A - tileMap.scrollVel, 400.0))
        }

        //add the velocity to the displacement
        d.plusAssign(vel)
        //multiply by delta-time to be accurate with the current frame rate
        d.scalarMultAssign(dt)

        //change position of player to reflect velocity changes
        if(!dying) pos.setRect(pos.x + d.x, pos.y - d.y, pos.width, pos.height)

        //change/update animation state
        val newAnim = getAnimationState()
        if (curAnimation != newAnim) {
            //if the animation state has changed,
            //reset the previous one and set the current state to the new state
            animations[curAnimation].reset()
            curAnimation = newAnim
        }
        //update the current animation
        animations[curAnimation].update()

        //reset player physics state
        onGround = false
        justLanded = false
    }

    //applies physics to the player, and accounts for keyboard input
    fun applyPhysics() {
        //velocity adjustments (i.e drag, etc)
        if(onGround) {
            vel.x *= PlayerConstants.GROUND_DRAG
        } else {
            vel.x *= PlayerConstants.AIR_DRAG
        }
        if(vel.x < 0.1 && vel.x > -0.1) vel.x = 0.0

        //update physics variables based on when the player was last on the ground
        timeSinceOnGround = (System.nanoTime() - lastOnGround)/1e9
        onGround = onGround || timeSinceOnGround < 0.083
        if(onGround && (System.nanoTime() - lastInAir)/1e9 < 0.01) {
            //if the time since the player was in-air is low, they have just hit the ground
            justLanded = true
        }
        if(!onGround) lastInAir = System.nanoTime()

        if (timeSinceOnGround < 0.3) {
            //if the player was on the ground recently, apply normal gravity
            vel.y += PlayerConstants.GRAVITY
        } else {
            //if the player was not on the ground recently, apply the gravity for falling
            vel.y += PlayerConstants.FALLING_GRAVITY
        }

        if (keyListener.keyDown(KeyCode.SPACE.toString())) {
            //if the user is pressing space, the player is on the ground
            //and the player has not already jumped, make the player jump
            if (onGround && !jumped) {
                vel.y = PlayerConstants.JUMP_STRENGTH
                jumped = true
            }
        }

        //if the user is pressing A or D, move the player left and right, respectively
        if (keyListener.keyDown("D")) {
            vel.x += if (onGround) {(PlayerConstants.MAX_VEL_X_G - tileMap.scrollVel)/13.0} else {(PlayerConstants.MAX_VEL_X_A - tileMap.scrollVel)/14.0}
        }
        if (keyListener.keyDown("A")) {
            vel.x -= if (onGround) {(PlayerConstants.MAX_VEL_X_G)/13.0} else {PlayerConstants.MAX_VEL_X_A/14.0}
        }
    }

    //returns what the current animation should be, based on the player's velocity
    fun getAnimationState(): Int {
        //find the player's left-right direction
        val xMag = abs(vel.x)
        val xDir = vel.x > 0 //true = right, false = left
        if (xMag <= 15.0 && xMag >= 0.1) {
            lastDir = (vel.x > 0.0)
        }

        //if player is dying, that animation takes priority
        if (dying) {
            //the dying animation has finished, so the player has fully died.
            if (animations[transitionSource].finished) {
                alive = false
            }
            transitioning = true
            transitionSource = 16
            transitionTarget = 16
            return transitionSource
        }

        //the player just landed on the ground,
        //start a transition into the idle state
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

        //return the current transition, if the player is currently in a transition state
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
                if (xDir) { //ground grapple throwing animations
                    9
                } else {
                    8
                }
            } else {
                if (xDir) { //air grapple throwing animations
                    11
                } else {
                    10
                }
            }
        } else if (grappling) {
            return if (grappleDir) { //air grappling animations
                13
            } else {
                12
            }
        }

        return if (!onGround) { //player is in the air
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

    //helper function to check if the grappling hook has collided with the map
    fun checkGrappleCollisions(): Boolean {
        for (hb in tileMap.getHitboxes()) {
            if (hb.intersects(grapplePos.x, grapplePos.y, 3.0, 3.0)) {
                grappleTargetPos = grapplePos
                return true
            }
        }
        return false
    }

    //helper function to draw a rotated image
    fun rotate(gc: GraphicsContext, angle: Double, px: Double, py: Double) {
        val r = Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    //function called whenever the player collides with the map
    override fun collideWithMap(other: Rect) {
        val collisionRect = pos.createIntersection(other)

        //collision on top or bottom of other
        if (collisionRect.width + 3 >= collisionRect.height) {
            //collision on top side of other
            if (collisionRect.centerY > pos.centerY) {
                if (collisionRect.width >= collisionRect.height) {
                    //set player state
                    canGrapple = true
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

    //helper function to create the list of all the animations
    fun createAnimations(): List<SpriteAnimation> {
        return listOf(
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
                0, 23,
                0, 0,
                0.0, 0.0,
                20.0, true, false
            ),
            SpriteAnimation( //running-right: 3
                Image(Path("src/main/resources/sprites/red-hood-running.png").toAbsolutePath().toUri().toURL().toString()),
                50.0, 40.0,
                0, 23,
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
                Image(Path("src/main/resources/sprites/red-hood-dying.png").toAbsolutePath().toUri().toURL().toString()),
                50.0, 40.0,
                0, 4,
                0, 0,
                0.0, 0.0,
                6.0, true, true
            ),
        )
    }

    override fun collide(e: Entity) {

    }
}