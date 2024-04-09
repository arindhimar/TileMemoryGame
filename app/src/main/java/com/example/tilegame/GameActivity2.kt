package com.example.tilegame

import android.animation.AnimatorInflater
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import java.util.*

class GameActivity2 : AppCompatActivity() {

    private lateinit var selectedTiles: MutableList<Int>
    private val originalRotations: MutableMap<Int, Float> = mutableMapOf()
    private lateinit var userSelectedTiles: MutableList<Int>
    private var roundsCompleted = 0
    private var score = 0
    private var continueGame = true
    private lateinit var gridView:GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game2)

         gridView = findViewById(R.id.grid_view)

        val dataList = generateDataList()
        val adapter = GridAdapter(this, dataList)
        gridView.adapter = adapter

        // Wait for 3 seconds after the activity loads
        Handler().postDelayed({
            // Select 4 random buttons

            selectRandomTiles(4, gridView)


        }, 3000)
    }

    private fun generateDataList(): List<String> {
        val dataList = mutableListOf<String>()
        for (i in 1..36) {
            dataList.add("Tile $i")
        }
        return dataList
    }

    private fun selectRandomTiles(count: Int, gridView: GridView) {


        selectedTiles = mutableListOf()
        val random = Random()
        val totalTiles = gridView.childCount
        val selectedIndices = mutableSetOf<Int>()
        selectedTiles.clear()
        repeat(count) {
            var randomIndex: Int
            do {
                randomIndex = random.nextInt(totalTiles)
            } while (randomIndex in selectedIndices)

            selectedIndices.add(randomIndex)
            selectedTiles.add(randomIndex) // Store the position instead of ID

            val selectedTile = gridView.getChildAt(randomIndex) as Button

            Log.d("TAG", "def: $selectedTile")

            // Store original rotation if not already stored
            originalRotations.getOrPut(randomIndex) { selectedTile.rotation }

            // Load the animator resource
            val animator = AnimatorInflater.loadAnimator(this, R.animator.animation180)
            animator.setTarget(selectedTile)
            animator.start()

            // Revert the rotation back after 4 seconds
            Handler().postDelayed({
                selectedTile.rotation = originalRotations[randomIndex] ?: 0f
            }, 4000)
        }

        // Start timer after selecting random tiles
        startTimer()
    }



    private fun startTimer() {



        // Initialize userSelectedTiles if not initialized
        if (!::userSelectedTiles.isInitialized) {
            userSelectedTiles = mutableListOf()
        }

        // Wait for 5 seconds
        Handler().postDelayed({
            // Check if user selected tiles match random tiles
            if (::userSelectedTiles.isInitialized && userSelectedTiles.containsAll(selectedTiles) && selectedTiles.containsAll(userSelectedTiles)) {
                // Match found
                score += if (roundsCompleted <= 3) 10 else 20
                roundsCompleted++
                Toast.makeText(this, "Tiles Match! Score: $score", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "user: $userSelectedTiles")

                if(roundsCompleted >= 3){
                    selectRandomTiles(5, gridView)
                } else{
                    selectRandomTiles(4, gridView)
                }

            } else {
                // Match not found
                Toast.makeText(this, "Tiles Do Not Match! Score: $score", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "user: $userSelectedTiles")
                continueGame = false

                // Save the score to a file
                saveScoreToFile(score)
            }
            userSelectedTiles.clear()
        }, 5000)
    }

    private fun saveScoreToFile(score: Int) {
        try {
            // Open a file for writing in append mode
            val outputStream = openFileOutput("score.txt", Context.MODE_APPEND)
            // Write the score followed by a newline character
            outputStream.write("$score\n".toByteArray())
            outputStream.close()
            Log.d("TAG", "Score saved to file.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error saving score to file: ${e.message}")
        }
    }


    fun onTileClicked(view: View) {
        if (view is Button) {
            if (!::userSelectedTiles.isInitialized) {
                userSelectedTiles = mutableListOf()
            }
            // Find the position of the clicked button
            val position = (view.parent as GridView).indexOfChild(view)
            userSelectedTiles.add(position) // Store the position instead of ID

            // Check if the user has selected the required number of tiles
            if (userSelectedTiles.size == if (roundsCompleted < 3) 4 else 5) {
                // Stop the timer to prevent further selection
                Handler().removeCallbacksAndMessages(null)
                // Start a new round
                Handler().postDelayed({
                    selectRandomTiles(if (roundsCompleted < 3) 4 else 5, view.parent as GridView)
                }, 2000)
            }
        }

        Log.d("TAG", "in fxn user: $userSelectedTiles")
    }
}
