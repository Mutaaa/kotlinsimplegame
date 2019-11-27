package com.example.androidgame

import android.content.Intent
import android.graphics.Color
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.Preference
import androidx.preference.PreferenceManager

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getString("reply", "<unset>") == "orange"){
            val layout = findViewById<ConstraintLayout>(R.id.menuLayout)
            layout.setBackgroundColor(Color.parseColor("#ff9c34"))
        }else if(prefs.getString("reply", "<unset>") == "blue"){
            val layout = findViewById<ConstraintLayout>(R.id.menuLayout)
            layout.setBackgroundColor(Color.parseColor("#87CEFA"))
        }else{
            val layout = findViewById<ConstraintLayout>(R.id.menuLayout)
            layout.setBackgroundColor(Color.parseColor("#FFC0CB"))
        }
    }

    fun start3x3(view: View) {
        val intent = Intent(this, Mode3Activity::class.java)
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
