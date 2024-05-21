package josh.icsplatformer.entities

import josh.icsplatformer.KeyListener
import josh.icsplatformer.TileMap

class EntityManager(private var entities: MutableList<Entity> = mutableListOf(), private val tileMap: TileMap) {
    fun update(dt: Double) {
        for (e in entities) {
            e.update(dt)
        }
    }

    fun show() {
        for (e in entities) {
            e.show()
        }
    }

    fun checkCollisions() {
        //sort hitboxes to more efficiently check collisions
        quickSort(entities, 0, entities.size)

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

            //entity-tilemap collisions (i.e collisions with platforms)
            for (j in 0..tileMap.hitboxes.lastIndex) {
                if (a.pos.intersects(tileMap.hitboxes[i])) {
                    a.collideWithMap(tileMap.hitboxes[i])
                }
            }
        }
    }

    private fun quickSort(arr: MutableList<Entity>, l: Int, r: Int) {
        if (arr.size == 1 || arr.size == 0) return
        if (l < r) {
            val pivot = partition(arr, l, r)

            quickSort(arr, l, pivot - 1)
            quickSort(arr, pivot, r)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun partition(arr: MutableList<Entity>, l: Int, r: Int): Int {

        val pivot = arr[r].pos.minX
        var i = l-1

        for (j in l..<r) {
            if (arr[j].pos.minX < pivot) {
                i++
                swap(arr, i, j)
            }
        }

        swap(arr, i+1, r)
        return i+1
    }

    private fun<T> swap(arr: MutableList<T>, i1: Int, i2: Int) {
        val tmp = arr[i1]
        arr[i1] = arr[i2]
        arr[i2] = tmp
    }
}