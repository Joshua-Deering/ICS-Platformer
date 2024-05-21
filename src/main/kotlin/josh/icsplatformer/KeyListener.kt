package josh.icsplatformer

import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class KeyListener(scene: Scene) {
    private var keysDown: MutableSet<KeyCode> = mutableSetOf()
    init {
        scene.setOnKeyPressed { key ->
            keysDown.add(key.code)
        }

        scene.setOnKeyReleased { key ->
            keysDown.remove(key.code)
        }
    }

    fun keyDown(key: String): Boolean {
        return keysDown.contains(KeyCode.valueOf(key))
    }
}