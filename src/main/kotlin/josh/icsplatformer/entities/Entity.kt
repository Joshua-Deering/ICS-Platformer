/*
 * Entity.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * Abstract class for all entities
 */

package josh.icsplatformer.entities

import javafx.scene.canvas.GraphicsContext
import java.awt.geom.Rectangle2D.Double as Rect

/**
 * abstract class for all entities
 * @property gc This entity's GraphicsContext
 * @property pos This entity's position
 */
abstract class Entity(val gc: GraphicsContext, var pos: Rect) {
    /**
     * called every frame, to render sprites to the screen
     */
    abstract fun show()

    /**
     * called every frame, to update pos and other variables,
     * given delta-time [dt]
     */
    abstract fun update(dt: Double)

    /**
     * called when this entity collides with another entity [e]
     */
    abstract fun collide(e: Entity)

    /**
     * called when this entity collides with the map,
     * given the [other] map rectangle
     */
    abstract fun collideWithMap(other: Rect)
}