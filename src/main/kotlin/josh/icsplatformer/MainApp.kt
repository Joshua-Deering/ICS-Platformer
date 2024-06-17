/*
 * MainApp.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * main app class, allows functionality of the rest of the components
 */

package josh.icsplatformer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

/**
 * Main app which manages each screen (game screen and menu screen)
 */
class MainApp : Application() {
    //variables for the game screen
    private lateinit var gameloop: GameLoop
    private lateinit var gameScene: Scene
    private var begunThread = false

    //variables for the menu screen
    private lateinit var menuScene: Scene
    private lateinit var menuController: Menu

    //variable for the current stage (the window)
    private lateinit var stage: Stage

    /**
     * called when this app starts
     * @param stage The stage object for the app
     */
    override fun start(stage: Stage) {
        this.stage = stage

        //load the menu screen
        val fMenu = File("src/main/resources/josh/icsplatformer/menu.fxml")
        val menuLoader = FXMLLoader(fMenu.toURI().toURL())
        menuScene = Scene(menuLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT)
        menuController = menuLoader.getController<Menu>()
        menuController.setCallback{event ->
            switchToGame()
        }

        //load the game screen
        val f = File("src/main/resources/josh/icsplatformer/main-view.fxml")
        val gameFxmlLoader = FXMLLoader(f.toURI().toURL())
        gameScene = Scene(gameFxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT)
        gameFxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D.isImageSmoothing = false

        //create a keylistener on the game scene
        val keyListener = KeyListener(gameScene)
        //create the gameloop
        gameloop = GameLoop({-> switchToMenu()}, gameFxmlLoader.getController<Controller>().gameCanvas.graphicsContext2D, keyListener)

        //set values for the window
        stage.title = "ICS-Platformer"
        stage.scene = menuScene
        stage.isResizable = false
        stage.requestFocus()

        //show the scene
        stage.show()
    }

    /**
     * called when the app is closed, ends gameloop thread
     */
    override fun stop() {
        //stop the gameloop, if it exists
        if(this::gameloop.isInitialized) {
            gameloop.end()
        }
    }

    /**
     * switches the window to the menu screen
     */
    fun switchToMenu() {
        menuController.loadHighScores()
        stage.scene = menuScene
    }

    /**
     * switches the window to the game screen
     */
    fun switchToGame() {
        //start the gameloop
        gameloop.start()
        //if the gameloop has not been started before, start it
        if(!begunThread) {
            gameloop.beginThread()
            begunThread = true
        }
        //show the scene
        stage.scene = gameScene
    }
}

//application entry point
fun main() {
    Application.launch(MainApp::class.java)
}