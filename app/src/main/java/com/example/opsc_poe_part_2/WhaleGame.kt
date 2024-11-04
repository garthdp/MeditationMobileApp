package com.example.opsc_poe_part_2

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer

class WhaleGame: AppCompatActivity() {
    private lateinit var airBar: ProgressBar
    private var airLevel = 100
    private val decreaseAmount = 1
    private lateinit var whaleImageView: ImageView
    private lateinit var net: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var rootLayout: View

    private var score = 0
    private var xpEarned = 0
    private val scoreHandler = Handler(Looper.getMainLooper())
    private val netHandler = Handler(Looper.getMainLooper())

    private var scoreRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whale_game)

        // Initialize air bar and bubbles
        airBar = findViewById(R.id.airBar)
        val bubble1 = findViewById<ImageView>(R.id.bubble1)
        val bubble2 = findViewById<ImageView>(R.id.bubble2)
        val bubble3 = findViewById<ImageView>(R.id.bubble3)
        whaleImageView = findViewById(R.id.whaleImageView)
        net = findViewById(R.id.net)
        scoreTextView = findViewById(R.id.scoreTextView)
        rootLayout = findViewById(R.id.rootLayout)

        whaleImageView.setBackgroundResource(R.drawable.whale_animation)
        val whaleAnimation = whaleImageView.background as AnimationDrawable
        whaleAnimation.start()

        startFloatingAnimation(bubble1, 5000)
        startFloatingAnimation(bubble2, 6000)
        startFloatingAnimation(bubble3, 7000)

        fixedRateTimer("decreaseAir", initialDelay = 0, period = 1000) {
            runOnUiThread {
                if (airLevel > 0) {
                    airLevel -= decreaseAmount
                    updateAirBar()
                }
            }
        }

        // Collect bubbles to replenish air
        bubble1.setOnClickListener { collectBubble(bubble1) }
        bubble2.setOnClickListener { collectBubble(bubble2) }
        bubble3.setOnClickListener { collectBubble(bubble3) }

        // Screen touch listener for whale movement
        findViewById<View>(R.id.bubbleContainer).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                moveWhaleToTouch(event.x, event.y)
                true
            } else {
                false
            }
        }

        startNetSpawning()
        startScoreCounting()
    }

    private fun startFloatingAnimation(bubble: ImageView, duration: Long) {
        val floatAnimation = TranslateAnimation(0f, 0f, 0f, -1500f).apply {
            this.duration = duration
            this.fillAfter = false
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    bubble.startAnimation(this@apply)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        bubble.startAnimation(floatAnimation)
    }

    private fun startNetSpawning() {
        net.visibility = View.VISIBLE
        netHandler.post(createNetRunnable())
    }

    private fun createNetRunnable(): Runnable {
        return object : Runnable {
            override fun run() {
                val fallSpeed = 10 + score / 5
                net.translationY += fallSpeed

                if (net.translationY > rootLayout.height) {
                    resetNet()
                } else if (isColliding(whaleImageView, net)) {
                    gameOver()
                } else {
                    netHandler.postDelayed(this, 30)
                }
            }
        }
    }

    private fun resetNet() {
        net.translationY = 0f
        net.translationX = (0..(rootLayout.width - net.width)).random().toFloat()
        netHandler.post(createNetRunnable())
    }

    private fun isColliding(view1: View, view2: View): Boolean {
        val location1 = IntArray(2)
        view1.getLocationOnScreen(location1)

        val location2 = IntArray(2)
        view2.getLocationOnScreen(location2)

        return !(location1[0] + view1.width < location2[0] ||
                location1[0] > location2[0] + view2.width ||
                location1[1] + view1.height < location2[1] ||
                location1[1] > location2[1] + view2.height)
    }

    private fun gameOver() {
        netHandler.removeCallbacksAndMessages(null)
        scoreHandler.removeCallbacksAndMessages(null)
        saveScore(score)
        var xp = 0
        if(25 < score && score < 50){
            xp = 50
        }
        else if(score >= 50){
            xp = 75
        }
        xpEarned = xp
        levelUp(xp)
        showGameOverDialog()
    }

    fun levelUp(score: Int) {
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val progressData = workDataOf(LevelUpWorker.EXPERIENCE to score.toString())

        Log.d("ProgressData", progressData.toString() + score.toString())
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<LevelUpWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()
        WorkManager.getInstance()
            .enqueue(request)
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Game Over")
            setMessage("Your score: $score\nXP earned: $xpEarned\nDo you want to play again?")
            setPositiveButton("Yes") { _, _ -> restartGame() }
            setNegativeButton("Quit") { _, _ -> finish() }
            setCancelable(true)
            setOnCancelListener { finish() }
            show()
        }
    }

    private fun restartGame() {
        score = 0
        updateScoreDisplay()
        resetNet()
        startNetSpawning()
        startScoreCounting()
    }

    private fun startScoreCounting() {
        scoreRunnable = object : Runnable {
            override fun run() {
                score++
                updateScoreDisplay()
                scoreHandler.postDelayed(this, 1000)
            }
        }
        scoreHandler.post(scoreRunnable!!)
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = "Score: $score"
    }

    private fun saveScore(score: Int) {
        getSharedPreferences("GamePrefs", MODE_PRIVATE).edit()
            .putInt("previousScore", score)
            .apply()
    }

    private fun updateAirBar() {
        airBar.progress = airLevel
        when {
            airLevel > 50 -> airBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
            airLevel in 1..50 -> airBar.progressTintList = ColorStateList.valueOf(Color.RED)
            airLevel <= 0 -> {
                airBar.progressTintList = ColorStateList.valueOf(Color.GRAY)
                airBar.alpha = 1f
                airBar.animate().alpha(0f).setDuration(500).withEndAction {
                    airBar.alpha = 1f
                }.start()
            }
        }
    }

    private fun collectBubble(bubble: ImageView) {
        bubble.animate().scaleX(0f).scaleY(0f).setDuration(300).withEndAction {
            bubble.visibility = View.INVISIBLE
        }.start()
        if (airLevel < 100) {
            airLevel += 10
            if (airLevel > 100) airLevel = 100
            updateAirBar()
        }

        bubble.postDelayed({
            bubble.visibility = View.VISIBLE
            bubble.scaleX = 1f
            bubble.scaleY = 1f
        }, 3000)
    }

    private fun moveWhaleToTouch(targetX: Float, targetY: Float) {
        val adjustedX = targetX - whaleImageView.width / 2
        val adjustedY = targetY - whaleImageView.height / 2

        ObjectAnimator.ofPropertyValuesHolder(
            whaleImageView,
            PropertyValuesHolder.ofFloat(View.X, adjustedX),
            PropertyValuesHolder.ofFloat(View.Y, adjustedY)
        ).apply {
            duration = 500
            start()
        }
    }
}