package josh.icsplatformer

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import josh.icsplatformer.entities.EntityManager
import josh.icsplatformer.entities.Player
import java.awt.geom.Rectangle2D.Double as Rect

class GameLoop(private val gc: GraphicsContext, private val keyListener: KeyListener) : AnimationTimer() {
    private var lastTick: Long = System.nanoTime()
    private var isPaused: Boolean = false

    private var player = Player(gc, Rect(50.0, 100.0, 20.0, 50.0), keyListener = keyListener)
    private var tileMap = TileMap(gc,
        mutableListOf(Tile(1, 6, 1)),
        mutableListOf(Rect(50.0, 300.0, 50.0, 50.0)))
    private var entityManager = EntityManager(mutableListOf(player), tileMap)

    override fun handle(now: Long) {
        if (!isPaused) {
            //divided by 1e9 to convert from nanoseconds to seconds
            val msSinceLastFrame = (now - lastTick)/1e9
            tick(msSinceLastFrame)
        }

        lastTick = now
    }

    private fun tick(dt: Double) {
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)

        //UPDATE
        tileMap.update(dt)
        entityManager.update(dt)

        //COLLISION MANAGEMENT
        entityManager.checkCollisions()

        //RENDER
        tileMap.show()
        entityManager.show()
    }

}