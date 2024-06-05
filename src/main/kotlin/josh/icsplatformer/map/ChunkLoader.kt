package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import java.io.File

class ChunkLoader {
    lateinit var possibleChunks: MutableList<Chunk>

    fun loadChunksFromFile(gc: GraphicsContext, file: String) {
        val lines = mutableListOf("") + File(file).useLines{it -> it.toList()}
        possibleChunks = mutableListOf()
        val indexes = lines.mapIndexedNotNull{i, elem -> i.takeIf{elem == ""}}.toMutableList()
        indexes.add(lines.lastIndex)

        for(i in 0..indexes.lastIndex-1) {
            val chunkLines = lines.subList(indexes[i], indexes[i+1])
            val offset = chunkLines[1].toDouble()
            possibleChunks.add(Chunk(gc, offset, chunkLines.subList(2, chunkLines.size).joinToString("\n")))
        }
    }

    fun getChunks(startIdx: Int, endIdx: Int): MutableList<Chunk> {
        var chunks = mutableListOf<Chunk>()
        for (i in startIdx..endIdx) {
            chunks.add(possibleChunks[i].clone())
        }
        return chunks
    }

    fun genChunk(scrollDist: Double): Chunk {
        return possibleChunks[1].clone()
    }

    companion object {

    }
}