package josh.icsplatformer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

class MainApp : Application() {
    private lateinit var gameloop: GameLoop
    private lateinit var stage: Stage
    private lateinit var menuScene: Scene
    private lateinit var menuController: Menu
    private lateinit var gameScene: Scene
    private var begunThread = false

    override fun start(stage: Stage) {
        this.stage = stage

        val fMenu = File("src/main/resources/josh/icsplatformer/menu.fxml")
        val menuLoader = FXMLLoader(fMenu.toURI().toURL())
        menuScene = Scene(menuLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT)
        menuController = menuLoader.getController<Menu>()
        menuController.setCallback{event ->
            switchToGame()
        }

        val f = File("src/main/resources/josh/icsplatformer/main-view.fxml")
        val gameFxmlLoader = FXMLLoader(f.toURI().toURL())
        gameScene = Scene(gameFxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT)
        gameFxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D.isImageSmoothing = false

        val keyListener = KeyListener(gameScene)
        gameloop = GameLoop({-> switchToMenu()}, gameFxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D, keyListener)

        stage.title = "ICS-Platformer"
        stage.scene = menuScene
        stage.isResizable = false
        stage.requestFocus()

        stage.show()
    }

    override fun stop() {
        println("stopping")
        if(this::gameloop.isInitialized) {
            gameloop.end()
        }
    }

    fun switchToMenu() {
        menuController.loadHighScores()
        stage.scene = menuScene
    }
    fun switchToGame() {
        println("switching to game")
        gameloop.start()
        if(!begunThread) {
            gameloop.beginThread()
            begunThread = true
        }
        stage.scene = gameScene
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}