package com.example.androidgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
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
            randomNo.shuffle()

        }
        Log.d("hg", "random number")
        println()
        for (x in randomNo) {
            println(x)
        }

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
        button_start.isEnabled = !boolean
        button_reset.isEnabled = boolean
    }


}