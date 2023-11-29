package com.example.cheat_project

import kotlin.properties.Delegates

class ScreenShoter {

    init {
        val thread = Thread{
            startProccess()
        }
        thread.start()
    }
    private fun startProccess() {

    }
}