package com.example.androidgame

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.merge


private const val MAXIMUM_STOP_WATCH_LIMIT = 3600L
private const val NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60


class MainActivity : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    val stupidThing = "glupiaRzecz"

    //initiate array for random number
    val randomNo = ArrayList<Int>()


    private val disposable = CompositeDisposable()
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }
    var boxes: ArrayList<Button> = arrayListOf()
    private var index16 = 0
    var clickedButtonText = String()
    var clickedButtonLocation = -1
    var locationX = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        ref = FirebaseDatabase.getInstance().reference
        val stupidId = ref.push().key.toString()
        ref.child(stupidId).setValue(stupidThing)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        if (supportActionBar != null)
            supportActionBar?.hide()

        mergeClicks().switchMap {
            if (it) timerObservable()
            else Observable.just(displayInitialState)
        }.subscribe(text_view_countdown::setText)
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
    }

    fun startGame() {

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
        boxes[index16].setBackgroundColor(Color.parseColor("#87CEFA"))
        boxes[index16].setText("X")
        locationX = index16
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
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
            else "${secs / NUMBER_OF_SECONDS_IN_ONE_MINUTE} : ${secs % NUMBER_OF_SECONDS_IN_ONE_MINUTE}"
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

    public fun buttonClick(view: View){
        var button = view as Button
        //println(button.text)
        if(button.text == "X"){
            if(locationX+1 < 16 && boxes[locationX+1].text == clickedButtonText){
                boxes[locationX+1].setBackgroundColor(Color.parseColor("#87CEFA"))
                boxes[locationX+1].setText("X")
                boxes[locationX].setText(clickedButtonText)
                boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                locationX = locationX+1
                index16 = locationX
                clickedButtonText = ""
            } else if (locationX-1 >= 0 && boxes[locationX-1].text == clickedButtonText) {
                boxes[locationX-1].setBackgroundColor(Color.parseColor("#87CEFA"))
                boxes[locationX-1].setText("X")
                boxes[locationX].setText(clickedButtonText)
                boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                locationX = locationX-1
                index16 = locationX
                clickedButtonText = ""
            } else if (locationX-4 >= 0 && boxes[locationX-4].text == clickedButtonText){
                boxes[locationX-4].setBackgroundColor(Color.parseColor("#87CEFA"))
                boxes[locationX-4].setText("X")
                boxes[locationX].setText(clickedButtonText)
                boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                locationX = locationX-4
                index16 = locationX
                clickedButtonText = ""
            } else if (locationX+4 < 16 && boxes[locationX+4].text == clickedButtonText) {
                boxes[locationX+4].setBackgroundColor(Color.parseColor("#87CEFA"))
                boxes[locationX+4].setText("X")
                boxes[locationX].setText(clickedButtonText)
                boxes[locationX].setBackgroundResource(android.R.drawable.btn_default)
                locationX = locationX+4
                index16 = locationX
                clickedButtonText = ""
            }
        } else {
            clickedButtonText = button.text.toString()
            clickedButtonLocation = boxes.indexOf(button)
        }

        checkIfCorrect()
    }

    fun checkIfCorrect(){
        val arr = IntArray(14) { i -> 0 }
        val arr2 = IntArray(14) { i -> 1 }

        for(x in 1..14){
            //println(x.toString()+1)
            if(boxes[x-1].text == x.toString()){
                arr[x-1] = 1
            } else {
                arr[x-1] = 0
            }
        }

        val areEqual = arr.contentEquals(arr2)
        println(areEqual)

        if ( areEqual == true ) print("success")

    }


}