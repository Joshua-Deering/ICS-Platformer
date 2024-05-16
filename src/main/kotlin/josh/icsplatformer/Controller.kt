package josh.icsplatformer

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.AnchorPane

class Controller {
    @FXML
    private lateinit var gameAnchor: AnchorPane
    @FXML
    lateinit var gameCanvas: Canvas

    @FXML
    fun initialize() {
        gameCanvas.widthProperty().bind(gameAnchor.widthProperty())
        gameCanvas.heightProperty().bind(gameAnchor.heightProperty())
    }
}