/*
 * Vec2.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * helper class to store positions as 2d vectors
 */


package josh.icsplatformer.lib

import kotlin.math.max
import kotlin.math.min

/**
 * @property x x position of this vector
 * @property y y position of this vector
 */
data class Vec2(var x: Double = 0.0, var y: Double = 0.0) {

    /**
     * adds this vector with another vector
     * @param other The other vector
     * @return The sum of the two vectors
     */
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    /**
     * subtracts another vector from this vector
     * @param other The other vector
     * @return The result of the subtraction
     */
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)

    /**
     * adds this vector with another vector,
     * and assigns the result to this vector
     * @param other The other vector
     */
    operator fun plusAssign(other: Vec2) {
        x += other.x
        y += other.y
    }
    /**
     * subtracts another vector from this vector,
     * and assigns the result to this vector
     * @param other The other vector
     */
    operator fun minusAssign(other: Vec2) {
        x -= other.x
        y -= other.y
    }

    /**
     * multiplies this vector with another vector
     * @param other the other vector
     * @return the product of the two vectors
     */
    fun scalarMult(other: Double) = Vec2(x * other, y * other)
    /**
     * multiplies this vector with another vector,
     * and assigns the result to this vector
     * @param other the other vector
     */
    fun scalarMultAssign(other: Double) {
        x *= other
        y *= other
    }

    /**
     * clamps this vector between a minimum and maximum vector
     * @param min the minimum value for this vector
     * @param max the maximum value for this vector
     */
    fun clamp(min: Vec2, max: Vec2) {
        x = min(max(x, min.x), max.x)
        y = min(max(y, min.y), max.y)
    }
}