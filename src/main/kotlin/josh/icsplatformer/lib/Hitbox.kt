package josh.icsplatformer.lib

/**
 * Hitbox class for storing information about entities collision areas.
 *
 * @property min The top left corner of this Hitbox
 * @property max The bottom right corner of this Hitbox
 */
data class Hitbox(var min: Vec2 = Vec2(), var max: Vec2 = Vec2()) {
    val width = max.x - min.x
    val height = max.y - min.y
    val beginX = min.x
    val endX = max.x

    /**
     * Checks whether this Hitbox overlaps with another Hitbox
     * @param other Other Hitbox
     */
    fun overlaps(other: Hitbox): Boolean {
        val d1x = other.min.x - min.x
        val d1y = other.min.y - min.y
        val d2x = min.x - other.min.x
        val d2y = min.y - other.min.y

        return !(d1x > 0.0 || d1y > 0.0 || d2x > 0.0 || d2y > 0.0)
    }

    /**
     * Moves this hitbox by some amount
     * @param dist How much to move
     */
    fun move(dist: Vec2) {
        min.plusAssign(dist)
        max.plusAssign(dist)
    }

    fun setPos(topLeft: Vec2) {
        min = topLeft
        max = Vec2(topLeft.x + width, topLeft.y + height)
    }
}
