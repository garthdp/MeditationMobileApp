package com.example.opsc_poe_part_2

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.fixedRateTimer
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class WhaleGame : AppCompatActivity() {
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
        airBar = findViewById(R.id.airBar)
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

    private fun setupJoystick() {
        joystickContainer.post {
            centerX = joystickContainer.width / 2f
            centerY = joystickContainer.height / 2f
            joystickRadius = min(joystickContainer.width, joystickContainer.height) / 2f

            joystickHandle.setOnTouchListener { _, event -> handleJoystickTouch(event) }
        }
        joystickHandle.setOnClickListener {} // Override performClick for accessibility
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
        bubbles.forEach { (bubble, duration) ->
            startFloatingAnimation(bubble, duration)
            bubble.setOnClickListener { collectBubble(bubble) }
        }

        // Decrease air over time
        fixedRateTimer("decreaseAir", initialDelay = 0, period = 1000) {
            runOnUiThread {
                if (airLevel > 0) {
                    airLevel -= decreaseAmount
                    updateAirBar()
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
    }

    private fun restartGame() {
        score = 0
        airLevel = 100
        updateAirBar()
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

    private fun collectBubble(bubble: ImageView) {
        score += 5
        scoreTextView.text = "Score: $score"
        bubble.visibility = View.INVISIBLE
    }

    private fun updateAirBar() {
        airBar.progress = airLevel
        if (airLevel <= 0) {
            gameOver()
        }
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
