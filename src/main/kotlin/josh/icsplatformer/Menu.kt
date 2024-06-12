package josh.icsplatformer

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import java.io.File
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.math.min
import kotlin.math.roundToInt

class Menu {
    @FXML
    lateinit var hsBox: VBox

    lateinit var callback: (event: ActionEvent) -> Unit

    @FXML
    fun initialize() {
        loadHighScores()
    }

    fun loadHighScores() {
        if (hsBox.children.lastIndex >= 0) {
            hsBox.children.remove(0, hsBox.children.lastIndex)
            hsBox.children.removeAt(0)
        }
        val hsLabel = Label("High Scores:")
        hsLabel.style = "-fx-text-fill: black; -fx-font-size: 20px; -fx-font-family: 'Arial';"
        hsBox.children.add(hsLabel)

        val hs = File("src/main/resources/highscores/highscores.txt")
        val lines = hs.readLines()
        for (l in lines) {
            val splitStr = l.split(",")
            val date = LocalDateTime.parse(splitStr[1])
            val label = Label("${splitStr[0].toDouble().roundToInt()} (${date.month} ${date.year}, ${date.hour%12}:${date.minute} ${if(date.hour > 12){"PM"}else{"AM"}})")
            label.style = "-fx-text-fill: black; -fx-font-size: 20px; -fx-font-family: 'Arial';"
            hsBox.children.add(label)
        }
    }

    fun buttonPressed(event: ActionEvent) {
        callback.invoke(event)
    }

    fun setCallback(cb: (event: ActionEvent) -> Unit): Boolean {
        callback = cb
        return true
    }
}