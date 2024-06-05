package josh.icsplatformer

import javafx.scene.Scene
import javafx.scene.input.KeyCode

class KeyListener(scene: Scene) {
    private var keysDown: MutableSet<KeyCode> = mutableSetOf()
    var mouseX = 0.0
    var mouseY = 0.0

    init {
        scene.setOnMouseMoved { m ->
            mouseX = m.x
            mouseY = m.y
        }

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