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

class HelloController {
    var shooter: ScreenShoter? = null
    lateinit var toMenu2: Button
    private var firstTime: Long = 0
    lateinit var connectButton: Button
    lateinit var getCode: TextField
    lateinit var back: Button
    lateinit var codeInsertButton: Button
    lateinit var codeGenButton: Button
    private var thread : Thread? = null
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


    private fun timer(minute : Int){
        if (thread == null){
            var minute = minute - 1
            thread = Thread{
                while (minute >= 0){
                    for (sec in 59 downTo 0){
                        var Sec = ""
                        if (sec < 10){
                            Sec = "0" + sec.toString()
                        }
                        else{
                            Sec = sec.toString()
                        }
                        var Time: String = "$minute:$Sec"
                        println(Time)
                        Thread.sleep(1000)
                    }
                    minute--
                }}
            thread!!.start()
        }
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
        if (waitOrWork()){
            var code = ""
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            code = (1..10).map { allowedChars.random() }.joinToString("")
            codeGenText.text = code;
            codeInsertText.text = code
            val current = LocalTime.now().plusHours(6)
            val formatted = DateTimeFormatter.ofPattern("HH:mm:ss")
            val timeNow = current.format(formatted)
            val filename = "databaseCode.txt"
            val finalString = "$code $timeNow\n"
            File(filename).appendText(finalString)
            timer(1)
        }
    }

    @FXML
    private fun inputCode() {
        code = getCode.text
        if (checkCode(code)){
            getCode.scene.window.hide()
            val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("getter-view.fxml"))
            val scene = Scene(fxmlLoader.load())
            val stage = Stage()
            stage.scene = scene
            stage.setOnShown {
                fxmlLoader.getController<RecieverController>().startController(code)
            }
            stage.show()
        }
    }

    private fun waitOrWork(): Boolean{
        val timeNow = System.currentTimeMillis()
        if (timeNow - 60000 >= firstTime){
            firstTime = timeNow
            return true
        }
        else{
            return false
        }
    }
    @FXML
    private fun connectToSession() {
        val key = codeInsertText.text
        println(key)
        if (key.length == 10) {
            println(key)
            println(key.length)
            shooter = ScreenShoter(key)
        }
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
        val current = LocalTime.now()
        val formatted = DateTimeFormatter.ofPattern("HH:mm:ss")
        val timeNow = current.format(formatted)
        val filename = File("databaseCode.txt")
        val bufferedReader = filename.bufferedReader()
        val text:List<String> = bufferedReader.readLines()
        for(line in text){
            val keyAndTime = line.split(" ").toTypedArray()
            if (keyAndTime.elementAt(0) == code){
                val endTime = keyAndTime.elementAt(1)
                if (endTime >= timeNow){
                    return true
                }
            }
        }
        return false
    }
}