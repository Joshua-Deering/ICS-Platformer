package josh.icsplatformer.map

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import josh.icsplatformer.SCREEN_HEIGHT
import josh.icsplatformer.SCREEN_WIDTH
import kotlin.io.path.Path

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

    fun showBackground(gc: GraphicsContext, startLayer: Int, endLayer: Int) {
        for (i in startLayer..endLayer) {
            offsets[i] -= scrollSpeed * i * 0.01
            gc.drawImage(layers[i], 0.0, 396.0, 928.0, 306.0, 0.0 + offsets[i], 0.0, SCREEN_WIDTH + 90.0, SCREEN_HEIGHT)
            gc.drawImage(layers[i], 0.0, 396.0, 928.0, 306.0, 0.0 + offsets[i] + SCREEN_WIDTH + 90.0, 0.0, SCREEN_WIDTH + 90.0, SCREEN_HEIGHT)
            if (offsets[i] < -(SCREEN_WIDTH+90.0)) {
                offsets[i] = 0.0
            }
        }
    }
}