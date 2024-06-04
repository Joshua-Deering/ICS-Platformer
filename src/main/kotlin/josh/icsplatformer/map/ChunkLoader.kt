package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import java.io.File

object ChunkLoader {
    fun loadChunksFromFile(gc: GraphicsContext, file: String): MutableList<Chunk> {
        val lines = mutableListOf("") + File("src/main/resources/tilemaps/chunks.txt").useLines{it -> it.toList()}
        val chunks = mutableListOf<Chunk>()
        val indexes = lines.mapIndexedNotNull{i, elem -> i.takeIf{elem == ""}}.toMutableList()
        indexes.add(lines.lastIndex)
        println(indexes)

        for(i in 0..indexes.lastIndex-1) {
            val chunkLines = lines.subList(indexes[i], indexes[i+1])
            val offset = chunkLines[1].toDouble()
            chunks.add(Chunk(gc, offset, chunkLines.subList(2, chunkLines.size).joinToString("\n")))
        }
        return chunks
    }
}