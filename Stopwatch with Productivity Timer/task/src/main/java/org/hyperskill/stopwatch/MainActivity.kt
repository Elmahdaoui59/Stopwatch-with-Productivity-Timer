package org.hyperskill.stopwatch

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import org.hyperskill.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var handler2: Handler

    private lateinit var updateTime: Runnable
    private var isRunning: Boolean = false
    private val colors = arrayOf(Color.RED, Color.BLUE, Color.BLACK)
    private var color = colors[0]
    private lateinit var binding: ActivityMainBinding
    private var secs: Int = 1
    private var upperLimit: Int? = null


    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        handler2 = Looper.myLooper()?.let { Handler(it) }!!

        binding.progressBar.visibility = View.INVISIBLE
        binding.settingsButton.isEnabled = !isRunning
        secs = 1



        updateTime = Runnable {
            val minutes = secs / 60
            val seconds = secs % 60
            val s = String.format("%02d:%02d", minutes, seconds)
            color = colors[(colors.indexOf(color) + 1) % colors.size]
            secs++
            handler.post {
                binding.textView.text = s
                binding.progressBar.indeterminateTintList = ColorStateList.valueOf(color)
                upperLimit?.let {
                    if (secs > it) {
                        binding.textView.setTextColor(Color.RED)
                        notifyUpperLimit()
                        upperLimit = null
                    }
                }
            }
            handler2.postDelayed(updateTime, 1000)
        }

        binding.startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                handler2.postDelayed(updateTime, 1000)
                binding.progressBar.visibility = View.VISIBLE
                binding.settingsButton.isEnabled = false
            }
        }

        binding.resetButton.setOnClickListener {
            resetUi()
        }

        binding.settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(
                R.layout.dialog_main, null, false
            )
            android.app.AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                    upperLimit = try {
                        editText.text.toString().toInt()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_LONG)
                            .show()
                        null
                    }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> null }
                .show()
        }


    }

    override fun onStop() {
        super.onStop()
        resetUi()
    }

    @SuppressLint("SetTextI18n")
    private fun resetUi() {
        handler2.removeCallbacks(updateTime)
        handler.removeCallbacks { }
        binding.progressBar.visibility = View.INVISIBLE
        binding.textView.apply {
            text = "00:00"
            setTextColor(Color.GRAY)
        }
        upperLimit = null
        secs = 0
        isRunning = false
        binding.settingsButton.isEnabled = true

    }
    private fun notifyUpperLimit() {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID,
            NotificationManagement(this).notificationBuilder.build())
    }
}