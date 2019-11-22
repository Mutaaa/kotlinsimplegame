package com.example.androidgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.*
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.merge
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import androidx.constraintlayout.widget.ConstraintLayout





private const val MAXIMUM_STOP_WATCH_LIMIT = 3600L
private const val NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60

data class Score(
    var user: String? = "",
    var score: String? = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user" to user,
            "score" to score
        )
    }
}

class MainActivity : AppCompatActivity() {

    var name = "Player 1"
    lateinit var ref: DatabaseReference
    var userScore = "0"
    var checkIfStart = 0

    //initiate array for random number
    val randomNo = ArrayList<Int>()
    var scores: MutableList<String> = mutableListOf()
    var scor = "LOL"
    var highestString = ""
    private val disposable = CompositeDisposable()
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }
    var boxes: ArrayList<Button> = arrayListOf()
    private var index16 = 0
    var clickedButtonText = String()
    var clickedButtonLocation = -1
    var locationX = -1
    var scoreId = ""



    override fun onCreate(savedInstanceState: Bundle?) {

        //val intent = Intent(this, MenuActivity::class.java)
        // start your next activity
        //startActivity(intent)

        ref = FirebaseDatabase.getInstance().reference
        scoreReading(ref)

        val actionbar = supportActionBar
        actionbar!!.title = "New Activity"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)




        setContentView(R.layout.activity_main)

