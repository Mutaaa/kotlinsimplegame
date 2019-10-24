package com.example.androidgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    val stupidThing = "glupiaRzecz"

    override fun onCreate(savedInstanceState: Bundle?) {

        ref = FirebaseDatabase.getInstance().reference
        val stupidId = ref.push().key.toString()
        ref.child(stupidId).setValue(stupidThing)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null)
            supportActionBar?.hide()

    }
}
