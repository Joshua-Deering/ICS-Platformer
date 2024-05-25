package josh.icsplatformer

import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.entities.EntityManager
import josh.icsplatformer.entities.Player
import josh.icsplatformer.map.Chunk
import josh.icsplatformer.map.Tile
import josh.icsplatformer.map.TileMap
import java.awt.geom.Rectangle2D.Double as Rect

//nanoseconds per tick/render
const val TIME_PER_TICK = 1e9 / 120.0
const val TIME_PER_RENDER = 1e9 / 60.0

class GameLoop(private val gc: GraphicsContext, private val keyListener: KeyListener) {
    private lateinit var gameThread: Thread
    private var stopped = false

    private var paused: Boolean = false

    private var player = Player(gc, Rect(50.0, 100.0, 20.0, 50.0), keyListener = keyListener)
    private var tileMap = TileMap(
        gc,
        mutableListOf(
            Chunk(gc, 0.0, mutableListOf(
                Tile(1, 6, 1)),
                mutableListOf(Rect(50.0, 300.0, 50.0, 50.0))
            )
        )
    )
    private var entityManager = EntityManager(mutableListOf(player), tileMap)

    fun start() {
        gameThread = Thread {
            var lastTick = System.nanoTime()
            var lastRender = System.nanoTime()

            while (!stopped) {
                val dTick = System.nanoTime() - lastTick
                val dRender = System.nanoTime() - lastRender

                if(!paused && dTick > TIME_PER_TICK) {
                    tick(dTick/1e9)
                    lastTick = System.nanoTime()
                }
                if(!paused && dRender > TIME_PER_RENDER) {
                    render()
                    lastRender = System.nanoTime()
                }
            }
        }
        gameThread.start()
    }

    fun stop() {
        stopped = true
    }

    private fun render() {
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
        //RENDER
        tileMap.show()
        entityManager.show()
    }

    private fun tick(dt: Double) {
        //UPDATE
        tileMap.update(dt)
        entityManager.update(dt)

        //COLLISION MANAGEMENT
        entityManager.checkCollisions()
    }

}