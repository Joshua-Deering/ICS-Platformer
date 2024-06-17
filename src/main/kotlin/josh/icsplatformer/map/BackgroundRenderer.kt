/*
 * BackgroundRenderer.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * helper class to manage rendering all layers of the parallax background
 */


package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import josh.icsplatformer.SCREEN_HEIGHT
import josh.icsplatformer.SCREEN_WIDTH
import kotlin.io.path.Path

/**
 * helper class to render all 10 layers of parallax to the background
 * @param scrollSpeed how fast the background scrolls
 */
class BackgroundRenderer(val scrollSpeed: Double = 0.0) {
    val layers = listOf(
        Image(Path("src/main/resources/Background Layers/Layer_0011.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0010.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0009.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0008.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0007.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0006.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0005.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0004.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0003.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0002.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0001.png").toAbsolutePath().toUri().toURL().toString()),
        Image(Path("src/main/resources/Background Layers/Layer_0000.png").toAbsolutePath().toUri().toURL().toString()),
    )
    val offsets = mutableListOf<Double>(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)

    /**
     * draw this background to the given graphics context
     * @param gc graphics context to draw the background to
     * @param startLayer the layer of the background to start on
     * @param endLayer the layer of the background to end on
     * @param shouldMove whether to move the backgrounds in this frame
     */
    fun showBackground(gc: GraphicsContext, startLayer: Int, endLayer: Int, shouldMove: Boolean) {
        for (i in startLayer..endLayer) {
            //move the background
            if(shouldMove) offsets[i] -= scrollSpeed * i * 0.01
            //draw the background
            gc.drawImage(layers[i], 0.0, 396.0, 928.0, 306.0, 0.0 + offsets[i], 0.0, SCREEN_WIDTH + 90.0, SCREEN_HEIGHT)
            gc.drawImage(layers[i], 0.0, 396.0, 928.0, 306.0, 0.0 + offsets[i] + SCREEN_WIDTH + 90.0, 0.0, SCREEN_WIDTH + 90.0, SCREEN_HEIGHT)
            //if the background has gone offscreen, move it back over to the right side of the screen
            if (offsets[i] < -(SCREEN_WIDTH+90.0)) {
                offsets[i] = 0.0
            }
        }
    }
}