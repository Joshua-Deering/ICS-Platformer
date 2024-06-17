/*
 * KeyListener.kt
 * Name: Joshua Deering
 * Student #: 334987377
 * Date: June 10, 2024
 * Class: 4U
 * Description:
 * keylistener, to keep track of keyboard and mouse input
 */

package josh.icsplatformer

import javafx.scene.Scene
import javafx.scene.input.KeyCode

/**
 * stores current state of the keyboard & mouse for use by game objects
 * @property scene the scene to track inputs on
 */
class KeyListener(scene: Scene) {
    private var keysDown: MutableSet<KeyCode> = mutableSetOf()
    var mouseX = 0.0
    var mouseY = 0.0

    init {
        //when the mouse moves, update its position variable
        scene.setOnMouseMoved { m ->
            mouseX = m.x
            mouseY = m.y
        }

        //when a key is pressed, register that a key was pressed in the list
        scene.setOnKeyPressed { key ->
            keysDown.add(key.code)
        }

        //if a key is released, remove it from the list of pressed keys
        scene.setOnKeyReleased { key ->
            keysDown.remove(key.code)
        }
    }

    /**
     * returns whether or not a key is currently pressed
     */
    fun keyDown(key: String): Boolean {
        //if the key is in the list, it is currently being pressed
        return keysDown.contains(KeyCode.valueOf(key))
    }

    /**
     * resets the keys-down list
     */
    fun reset() {
        keysDown = mutableSetOf<KeyCode>()
    }
}