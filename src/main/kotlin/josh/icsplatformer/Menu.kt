package josh.icsplatformer

import javafx.event.ActionEvent

class Menu {
    lateinit var callback: (event: ActionEvent) -> Unit

    fun buttonPressed(event: ActionEvent) {
        callback.invoke(event)
    }

    fun setCallback(cb: (event: ActionEvent) -> Unit): Boolean {
        callback = cb
        return true
    }
}