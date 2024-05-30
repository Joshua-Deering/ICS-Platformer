package josh.icsplatformer.map

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlin.io.path.Path

object MapTextures {
    val textures: List<ImageView> = listOf(
        ImageView(Image(Path("src/main/resources/sprites/test.png").toAbsolutePath().toUri().toURL().toString()))
    )
}