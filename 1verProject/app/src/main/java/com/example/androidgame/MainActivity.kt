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

    //button array
    val buttons = listOf<View>(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11,
        btn12, btn13, btn14, btn15,btn16)

    var temp = 0


    private val disposable = CompositeDisposable()
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }
    var boxes: ArrayList<Button> = arrayListOf()
    private var index16 = 0


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

        /*
        Log.d("hg", "random number")
//        println()
//        for (x in randomNo) {
//            println(x)
//        }

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
        println(index16)
        boxes[index16].setBackgroundColor(Color.parseColor("#87CEFA"))
        boxes[index16].setText("")
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
}