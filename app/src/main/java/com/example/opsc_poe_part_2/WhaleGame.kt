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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class WhaleGame : AppCompatActivity() {
    /*
Code Attribution
Title:How to pass values from a Class to Activity using Intents in Android Studio Tutorial 12 Total Score
Author: Coding Cafe
post link: https://www.youtube.com/watch?v=pAIC3JI1org
Usage: Learned how to make game

*/
    private lateinit var airBar: ProgressBar
    private var airLevel = 100
    private val decreaseAmount = 1
    private lateinit var whaleImageView: ImageView
    private lateinit var net: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var rootLayout: View
    private lateinit var joystickHandle: ImageView
    private lateinit var joystickContainer: View

    private var score = 0

    private var highScore = 0
    private var xpEarned = 0
    private val scoreHandler = Handler(Looper.getMainLooper())
    private val netHandler = Handler(Looper.getMainLooper())
    private val movementHandler = Handler(Looper.getMainLooper())

    private var scoreRunnable: Runnable? = null
    private var centerX = 0f
    private var centerY = 0f
    private var joystickRadius = 0f
    private var moveX = 0f
    private var moveY = 0f

    // Define bubbles list
    private lateinit var bubbles: List<Pair<ImageView, Long>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whale_game)

        joystickHandle = findViewById(R.id.joystickHandle)
        joystickContainer = findViewById(R.id.joystickContainer)

        whaleImageView = findViewById(R.id.whaleImageView)
        net = findViewById(R.id.net)
        scoreTextView = findViewById(R.id.scoreTextView)
        rootLayout = findViewById(R.id.rootLayout)

        // Initialize high score
        highScore = getHighScoreFromStorage() // Method to fetch high score
        findViewById<TextView>(R.id.highScoreTextView).text = "High Score: $highScore"

        setupJoystick()
        initializeGameComponents()
    }
    /*
    Code Attribution
    Title:Ep. 04 - Joystick and touch events | Android Studio 2D Game Development
    Author: Alex Bükk
    post link: https://www.youtube.com/watch?v=3oZ2jt0hQmo
    Usage: Learned how to make joystick

    */
    private fun setupJoystick() {
        joystickContainer.post {
            centerX = joystickContainer.width / 2f
            centerY = joystickContainer.height / 2f
            joystickRadius = min(joystickContainer.width, joystickContainer.height) / 2f

            joystickHandle.setOnTouchListener { _, event -> handleJoystickTouch(event) }
        }
        joystickHandle.setOnClickListener {}
    }
    /*
    Code Attribution
    Title:How to pass values from a Class to Activity using Intents in Android Studio Tutorial 12 Total Score
    Author: Coding Cafe
    post link: https://www.youtube.com/watch?v=pAIC3JI1org
    Usage: Learned how to make game

    */
    private fun saveScore(score: Int) {
        val sharedPreferences = getSharedPreferences("game_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("current_score", score)
        editor.apply()
    }

    private fun initializeGameComponents() {
        // Initialize animations
        whaleImageView.setBackgroundResource(R.drawable.whale_animation)
        val whaleAnimation = whaleImageView.background as AnimationDrawable
        whaleAnimation.start()

        // Initialize bubbles here
        bubbles = listOf(
            findViewById<ImageView>(R.id.bubble1) to 5000L,
            findViewById<ImageView>(R.id.bubble2) to 6000L,
            findViewById<ImageView>(R.id.bubble3) to 7000L
        )


        // Decrease air over time
        fixedRateTimer("decreaseAir", initialDelay = 0, period = 1000) {
            runOnUiThread {
                if (airLevel > 0) {
                    airLevel -= decreaseAmount

                }
            }
        }

        startNetSpawning()
        startScoreCounting()
    }

    private fun handleJoystickTouch(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                handleJoystickMove(event)
                true
            }
            MotionEvent.ACTION_UP -> {
                resetJoystick()
                true
            }
            else -> false
        }
    }

    private fun handleJoystickMove(event: MotionEvent) {
        val dx = event.x - centerX
        val dy = event.y - centerY
        val distance = sqrt(dx * dx + dy * dy)

        val maxDistance = joystickRadius - joystickHandle.width / 2
        val limitedDistance = min(distance, maxDistance)
        val angle = atan2(dy, dx)

        val newX = centerX + limitedDistance * cos(angle)
        val newY = centerY + limitedDistance * sin(angle)

        joystickHandle.x = newX - joystickHandle.width / 2
        joystickHandle.y = newY - joystickHandle.height / 2

        moveX = cos(angle) * (limitedDistance / maxDistance)
        moveY = sin(angle) * (limitedDistance / maxDistance)

        if (!movementHandler.hasMessages(0)) {
            movementHandler.post(movementRunnable)
        }
    }

    private val movementRunnable = object : Runnable {
        override fun run() {
            // Move the whale image based on joystick direction
            whaleImageView.x += moveX * 10  // Adjust multiplier as needed for speed
            whaleImageView.y += moveY * 10

            // Keep whale within screen boundaries
            whaleImageView.x = whaleImageView.x.coerceIn(0f, rootLayout.width - whaleImageView.width.toFloat())
            whaleImageView.y = whaleImageView.y.coerceIn(0f, rootLayout.height - whaleImageView.height.toFloat())

            // Continue running if joystick is moved
            if (moveX != 0f || moveY != 0f) {
                movementHandler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    private fun resetJoystick() {
        joystickHandle.animate()
            .x(centerX - joystickHandle.width / 2)
            .y(centerY - joystickHandle.height / 2)
            .setDuration(300)
            .start()

        moveX = 0f
        moveY = 0f
        movementHandler.removeCallbacks(movementRunnable)
        joystickHandle.performClick()
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
    /*
    Code Attribution
    Title:ChatGpt Chat
    Author: ChatGpt
    post link: https://chatgpt.com/
    Usage: How to spawn net
    */
    private fun startNetSpawning() {
        net.visibility = View.VISIBLE
        netHandler.post(createNetRunnable())
    }

    private fun createNetRunnable(): Runnable {
        var fallSpeed = 5 // Starting fall speed
        var timeElapsed = 0L // Variable to track time elapsed
        return object : Runnable {
            override fun run() {
                // Increase fall speed every 10 seconds
                timeElapsed += 30 // Increment time elapsed (this runs every 30ms)
                if (timeElapsed >= 10000) { // 10 seconds
                    fallSpeed = min(fallSpeed + 2, 50) // Increase speed but cap at max speed
                    timeElapsed = 0 // Reset elapsed time
                }

                net.translationY += fallSpeed

                // Check if the net has gone off-screen or if it has collided with the whale
                if (net.translationY > rootLayout.height) {
                    resetNet()
                } else if (isColliding(whaleImageView, net)) {
                    gameOver()
                } else {
                    netHandler.postDelayed(this, 30) // Continue running every 30ms
                }
            }
        }
    }
    /*
    Code Attribution
    Title:ChatGpt Chat
    Author: ChatGpt
    post link: https://chatgpt.com/
    Usage: How to spawn net
    */
    private fun resetNet() {
        // Get the whale's current Y position
        val whaleCurrentY = whaleImageView.translationY

        // Set the net's Y position to a random value between the top of the screen and just above the whale's current position
        // The maximum Y position for the net should be just above the whale
        val maxStartY = if (whaleCurrentY > 0) whaleCurrentY - 1 else 0
        val randomStartY = (0..maxStartY.toInt()).random().toFloat()
        net.translationY = randomStartY

        // Randomize the X position of the net within the layout's width
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
    private var currentScore = 0 // Initialize current score

    private fun updateScore(newScore: Int) {
        currentScore = newScore
        findViewById<TextView>(R.id.scoreTextView).text = "Score: $currentScore"

        // Check for high score
        val highScore = getHighScoreFromStorage()
        if (currentScore > highScore) {
            saveHighScoreToStorage(currentScore)
            findViewById<TextView>(R.id.highScoreTextView).text = "High Score: $currentScore"
        } else {
            findViewById<TextView>(R.id.highScoreTextView).text = "High Score: $highScore"
        }
    }


    private fun gameOver() {
        netHandler.removeCallbacksAndMessages(null)
        scoreHandler.removeCallbacksAndMessages(null)
        updateHighScore()

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Your Score: $score\nHigh Score: $highScore")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                restartGame()
            }
            .setCancelable(false)
            .show()

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
        airLevel = 100
        resetNet()
        startScoreCounting()
        initializeGameComponents()
    }

    private fun startScoreCounting() {
        scoreRunnable = Runnable {
            score++
            scoreTextView.text = "Score: $score"
            scoreHandler.postDelayed(scoreRunnable!!, 1000)
        }
        scoreHandler.post(scoreRunnable!!)
    }

    private fun updateHighScore() {
        if (score > highScore) {
            highScore = score
            saveHighScoreToStorage(highScore)
        }
    }

    private fun getHighScoreFromStorage(): Int {
        val sharedPreferences = getSharedPreferences("game_preferences", MODE_PRIVATE)
        return sharedPreferences.getInt("high_score", 0)
    }

    private fun saveHighScoreToStorage(score: Int) {
        val sharedPreferences = getSharedPreferences("game_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("high_score", score)
        editor.apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        scoreHandler.removeCallbacksAndMessages(null)
        netHandler.removeCallbacksAndMessages(null)
        movementHandler.removeCallbacksAndMessages(null)
    }
}
