package josh.icsplatformer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

class MainApp : Application() {
    private lateinit var gameloop: GameLoop

    override fun start(stage: Stage) {
        val f = File("src/main/resources/josh/icsplatformer/main-view.fxml")
        val fxmlLoader = FXMLLoader(f.toURI().toURL())
        val scene = Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT)
        stage.title = "ICS-Platformer"
        stage.scene = scene
        stage.isResizable = false
        stage.requestFocus()
        fxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D.isImageSmoothing = false

        val keyListener = KeyListener(stage.scene)
        gameloop = GameLoop(fxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D, keyListener)
        gameloop.start()

        stage.show()
    }

    override fun stop() {
        println("stopping")
        gameloop.stop()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}