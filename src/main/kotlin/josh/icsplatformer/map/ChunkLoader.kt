/*
 * ChunkLoader.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * helper class to load and generate possible chunks
 */


package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import java.io.File
import kotlin.math.absoluteValue

/**
 * helper class to load all possible chunks for use later
 */
class ChunkLoader {
    //all possible chunks
    lateinit var possibleChunks: MutableList<Chunk>
    var lastTwoChunks = mutableListOf(0, 1)
    var easyChunksIdx: Int = 0
    var medChunksIdx: Int = 0
    var hardChunksIdx: Int = 0

    fun loadChunksFromFile(gc: GraphicsContext, file: String) {
        //load the chunks file
        val lines = File(file).useLines{it -> it.toList()}
        possibleChunks = mutableListOf()

        var offset = 0.0
        var chunkLines = mutableListOf<String>()
        var curIdx = 0
        //iterate through the file, adding chunks as they appear
        for (l in lines) {
            when (l) {
                "// BEGIN EASY CHUNKS" -> easyChunksIdx = curIdx+1
                "// BEGIN MEDIUM CHUNKS" -> medChunksIdx = curIdx+1
                "// BEGIN HARD CHUNKS" -> hardChunksIdx = curIdx+1
            }
            if (l == "" || l[0] == '/') continue
            val split = l.split(",")
            if (split.size == 1) {
                if(chunkLines.size > 1) {
                    possibleChunks.add(createChunkFromStrings(gc, offset, chunkLines))
                    chunkLines = mutableListOf()
                    curIdx++
                }
                offset = l.split(" ")[0].toDouble()
                continue
            }
            chunkLines.add(l)
        }
        println("$easyChunksIdx, $medChunksIdx $hardChunksIdx")
        possibleChunks.add(createChunkFromStrings(gc, offset, chunkLines))
    }

    //function to get sublist of chunks from possible chunks
    fun getChunks(startIdx: Int, endIdx: Int): MutableList<Chunk> {
        var chunks = mutableListOf<Chunk>()
        for (i in startIdx..endIdx) {
            chunks.add(possibleChunks[i].clone())
        }
        return chunks
    }

    //function to get a new random chunk, based on the player distance
    fun genChunk(scrollDist: Double): Chunk {
        var possibleRange = if(scrollDist < 5000) {
            easyChunksIdx..medChunksIdx-1
        } else if(scrollDist < 9000) {
            medChunksIdx..hardChunksIdx-1
        } else {
            hardChunksIdx..possibleChunks.lastIndex
        }
        var chunk = possibleRange.random()
        while (chunk == lastTwoChunks[0] && chunk == lastTwoChunks[1]) {
            chunk = possibleRange.random()
        }
        lastTwoChunks[0] = lastTwoChunks[1]
        lastTwoChunks[1] = chunk
        return possibleChunks[chunk].clone()
    }

    //function to create a chunk from a string
    fun createChunkFromStrings(gc: GraphicsContext, offset: Double, lines: List<String>): Chunk {
        var tiles = mutableListOf<Tile>()
        //for each non-zero number in the string, add a new tile with the number (type)
        for(i in 0..lines.lastIndex) {
            val lSplit = lines[i].split(",")
            for (j in 0..lSplit.lastIndex) {
                //skip empty lines
                if (lSplit[i] == "") continue
                val type = lSplit[j].toInt()
                //zeroes are empty tiles
                if (type == 0) continue
                tiles.add(Tile(j, i, type))
            }
        }

        return Chunk(gc, offset, tiles)
    }
}