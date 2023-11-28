package com.example.cheat_project

import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane

class RecieverController {
    lateinit var labelCodeDisplay: Label
    lateinit var buttonToFolder: Button
    lateinit var imageDisplay: ImageView
    lateinit var buttonGoToMenuReciever: Button
    lateinit var scrollImages: AnchorPane

    lateinit var codeString: String

    @FXML
    fun displayCode(code: String) {
        codeString = code
        labelCodeDisplay.text = code
    }
}