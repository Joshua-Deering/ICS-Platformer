/*
 * GameLoop.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * Main game loop, manages everything for the actual game
 */

package josh.icsplatformer

import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import josh.icsplatformer.entities.EntityManager
import josh.icsplatformer.entities.Player
import josh.icsplatformer.map.BackgroundRenderer
import josh.icsplatformer.map.ChunkLoader
import josh.icsplatformer.map.TileMap
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt
import java.awt.geom.Rectangle2D.Double as Rect

//nanoseconds per tick/render
const val TIME_PER_TICK = 1e9 / 120.0
const val TIME_PER_RENDER = 1e9 / 60.0

/**
 * main game loop, manages rendering and updating the whole game scene
 * @property parentCallback callback to be called when the scene needs to be changed
 * @property gc the graphics context to render everything to
 * @property keyListener the key listener for this scene, for keyboard input
 */
class GameLoop(val parentCallback: () -> Unit, private val gc: GraphicsContext, private val keyListener: KeyListener) {
    //variables for stopping and changing scenes
    private var gameThread: Thread
    private var stopped = false

    //game variables
    var chunkLoader = ChunkLoader()
    private lateinit var player: Player
    private lateinit var tileMap: TileMap
    private lateinit var entityManager: EntityManager
    private var backgroundRenderer = BackgroundRenderer(10.0)

    //create the thread, to be started later
    init {
        gameThread = thread(start = false, block = {
            //store last tick and render time
            var lastTick = System.nanoTime()
            var lastRender = System.nanoTime()

            while (!GAME_STOPPED) {
                //if the game is stopped, dont update
                if(stopped) {
                    lastTick = System.nanoTime()
                    lastRender = System.nanoTime()
                    continue
                }
                val dTick = System.nanoTime() - lastTick
                val dRender = System.nanoTime() - lastRender

                //if it is time for the next tick, call the tick method
                if(dTick > TIME_PER_TICK) {
                    //platform.runlater forces javafx to run these methods on the JavaFX thread,
                    //to prevent the random crashing
                    Platform.runLater(Runnable{tick(dTick/1e9)})
                    lastTick = System.nanoTime()
                }
                //if it is time for the next render, call the render method
                if(dRender > TIME_PER_RENDER) {
                    //platform.runlater forces javafx to run these methods on the JavaFX thread,
                    //to prevent the random crashing
                    Platform.runLater(Runnable{render()})
                    lastRender = System.nanoTime()
                }
            }
        })
    }

    /**
     * starts the thread for the gameloop
     */
    fun beginThread() {
        gameThread.start()
    }

    /**
     * starts or restarts an instance of this gameloop
     */
    fun start() {
        //initialize all the game variables
        chunkLoader = ChunkLoader()
        chunkLoader.loadChunksFromFile(gc, "src/main/resources/tilemaps/chunks.txt")
        tileMap = TileMap(chunkLoader, mutableListOf(chunkLoader.possibleChunks[0].clone(), chunkLoader.possibleChunks[1].clone()), -60.0)
        player = Player(gc, tileMap, Rect(230.0, 240.0, 30.0, 36.0), keyListener = keyListener)
        entityManager = EntityManager(mutableListOf(player), tileMap)
        stopped = false
    }

    /**
     * temporarily stops this gameloop instance
     */
    fun stop() {
        //clear the screen and reset keylistener
        stopped = true
        gc.clearRect(0.0, 0.0, SCREEN_WIDTH, SCREEN_HEIGHT)
        keyListener.reset()

        //write this score to the highscores file
        val hs = File("src/main/resources/highscores/highscores.txt")
        hs.createNewFile()
        val fileStr = hs.readLines().toMutableList()
        //add the current score to the list of scores
        fileStr.add("${tileMap.scrollDist.absoluteValue},${LocalDateTime.now()}")
        //read the file into numbers
        val fileNums: MutableList<Pair<Double, String>> = fileStr.map{ str ->
            Pair(str.split(",")[0].toDouble().absoluteValue, str.split(",")[1])
        }.toMutableList()
        //sort the scores
        fileNums.sortBy{x -> x.first}
        //write the top ten scores back to the highscores file
        var writeStr = ""
        for (i in (max(fileNums.lastIndex-9, 0)..fileNums.lastIndex).reversed()) {
            writeStr += "${fileNums[i].first},${fileNums[i].second}\n"
        }
        //write the text to the file
        hs.writeText(writeStr)
    }

    /**
     * ends this gameloop instance
     */
    fun end() {
        GAME_STOPPED = true
    }

    /**
     * renders all game objects to the screen
     */
    private fun render() {
        //first clear the canvas
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
        //render the parallax background
        backgroundRenderer.showBackground(gc, 0, 10, !player.dying)
        //RENDER
        tileMap.show()
        entityManager.show()

        //write the score to the screen
        gc.fill = Color.WHITE
        gc.font = Font("Arial", 20.0)
        gc.fillText("Score: ${tileMap.scrollDist.roundToInt().absoluteValue}", 10.0, 20.0)

        //if the player died/is dying, write their score to the screen in large text
        if (player.dying) {
            gc.fill = Color.WHITE
            gc.font = Font("Arial", 200.0)
            gc.fillText("Score:\n${tileMap.scrollDist.roundToInt().absoluteValue}", 10.0, 250.0)
        }
    }

    /**
     * updates all game objects
     * @param dt The time since the last tick
     */
    private fun tick(dt: Double) {
        //if the player has died, call the parant callback to switch back to the menu screen
        if (!player.alive) {
            stop()
            Platform.runLater(Runnable { parentCallback.invoke() })
        }

        //UPDATE
        //only update the map if the player is not currently dying
        if(!player.dying) tileMap.update(dt)
        entityManager.update(dt)

        //COLLISION MANAGEMENT
        entityManager.checkCollisions()
    }
}