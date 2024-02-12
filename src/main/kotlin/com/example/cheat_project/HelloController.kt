package com.example.cheat_project

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import javax.imageio.ImageIO
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class HelloController {
    companion object {
        var shooter: ScreenShoter? = null
    }
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
    @FXML
    private lateinit var timerLabel: Label
    @FXML
    private lateinit var infoErrorLabel: Label
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
    private fun timer(minute: Int) {
        if (thread == null) {
            var minute = minute - 1
            thread = Thread {
                while (minute >= 0) {
                    for (sec in 59 downTo 0) {
                        var Sec = if (sec < 10) "0$sec" else sec.toString()
                        var Time: String = "$minute:$Sec"
                        try {
                            Platform.runLater {
                                timerLabel.text = Time
                                timerLabel.alignment = Pos.CENTER
                            }
                            Thread.sleep(1000)
                        } catch (ex: java.lang.Exception) {
                            // Handle the exception
                        }
                    }
                    minute--
                }
                var info: String = "Генерация доступна!"
                Platform.runLater {
                    timerLabel.text = info
                }
            }
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
            val code = getCodeFromServer()
            codeGenText.text = code;
            codeInsertText.text = code
            thread?.interrupt()
            thread = null
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
        println(key) // print code in console
        if (checkCode(key)){
            if (shooter != null) {
                shooter!!.stopThread()
                shooter = null
            }
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

        val codeToCheck = code
        val result = checkCodeOnServer(codeToCheck)

        if (result) {
            println("Код найден на сервере.")
            return true
        } else {
            infoErrorLabel.text = "Код не найден!"
            infoErrorLabel.alignment = Pos.CENTER
            return false
        }
    }

    fun getCodeFromServer() : String {

        val serverUrl = "http://10.193.92.98:8000"  // Замените на фактический URL сервера

        val url = URL("$serverUrl/get_code")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        var message : String = ""
        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line).append('\n')
            }

            reader.close()
            message = response.toString()
        } finally {
            connection.disconnect()
        }

        return message
    }
    fun checkCodeOnServer(code: String): Boolean {
        val serverUrl = "http://10.193.92.98:8000"  // Замените на фактический URL сервера

        val url = URL("$serverUrl/verify_code/$code")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        try {
            return connection.responseMessage == "OK"
        } finally {
            connection.disconnect()
        }
    }
}