package josh.icsplatformer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainApp::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 750.0, 450.0)
        stage.title = "ICS-Platformer"
        stage.scene = scene

        val gameloop = GameLoop(fxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D);
        gameloop.start();

        stage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}