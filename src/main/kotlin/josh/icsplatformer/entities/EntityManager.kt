/*
 * EntityManager.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * manager class, to manage all entities in the game
 */

package josh.icsplatformer.entities

import josh.icsplatformer.map.TileMap

/**
 * Manages all entities in the game - updates each of them and renders each of them
 * also checks collisions each frame and calls the appropriate method in case of a collision
 */
class EntityManager(private var entities: MutableList<Entity> = mutableListOf(), private val tileMap: TileMap) {
    //updates every entity
    fun update(dt: Double) {
        for (e in entities) {
            e.update(dt)
        }
    }

    //renders every entity
    fun show() {
        for (e in entities) {
            e.show()
        }
    }

    //check collisions for every entity
    fun checkCollisions() {
        //sort hitboxes to more efficiently check collisions
        quickSort(entities, 0, entities.size)

        //loop through all entities, finding ones with collisions
        for (i in 0..entities.lastIndex) {
            val a = entities[i]

            //entity-entity collisions
            for (j in (i+1)..entities.lastIndex) {
                val b = entities[j]
                //entity is past this entity, no more can intersect (list is sorted)
                if (b.pos.minX > a.pos.maxX) break

                if (a.pos.intersects(b.pos)) {
                    //call collision code for both entities
                    a.collide(b)
                    b.collide(a)
                }
            }

            val tileMapHbs = tileMap.getHitboxes()
            //entity-tilemap collisions (i.e collisions with platforms)
            for (j in 0..tileMapHbs.lastIndex) {
                if (a.pos.intersects(tileMapHbs[j])) {
                    a.collideWithMap(tileMapHbs[j])
                }
            }
        }
    }

    //quicksort method to sort hitboxes
    private fun quickSort(arr: MutableList<Entity>, l: Int, r: Int) {
        if (arr.size == 1 || arr.isEmpty()) return
        if (l < r) {
            val pivot = partition(arr, l, r)

            quickSort(arr, l, pivot - 1)
            quickSort(arr, pivot, r)
        }
    }

    //helper function for the quicksort function
    private fun partition(arr: MutableList<Entity>, l: Int, r: Int): Int {

        val pivot = arr[r].pos.minX
        var i = l-1

        for (j in l..r-1) {
            if (arr[j].pos.minX < pivot) {
                i++
                swap(arr, i, j)
            }
        }

        swap(arr, i+1, r)
        return i+1
    }

    //helper function for the quicksort function
    private fun<T> swap(arr: MutableList<T>, i1: Int, i2: Int) {
        val tmp = arr[i1]
        arr[i1] = arr[i2]
        arr[i2] = tmp
    }
}