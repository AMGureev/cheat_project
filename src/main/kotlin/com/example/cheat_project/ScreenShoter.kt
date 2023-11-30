package com.example.cheat_project

import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ScreenShoter (
    clientId: String
) {
    val serverUrl = "http://10.193.91.153:8000"  // Replace with the actual server URL
    //val clientId : String = "id"
    val imageUrl = "$serverUrl/$clientId"
    var startTime : Long = System.currentTimeMillis()

    init {
        initializeShooter()
        println("Shooter created")
    }
    private fun initializeShooter() {
        startTime = System.currentTimeMillis()
        val thread = Thread{
            while (true) {
                startProccess()
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    throw RuntimeException()
                }
            }
        }
        thread.start()
    }
    private fun startProccess() {
        println("In proccess")
        val interval : Long = 10000
        var endTime = System.currentTimeMillis()
        if (endTime - startTime > interval) {
            try {
                val robot = Robot()
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                val screenRect = Rectangle(screenSize)
                val screenshot = robot.createScreenCapture(screenRect)
                val fileName = "src/images/screenshot.png"
                val outputFile = File(fileName)
                ImageIO.write(screenshot, "png", outputFile)
                println("Скриншот сохранен в: ${outputFile.absolutePath}")
                sendToServer(fileName)
                //println("File was sent successfully: ${outputFile.absolutePath}")
            } catch (e: Exception) {
                println("Ошибка при создании скриншота: ${e.message}")
            }
            startTime = endTime
        }
    }

    private fun sendToServer(name: String) {
        val filePath = name

        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection

            // Set connection properties
            connection.doOutput = true
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=${System.currentTimeMillis()}")

            val outputStream = DataOutputStream(connection.outputStream)

            // Add file part
            val fileName = File(filePath).name
            val fileInputStream = FileInputStream(filePath)
            val boundary = "*****"
            val lineEnd = "\r\n"

            outputStream.writeBytes("--$boundary$lineEnd")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$lineEnd")
            outputStream.writeBytes(lineEnd)

            val bufferSize = 4096
            val buffer = ByteArray(bufferSize)
            var bytesRead: Int

            while (fileInputStream.read(buffer, 0, bufferSize).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.writeBytes(lineEnd)
            outputStream.writeBytes("--$boundary--$lineEnd")

            // Close streams
            fileInputStream.close()
            outputStream.flush()
            outputStream.close()

            // Get the server response
            val responseCode = connection.responseCode
            val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            val response = StringBuilder()

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Close streams
            reader.close()
            inputStream.close()

            // Print server response
            println("Server Response: $response")

            // Close the connection
            connection.disconnect()

        } catch (e: Exception) {
            println("File $filePath$ was not sent")
        }
    }
}