//        if (supportActionBar != null)
//            supportActionBar?.hide()

        mergeClicks().switchMap {
            if (it) timerObservable()
            else Observable.empty()
        }   .subscribe(text_view_countdown::setText)
            .let(disposable::add)

        for (i in 1..16) {
            randomNo.add(i)
        }

        boxes = arrayListOf(
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6),
            findViewById(R.id.btn7),
            findViewById(R.id.btn8),
            findViewById(R.id.btn9),
            findViewById(R.id.btn10),
            findViewById(R.id.btn11),
            findViewById(R.id.btn12),
            findViewById(R.id.btn13),
            findViewById(R.id.btn14),
            findViewById(R.id.btn15),
            findViewById(R.id.btn16)
        )

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getString("reply", "<unset>") == "green"){
            val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
            layout.setBackgroundColor(Color.parseColor("#98FB98"))

        }else if(prefs.getString("reply", "<unset>") == "blue"){
            val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
            layout.setBackgroundColor(Color.parseColor("#87CEFA"))
        }else{
            val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
            layout.setBackgroundColor(Color.parseColor("#FFC0CB"))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        return true
    }

    fun startGame() {

        text_view_countdown.isVisible = true
        checkIfStart = 1
        randomNo.shuffle()

        btn1.text = randomNo[0].toString()
        btn2.text = randomNo[1].toString()
        btn3.text = randomNo[2].toString()
        btn4.text = randomNo[3].toString()
        btn5.text = randomNo[4].toString()
        btn6.text = randomNo[5].toString()
        btn7.text = randomNo[6].toString()
        btn8.text = randomNo[7].toString()
        btn9.text = randomNo[8].toString()
        btn10.text = randomNo[9].toString()
        btn11.text = randomNo[10].toString()
        btn12.text = randomNo[11].toString()
        btn13.text = randomNo[12].toString()
        btn14.text = randomNo[13].toString()
        btn15.text = randomNo[14].toString()
        btn16.text = randomNo[15].toString()

        index16 = randomNo.indexOf(16)
        boxes[index16].setBackgroundColor(Color.parseColor("#FFFFFF"))
        boxes[index16].setText("X")
        locationX = index16
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.clear()
            super.onDestroy()
            buttonStateManager(false)
        }
    }

    private fun mergeClicks(): Observable<Boolean> =
        listOf(
            button_start.clicks().map { true },
            button_reset.clicks().map { false })
            .merge()
            .doOnNext(::buttonStateManager)

    private fun timerObservable(): Observable<String> =
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .takeWhile { it <= MAXIMUM_STOP_WATCH_LIMIT }
            .map(timeFormatter)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { buttonStateManager(false) }

    private val timeFormatter: (Long) -> String =
        { secs ->
            if (secs == MAXIMUM_STOP_WATCH_LIMIT) displayInitialState
            else "${secs / NUMBER_OF_SECONDS_IN_ONE_MINUTE}:${secs % NUMBER_OF_SECONDS_IN_ONE_MINUTE}"
        }

    private fun buttonStateManager(boolean: Boolean) {
        if(boolean) {
            this.startGame()

        }else{
            boxes[index16].setBackgroundResource(android.R.drawable.btn_default)
            boxes[index16].setText("16")

        }
        button_start.isEnabled = !boolean
        button_reset.isEnabled = boolean
    }

    fun scoreReading(reference: DatabaseReference){
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var highest = 99999
                 for (scoreSnapshot in dataSnapshot.children) {

                     var score = scoreSnapshot.child("score").getValue(String::class.java)
                     scor = score.toString()
                     val separate = scor.split(":")
                     val test = separate[0].toInt()*60 + separate[1].toInt()
                     if(test < highest && test != 0){
                         highest = test
                         highestString = scor
                     }
                }
                textView_highscore.setText("Highest score: "+highestString.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                println("Failed to read value.")
            }
        })
    }

    fun buttonClick(view: View){
        if(checkIfStart == 1) {
            var button = view as Button
            //println(button.text)
            if (button.text == "X") {
                if (locationX + 1 < 16 && boxes[locationX + 1].text == clickedButtonText) {
                    boxes[locationX + 1].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    boxes[locationX + 1].setText("X")
                    boxes[locationX].setText(clickedButtonText)
                    boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                    locationX = locationX + 1
                    index16 = locationX
                    clickedButtonText = ""
                } else if (locationX - 1 >= 0 && boxes[locationX - 1].text == clickedButtonText) {
                    boxes[locationX - 1].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    boxes[locationX - 1].setText("X")
                    boxes[locationX].setText(clickedButtonText)
                    boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                    locationX = locationX - 1
                    index16 = locationX
                    clickedButtonText = ""
                } else if (locationX - 4 >= 0 && boxes[locationX - 4].text == clickedButtonText) {
                    boxes[locationX - 4].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    boxes[locationX - 4].setText("X")
                    boxes[locationX].setText(clickedButtonText)
                    boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                    locationX = locationX - 4
                    index16 = locationX
                    clickedButtonText = ""
                } else if (locationX + 4 < 16 && boxes[locationX + 4].text == clickedButtonText) {
                    boxes[locationX + 4].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    boxes[locationX + 4].setText("X")
                    boxes[locationX].setText(clickedButtonText)
                    boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                    locationX = locationX + 4
                    index16 = locationX
                    clickedButtonText = ""
                }
            } else {
                clickedButtonText = button.text.toString()
                clickedButtonLocation = boxes.indexOf(button)
            }
            checkIfCorrect()
        }
    }

    fun checkIfCorrect(){
        //uncomment when want to test
        //val arr = IntArray(15) { i -> 1 }

        //comment when want to test
        val arr = IntArray(15) { i -> 0 }

        //dont need to do anything with this
        val arr2 = IntArray(15) { i -> 1 }

        //comment these when want to test
        for(x in 1..15){
            //println(x.toString()+1)
            if(boxes[x-1].text == x.toString()){
                arr[x-1] = 1
            } else {
                arr[x-1] = 0
            }
        }

        val areEqual = arr.contentEquals(arr2)
        println(areEqual)

        if ( areEqual == true ) {
            boxes[15].setText("16")
            boxes[index16].setBackgroundResource(android.R.drawable.btn_default)
            //onDestroy()
            scoreId = ref.push().key.toString()

            userScore = text_view_countdown.text.toString()
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            name = prefs.getString("signature", "<unset>").toString()

            if(!userScore.equals("0:0")) {
                val newScore = Score(name, userScore)
                ref.child(scoreId).setValue(newScore)
                println("Score " + userScore)
                button_reset.callOnClick()
            }
        } else { }
    }
    override fun onStop() {
        checkIfStart = 0
        disposable.dispose()
        super.onStop()
    }
}