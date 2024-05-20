package josh.icsplatformer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import java.net.URI

class MainApp : Application() {
    override fun start(stage: Stage) {
        val f = File("src/main/resources/josh/icsplatformer/main-view.fxml")
        val fxmlLoader = FXMLLoader(f.toURI().toURL())
        val scene = Scene(fxmlLoader.load(), 750.0, 450.0)
        stage.title = "ICS-Platformer"
        stage.scene = scene

        val gameloop = GameLoop(fxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D)
        gameloop.start()

        stage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}