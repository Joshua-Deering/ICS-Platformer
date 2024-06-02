package josh.icsplatformer.map

import javafx.scene.image.Image
import kotlin.io.path.Path

object MapTextures {
    val textures: List<Image> = listOf(
        Image(Path("src/main/resources/sprites/test.png").toAbsolutePath().toUri().toURL().toString()),
    )
}