package josh.icsplatformer.lib

import josh.icsplatformer.entities.Entity

class CollisionManager(private var entities: MutableList<Entity> = mutableListOf()) {

    fun checkCollisions() {

        quickSort(entities, 0, entities.size)

        //go through sorted list of entities, calling collide() on any entities that overlap
        for ((i, e) in entities.withIndex()) {
            for (j in i until entities.size-1) {
                //could be colliding, since their x-coordinates overlap
                if (e.pos.endX > entities[j].pos.beginX && e.pos.endX < entities[j].pos.endX) {
                    if (e.pos.overlaps(entities[j].pos)) {
                        e.collide(entities[j])
                    }
                } else {
                    break
                }
            }
        }
    }

    private fun quickSort(arr: MutableList<Entity>, l: Int, r: Int) {
        if (l < r) {
            val pivot = partition(arr, l, r)

            quickSort(arr, l, pivot - 1)
            quickSort(arr, pivot, r)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun partition(arr: MutableList<Entity>, l: Int, r: Int): Int {

        val pivot = arr[r].pos.beginX
        var i = l-1

        for (j in l..<r) {
            if (arr[j].pos.beginX < pivot) {
                i++
                swap(arr, i, j)
            }
        }

        swap(arr, i+1, r)
        return i+1
    }

    private fun swap(arr: MutableList<Entity>, i1: Int, i2: Int) {
        val tmp = arr[i1]
        arr[i1] = arr[i2]
        arr[i2] = tmp
    }
}