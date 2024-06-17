/*
 * Controller.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * controller for the main game screen
 */

package josh.icsplatformer

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.AnchorPane

/**
 * controller for the main game scene
 */
class Controller {
    @FXML
    private lateinit var gameAnchor: AnchorPane
    @FXML
    lateinit var gameCanvas: Canvas

    fun initialize() {
        //on initialization, bind the canvas width and height to the parent's width and height
        gameCanvas.widthProperty().bind(gameAnchor.widthProperty())
        gameCanvas.heightProperty().bind(gameAnchor.heightProperty())
    }
}