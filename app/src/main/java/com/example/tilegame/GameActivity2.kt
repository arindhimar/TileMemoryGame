package com.example.tilegame

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GameActivity2 : AppCompatActivity() {
    private lateinit var selectedTiles: MutableList<Int>
    private lateinit var highlightedTiles: MutableList<Int>
    private val originalRotations: MutableMap<Int, Float> = mutableMapOf()
    private lateinit var userSelectedTiles: MutableList<Int>
    private var roundsCompleted = 0
    private var score = 0
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game2)

        gridView = findViewById(R.id.grid_view)

        val dataList = generateDataList()
        val adapter = GridAdapter(this, dataList)
        gridView.adapter = adapter

        // Wait for 3 seconds after the activity loads
        Handler().postDelayed({
            // Start the game
            startGame()
        }, 3000)
    }

    private fun generateDataList(): List<String> {
        val dataList = mutableListOf<String>()
        for (i in 1..36) {
            dataList.add("Tile $i")
        }
        return dataList
    }

    private fun startGame() {
        roundsCompleted = 0
        score = 0

        // Highlight tiles for the user to remember
        highlightTiles(4)
    }

    private fun highlightTiles(count: Int) {
        highlightedTiles = mutableListOf()
        val random = Random()
        val totalTiles = gridView.childCount
        val selectedIndices = mutableSetOf<Int>()
        highlightedTiles.clear()
        repeat(count) {
            var randomIndex: Int
            do {
                randomIndex = random.nextInt(totalTiles)
            } while (randomIndex in selectedIndices)

            selectedIndices.add(randomIndex)
            highlightedTiles.add(randomIndex) // Store the position instead of ID

            val selectedTile = gridView.getChildAt(randomIndex) as Button

            // Store original rotation if not already stored
            originalRotations.getOrPut(randomIndex) { selectedTile.rotation }

            // Load the animator resource
            val animator = AnimatorInflater.loadAnimator(this, R.animator.animation180)
            animator.setTarget(selectedTile)
            animator.start()

            // Revert the rotation back after 3 seconds
            Handler().postDelayed({
                selectedTile.rotation = originalRotations[randomIndex] ?: 0f
            }, 3000)
        }

        // Wait for 3 seconds after highlighting tiles
        Handler().postDelayed({

            Log.d("TAG", "highlightTiles: $highlightedTiles")

            // Start the timer for user selection
            startTimer()
        }, 3000)
    }

    private fun startTimer() {
        // Initialize userSelectedTiles if not initialized
        if (!::userSelectedTiles.isInitialized) {
            userSelectedTiles = mutableListOf()
        }



        // Wait for 5 seconds
        Handler().postDelayed({
            Log.d("TAG", "startTimer: $userSelectedTiles")
            if (userSelectedTiles.size == highlightedTiles.size && userSelectedTiles.containsAll(highlightedTiles)) {
                // User selected correct tiles
                score += if (score >= 30) 20 else 10 // Increase score based on the condition
                Toast.makeText(this, "Tiles Match! Score: $score", Toast.LENGTH_SHORT).show()

                // Update the score
                val scoreBoard: TextView = findViewById(R.id.scoreBoard)
                scoreBoard.text = "Score : $score"

                roundsCompleted++
                if (roundsCompleted <= 3) {
                    // Start a new round with 4 tiles
                    highlightTiles(4)
                } else {
                    // Start a new round with 5 tiles
                    highlightTiles(5)
                }

                userSelectedTiles.clear()

            } else {
                // User selected wrong tiles or time's up
                endGame()
            }
        }, 5000)
    }

    private fun endGame() {
        Toast.makeText(this, "Game Over! Final Score: $score", Toast.LENGTH_SHORT).show()

        val sp = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val name = sp.getString("name", "null")

        if (name != "null") {
            // Save name and score to SharedPreferences for top 3 scores
            saveScoreToSharedPreferences(name!!, score)

            // Wait for 3 seconds after saving score
            Handler().postDelayed({
                // Start the timer for switching activity
                val intent = Intent(this, HighScoreActivity::class.java)
                intent.putExtra("score", score) // Pass the score to HighScoreActivity
                startActivity(intent)
                finish()
            }, 3000)
        } else {
            Toast.makeText(this, "Name not provided. Score not recorded.", Toast.LENGTH_SHORT).show()
            // Start the timer for switching activity without saving score
            Handler().postDelayed({
                val intent = Intent(this, HighScoreActivity::class.java)
                intent.putExtra("score", score) // Pass the score to HighScoreActivity
                startActivity(intent)
                finish()
            }, 3000)
        }
    }


//    private fun endGame() {
//        Toast.makeText(this, "Game Over! Final Score: $score", Toast.LENGTH_SHORT).show()
//        val sp = getSharedPreferences("userData", Context.MODE_PRIVATE)
//        val name = sp.getString("name", "null")
//        if (name != "null") {
//            // Save name and score to SharedPreferences for top 3 scores
//            saveScoreToSharedPreferences(name!!, score)
//        }
//
//        // Wait for 3 seconds after saving score
//        Handler().postDelayed({
//            // Start the timer for switching activity
//            val intent = Intent(this, HighScoreActivity::class.java)
//            intent.putExtra("score", score) // Pass the score to HighScoreActivity
//            startActivity(intent)
//            finish()
//        }, 3000)
//    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,WelcomeAcitvity::class.java)
        startActivity(intent)
    }

    private fun saveScoreToFile(name: String, score: Int) {
        try {
            // Open a file for writing in append mode
            val outputStream = openFileOutput("gameScore.txt", Context.MODE_APPEND)
            // Write the name, score, and a newline character
            outputStream.write("$name $score\n".toByteArray())
            outputStream.close()
            Log.d("TAG", "Name and score saved to file.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error saving name and score to file: ${e.message}")
        }
    }

    private fun saveScoreToSharedPreferences(name: String, score: Int) {
        val sharedPreferences = getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing scores from SharedPreferences
        val currentScores = sharedPreferences.getStringSet("scores", setOf())?.toMutableSet() ?: mutableSetOf()

        // Add the new score along with its name
        currentScores.add("$name $score")

        // Sort the scores by score value in descending order
        val sortedScores = currentScores.sortedByDescending { it.split(" ")[1].toInt() }

        // Retain only the top 3 scores
        val top3Scores = sortedScores.take(3)

        // Save the top 3 scores to SharedPreferences
        editor.putStringSet("scores", top3Scores.toSet())
        editor.apply()
    }

    fun onTileClicked(view: View) {
        if (view is Button) {
            if (!::userSelectedTiles.isInitialized) {
                userSelectedTiles = mutableListOf()
            }
            // Find the position of the clicked button
            val position = (view.parent as GridView).indexOfChild(view)
            userSelectedTiles.add(position) // Store the position instead of ID
        }
    }
}
