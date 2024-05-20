package josh.icsplatformer.lib

data class Vec2(var x: Double = 0.0, var y: Double = 0.0) {

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun plusAssign(other: Vec2) {
        x += other.x
        y += other.y
    }
    operator fun minusAssign(other: Vec2) {
        x -= other.x
        y -= other.y
    }

    fun scalarMult(other: Double) {
        x *= other
        y *= other
    }
}