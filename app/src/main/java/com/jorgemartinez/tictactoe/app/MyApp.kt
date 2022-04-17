package com.jorgemartinez.tictactoe.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
    /*
    init {
        produce un error con init
         FirebaseApp.initializeApp(this)
    }*/
}