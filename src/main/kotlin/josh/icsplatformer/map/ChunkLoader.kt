package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import java.io.File

class ChunkLoader {
    lateinit var possibleChunks: MutableList<Chunk>

    fun loadChunksFromFile(gc: GraphicsContext, file: String) {
        val lines = File(file).useLines{it -> it.toList()}
        possibleChunks = mutableListOf()
        var offset = 0.0
        var chunkLines = mutableListOf<String>()
        for (l in lines) {
            if (l == "") continue
            val split = l.split(",")
            if (split.size == 1) {
                if(chunkLines.size > 1) {
                    possibleChunks.add(createChunkFromStrings(gc, offset, chunkLines))
                    chunkLines = mutableListOf()
                }
                offset = l.toDouble()
                continue
            }
            chunkLines.add(l)
        }
        possibleChunks.add(createChunkFromStrings(gc, offset, chunkLines))
    }

    fun getChunks(startIdx: Int, endIdx: Int): MutableList<Chunk> {
        var chunks = mutableListOf<Chunk>()
        for (i in startIdx..endIdx) {
            chunks.add(possibleChunks[i].clone())
        }
        return chunks
    }

    fun genChunk(scrollDist: Double): Chunk {
        return possibleChunks[2].clone()
    }

    fun createChunkFromStrings(gc: GraphicsContext, offset: Double, lines: List<String>): Chunk {
        var tiles = mutableListOf<Tile>()
        for(i in 0..lines.lastIndex) {
            val lSplit = lines[i].split(",")
            for (j in 0..lSplit.lastIndex) {
                if (lSplit[i] == "") continue
                val type = lSplit[j].toInt()
                if (type == 0) continue
                tiles.add(Tile(j, i, type))
            }
        }

        return Chunk(gc, offset, tiles)
    }
}