package com.example.cheat_project

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.awt.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class HelloApplication : Application() {
    private var thread : Thread? = null
    override fun start(stage: Stage) {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

        when {
            osName.contains("win") -> println("Windows")
            osName.contains("mac") -> println("Unix or macOS")
            else -> println("Other operating system")
        }

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

fun stopApp() {
    exitProcess(0)
}

fun createTray() {
    val tray = SystemTray.getSystemTray()
    val image = Toolkit.getDefaultToolkit().getImage(".png")

    val trayIcon = TrayIcon(image, "---")
    trayIcon.isImageAutoSize = true

    val popupMenu = PopupMenu()

    /*val showItem = MenuItem("Show")
    showItem.addActionListener {
       /* Platform.runLater {
            stage.show()
            stage.toFront()
        }*/
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        var scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        var stage = Stage()
        stage.scene = scene
        stage.show()
    }*/

    val exitItem = MenuItem("Exit")
    exitItem.addActionListener {
        tray.remove(trayIcon)
        stopApp()
    }

    //popupMenu.add(showItem)
    popupMenu.addSeparator()
    popupMenu.add(exitItem)

    trayIcon.popupMenu = popupMenu

    tray.add(trayIcon)
}

fun main() {
    createTray()
    Application.launch(HelloApplication::class.java)
}//