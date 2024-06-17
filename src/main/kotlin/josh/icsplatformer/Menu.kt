/*
 * Menu.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * controller for the menu screen
 */

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

/**
 * Menu controller class
 */
class Menu {
    //box for highscores
    @FXML
    lateinit var hsBox: VBox

    lateinit var callback: (event: ActionEvent) -> Unit

    /**
     * called when this menu screen is created
     */
    @FXML
    fun initialize() {
        //on initialization, load the highscores
        loadHighScores()
    }

    /**
     * loads the highscores from the highscores file
     */
    fun loadHighScores() {
        //if the highscores box is already populated, remove its contents
        if (hsBox.children.lastIndex >= 0) {
            hsBox.children.remove(0, hsBox.children.lastIndex)
            hsBox.children.removeAt(0)
        }
        //set the top label
        val hsLabel = Label("High Scores:")
        hsLabel.style =  "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Arial';"
        hsBox.children.add(hsLabel)

        //open and read the highscores file
        val hs = File("src/main/resources/highscores/highscores.txt")
        val lines = hs.readLines()
        for ((i, l) in lines.iterator().withIndex()) {
            //for each highscore, format it into readable numbers
            val splitStr = l.split(",")
            val date = LocalDateTime.parse(splitStr[1])
            val hourLabel = if(date.hour % 12 == 0) {12} else {date.hour % 12}
            val minuteLabel = if(date.minute < 10) {
                "0" + date.minute
            } else {
                date.minute.toString()
            }
            //then add the formatted highscore onto the list as a label
            val label = Label("${i+1}: ${splitStr[0].toDouble().roundToInt()} " +
                    "(${date.month.toString().lowercase().replaceFirstChar { c -> c.uppercase() }} ${date.dayOfMonth}, ${date.year}," +
                    " ${hourLabel}:${minuteLabel} ${if(date.hour > 12){"PM"}else{"AM"}})")
            label.style =  "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Arial';"
            hsBox.children.add(label)
        }
    }

    /**
     * calls the parent callback for changing screens when the button is pressed
     */
    fun buttonPressed(event: ActionEvent) {
        callback.invoke(event)
    }

    /**
     * setter method to set the parent callback
     */
    fun setCallback(cb: (event: ActionEvent) -> Unit): Boolean {
        callback = cb
        return true
    }
}