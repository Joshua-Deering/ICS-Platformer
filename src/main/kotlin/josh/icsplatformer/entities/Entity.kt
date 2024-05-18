package josh.icsplatformer.entities

import josh.icsplatformer.lib.Hitbox

abstract class Entity(var pos: Hitbox) {
    abstract fun show()
    abstract fun update(dt: Double)
    abstract fun collide(other: Entity)
}