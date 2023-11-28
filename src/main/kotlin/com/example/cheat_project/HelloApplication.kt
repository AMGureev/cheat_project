package com.example.cheat_project

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        var fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        var scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
        stage.centerOnScreen()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}