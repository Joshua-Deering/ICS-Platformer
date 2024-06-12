package josh.icsplatformer

import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.entities.EntityManager
import josh.icsplatformer.entities.Player
import josh.icsplatformer.map.BackgroundRenderer
import josh.icsplatformer.map.ChunkLoader
import josh.icsplatformer.map.TileMap
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.min
import java.awt.geom.Rectangle2D.Double as Rect

//nanoseconds per tick/render
const val TIME_PER_TICK = 1e9 / 120.0
const val TIME_PER_RENDER = 1e9 / 60.0

class GameLoop(val parentCallback: () -> Unit, private val gc: GraphicsContext, private val keyListener: KeyListener) {
    private var gameThread: Thread
    private var stopped = false

    var chunkLoader = ChunkLoader()
    private lateinit var player: Player
    private lateinit var tileMap: TileMap
    private lateinit var entityManager: EntityManager
    private var backgroundRenderer = BackgroundRenderer(10.0)

    init {
        gameThread = thread(start = false, block = {
            var lastTick = System.nanoTime()
            var lastRender = System.nanoTime()

            while (!GAME_STOPPED) {
                if(stopped) {
                    lastTick = System.nanoTime()
                    lastRender = System.nanoTime()
                    continue
                }
                val dTick = System.nanoTime() - lastTick
                val dRender = System.nanoTime() - lastRender

                if(dTick > TIME_PER_TICK) {
                    tick(dTick/1e9)
                    lastTick = System.nanoTime()
                }
                if(dRender > TIME_PER_RENDER) {
                    render()
                    lastRender = System.nanoTime()
                }
            }
        })
    }

    fun beginThread() {
        gameThread.start()
    }

    fun start() {
        chunkLoader = ChunkLoader()
        chunkLoader.loadChunksFromFile(gc, "src/main/resources/tilemaps/chunks.txt")
        tileMap = TileMap(chunkLoader, chunkLoader.getChunks(0, 1), -40.0)
        player = Player(gc, tileMap, Rect(50.0, 100.0, 30.0, 36.0), keyListener = keyListener, tileMapScroll = -40.0)
        entityManager = EntityManager(mutableListOf(player), tileMap)
        stopped = false
    }

    fun stop() {
        stopped = true
        gc.clearRect(0.0, 0.0, SCREEN_WIDTH, SCREEN_HEIGHT)

        //write this score to the highscores file
        val hs = File("src/main/resources/highscores/highscores.txt")
        hs.createNewFile()
        val fileStr = hs.readLines().toMutableList()
        fileStr.add("${tileMap.scrollDist.absoluteValue},${LocalDateTime.now()}")
        println(fileStr)
        val fileNums: MutableList<Pair<Double, String>> = fileStr.map{ str ->
            Pair(str.split(",")[0].toDouble().absoluteValue, str.split(",")[1])
        }.toMutableList()
        fileNums.sortBy{x -> x.first}
        println(fileNums)
        var writeStr = ""
        for (i in (0..min(9, fileNums.lastIndex)).reversed()) {
            writeStr += "${fileNums[i].first},${fileNums[i].second}\n"
        }
        println(writeStr)
        hs.writeText(writeStr)
    }

    fun end() {
        GAME_STOPPED = true
    }

    private fun render() {
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
        backgroundRenderer.showBackground(gc, 0, 10)
        //RENDER
        tileMap.show()
        entityManager.show()
    }

    private fun tick(dt: Double) {
        if (!player.alive) {
            Platform.runLater(Runnable { parentCallback.invoke() })
            stop()
            println("player died")
        }

        //UPDATE
        tileMap.update(dt)
        entityManager.update(dt)

        //COLLISION MANAGEMENT
        entityManager.checkCollisions()
    }
}