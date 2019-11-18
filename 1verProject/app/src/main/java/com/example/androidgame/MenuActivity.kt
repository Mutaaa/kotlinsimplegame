package com.example.androidgame

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun start3x3(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        view.context.startActivity(intent)
        /*
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)*/
    }


    fun start4x4(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        view.context.startActivity(intent)
        /*
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)*/
    }

    fun startSettings(view:View){
        val intent2 = Intent(this,SettingsActivity::class.java)
        view.context.startActivity(intent2)
    }



}
