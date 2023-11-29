package com.example.cheat_project

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.io.FileWriter


class HelloController {
    lateinit var toMenu2: Button
    lateinit var connectButton: Button
    lateinit var getCode: TextField
    lateinit var back: Button
    lateinit var codeInsertButton: Button
    lateinit var codeGenButton: Button
    @FXML
    private lateinit var codeInsertText: TextField
    @FXML
    private lateinit var codeGenText: TextField
    @FXML
    private lateinit var welcomeText: Label
    @FXML
    private lateinit var nextButton: Button
    @FXML
    private lateinit var choiceBox: ChoiceBox<String>

    private lateinit var code: String

    @FXML
    private fun onHelloButtonClick() {
        nextButton.scene.window.hide()
        val fxmlLoader : FXMLLoader
        if (choiceBox.value == "Отправитель") {
            fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("generate-code.fxml"))
        } else {
            fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("input-code.fxml"))
        }
        var scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        var stage = Stage()
        stage.scene = scene
        stage.show()
    }

    @FXML
    private fun choiceBoxClick() {
        nextButton.isDisable = false;
    }

    @FXML
    private fun goBack() {
        back.scene.window.hide()
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        val loader = fxmlLoader.getController<RecieverController>()
        val scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        val stage = Stage()
        stage.scene = scene
        stage.show()
    }

    @FXML
    private fun goToMenu() {
        toMenu2.scene.window.hide()
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        val stage = Stage()
        stage.scene = scene
        stage.show()
    }

    @FXML
    private fun generateCode() {
        var code = ""
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        code = (1..10).map { allowedChars.random() }.joinToString("")
        codeGenText.text = code;
        val current = LocalTime.now().plusHours(6)
        val formatted = DateTimeFormatter.ofPattern("HH:mm:ss")
        val timeNow = current.format(formatted)
        val filename = "databaseCode.txt"
        val finalString = "$code $timeNow"
        println(File(filename).exists())
        addLineToFile(filename,finalString)
    }

    private fun addLineToFile(filePath: String, newLine: String) {
        try {
            val fileWriter = FileWriter(filePath, true)
            fileWriter.use { it.write("$newLine\n") }
        } catch (e: Exception) {
            println("Error writing to the file: $e")
        }
    }


    @FXML
    private fun inputCode() {
        code = getCode.text
        getCode.scene.window.hide()
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("getter-view.fxml"))
        val scene = Scene(fxmlLoader.load())
        val stage = Stage()
        stage.scene = scene
        stage.setOnShown {
            fxmlLoader.getController<RecieverController>().displayCode(code)
        }
        stage.show()
    }
    @FXML
    private fun screenShot() {
        try {
            val robot = Robot()
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val screenRect = Rectangle(screenSize)
            val screenshot = robot.createScreenCapture(screenRect)
            val outputFile = File("screenshot.png")
            ImageIO.write(screenshot, "png", outputFile)
            println("Скриншот сохранен в: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("Ошибка при создании скриншота: ${e.message}")
        }
    }

    private fun checkCode(code: String): Boolean{
        val timeNow = LocalTime.parse(LocalTime.now().toString(), DateTimeFormatter.ofPattern("H:m:ss"))
        val filename = File("com/example/cheat_project/databaseCode.txt")
        val bufferedReader = filename.bufferedReader()
        val text:List<String> = bufferedReader.readLines()
        for(line in text){
            val keyAndTime = line.split(" ").toTypedArray()
            if (keyAndTime.elementAt(0) == code){
                val endTime = LocalTime.parse(keyAndTime.elementAt(1), DateTimeFormatter.ofPattern("H:m:ss"))
                if (endTime >= timeNow){
                    return true
                }
            }
        }
        return false
    }
}