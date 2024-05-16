package josh.icsplatformer

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import josh.icsplatformer.lib.Hitbox
import josh.icsplatformer.lib.Vec2

class GameLoop(private val gc: GraphicsContext) : AnimationTimer() {
    private var lastTick: Long = System.nanoTime()
    private var isPaused: Boolean = false

    private var player = Player(gc, Hitbox(Vec2(10.0, 200.0), Vec2(30.0, 250.0)))

    override fun handle(now: Long) {
        if (!isPaused) {
            val msSinceLastFrame = (now - lastTick)/1e6
            tick(msSinceLastFrame)
        }

        lastTick = now
    }

    private fun tick(msSinceLastFrame: Double) {
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
        gc.fill = Color.BLUE
        gc.fillRect(10.0, 10.0, 100.0, 100.0)

        player.update(msSinceLastFrame)
        player.show()
    }

}