package com.example.cheat_project

import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.spec.ECField
import java.io.BufferedInputStream

class RecieverController {
    val interval: Long = 3000
    lateinit var labelCodeDisplay: Label
    lateinit var buttonToFolder: Button
    lateinit var imageDisplay: ImageView
    lateinit var buttonGoToMenuReciever: Button
    lateinit var scrollImages: AnchorPane

    lateinit var codeString: String
    val serverUrl = "http://10.193.91.153:8000"  // Replace with the actual server URL
    var clientId : String = "id"
    val imageUrl = "$serverUrl/$clientId"
    val savePath = "src/images/downloaded/image_downloaded.png"
    var startTime: Long = 0


    @FXML
    fun startController(code: String) {
        codeString = code
        clientId = code
        labelCodeDisplay.text = code
        startTime = System.currentTimeMillis()
        val thread = Thread {
            while (true) {
                getImageContinuously()
                try {
                    Thread.sleep(1000);
                } catch (ex: java.lang.Exception) {
                    println("Thread error occurred!")
                }
            }
        }
        thread.start()
    }

    fun getImageContinuously() {
        val currTime = System.currentTimeMillis()
        if (currTime - startTime > interval) {
            try {
                val file = File("src/images/downloaded/image_downloaded.png")
                val image = Image(file.toURI().toString())
                downloadImage(imageUrl, savePath)
                imageDisplay.image = image
                println("Image was set")
            } catch (ex: Exception) {
                println("Can't get image from file")
            }
            startTime = currTime
        }
    }

    /*fun downloadImage(imageUrl: String, savePath: String) {
        val url = URL(imageUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val inputStream = connection.inputStream

            // Save the image to a file
            val fileOutputStream = FileOutputStream(savePath)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            fileOutputStream.close()
            println("Image downloaded successfully to $savePath")
        } catch (e: Exception) {
            println("ERROR during image download!")
        } finally {
            connection.disconnect()
        }
    }*/

    private fun downloadImage(imageUrl: String, savePath: String) {
        try {
            // Create URL object
            val url = URL(imageUrl)

            // Open a connection
            val connection = url.openConnection() as HttpURLConnection

            // Set the request method to GET
            connection.requestMethod = "GET"

            // Get the input stream
            val inputStream = BufferedInputStream(connection.inputStream)

            // Create a FileOutputStream to save the image
            val outputFile = FileOutputStream(savePath)

            // Read the input stream and write to the output file
            inputStream.use { input ->
                outputFile.use { fileOut ->
                    input.copyTo(fileOut)
                }
            }

            println("Image downloaded successfully.")
        } catch (e: Exception) {
            println("Failed to download image. Exception: $e")
        }
    }
}