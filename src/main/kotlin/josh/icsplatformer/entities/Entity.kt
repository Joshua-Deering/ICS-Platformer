package josh.icsplatformer.entities

import java.awt.geom.Rectangle2D.Double as Rect

abstract class Entity(var pos: Rect) {
    //called every frame, to render sprites to the screen
    abstract fun show()
    //called every frame, to update pos and other variables
    abstract fun update(dt: Double)
    //called when this entity collides with another entity
    abstract fun collide(e: Entity)
    //called when this entity collides with the map
    abstract fun collideWithMap(other: Rect)
}