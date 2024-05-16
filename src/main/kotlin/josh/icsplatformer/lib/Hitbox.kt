package josh.icsplatformer.lib

data class Hitbox(var min: Vec2 = Vec2(), var max: Vec2 = Vec2()) {
    val width = max.x - min.x
    val height = max.y - min.y

    fun overlaps(other: Hitbox): Boolean {
        val d1x = other.min.x - min.x
        val d1y = other.min.y - min.y
        val d2x = min.x - other.min.x
        val d2y = min.y - other.min.y

        return !(d1x > 0.0 || d1y > 0.0 || d2x > 0.0 || d2y > 0.0)
    }

    operator fun plusAssign(other: Vec2) {
        min.plusAssign(other)
        max.plusAssign(other)
    }
}
