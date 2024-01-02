package com.example.cheat_project

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.stage.Stage
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class HelloApplication : Application() {
    private var thread : Thread? = null
    override fun start(stage: Stage) {
        var fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        var scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        stage.title = "Cheat-project"
        stage.scene = scene
        stage.show()
        stage.centerOnScreen()
        startThread()
    }

    private val executor = Executors.newSingleThreadScheduledExecutor()

    private fun startThread() {
        executor.scheduleAtFixedRate(::clearOldCodeInDateBase, 0, 30, TimeUnit.MINUTES)
    }
    private fun clearOldCodeInDateBase() {
        thread = Thread {
            val current = LocalDateTime.now()
            val formatted = DateTimeFormatter.ofPattern("dd/MM/yy:HH:mm:ss")
            val filename = File("databaseCode.txt")
            val bufferedReader = filename.bufferedReader()
            val linesToKeep = mutableListOf<String>()

            bufferedReader.useLines { lines ->
                lines.forEach { line ->
                    val keyAndTime = line.split(" ").toTypedArray()
                    val endTime = keyAndTime.elementAt(1)
                    if (LocalDateTime.parse(endTime, formatted) > current) {
                        linesToKeep.add(line)
                    }
                }
            }
            // Write the lines that should be kept back to the file
            filename.bufferedWriter().use { writer ->
                linesToKeep.forEach { writer.write("$it\n") }
            }
        }
        thread!!.start()
        println("Очистка произведена!")
    }

}

fun main() {
    Application.launch(HelloApplication::class.java)
}//