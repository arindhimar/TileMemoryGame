package com.example.tilegame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HighScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)

        // Retrieve current score from intent
        val currentScore = intent.getIntExtra("score", 0)

        // Display current score
        val textCurrentScore: TextView = findViewById(R.id.text_current_score)
        textCurrentScore.text = "Current Score: $currentScore"

       displayHighScores()

    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,WelcomeAcitvity::class.java)
        startActivity(intent)
    }


    private fun displayHighScores() {
        // Retrieve high scores from SharedPreferences
        val sharedPreferences = getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val topScores = sharedPreferences.getStringSet("scores", setOf()) ?: emptySet()

        // Display top 3 high scores
        val textScore1: TextView = findViewById(R.id.text_score1)
        val textScore2: TextView = findViewById(R.id.text_score2)
        val textScore3: TextView = findViewById(R.id.text_score3)

        if (topScores.isNotEmpty()) {
            val scoresList = topScores.map { it.split(" ") }
            if (scoresList.isNotEmpty()) {
                textScore1.text = "1. ${scoresList[0][0]} ${scoresList[0][1]}"
            } else {
                textScore1.text = "1. -"
            }

            if (scoresList.size > 1) {
                textScore2.text = "2. ${scoresList[1][0]} ${scoresList[1][1]}"
            } else {
                textScore2.text = "2. -"
            }

            if (scoresList.size > 2) {
                textScore3.text = "3. ${scoresList[2][0]} ${scoresList[2][1]}"
            } else {
                textScore3.text = "3. -"
            }
        } else {
            // No high scores available
            textScore1.text = "1. -"
            textScore2.text = "2. -"
            textScore3.text = "3. -"
        }
    }


}
