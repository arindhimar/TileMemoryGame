package com.example.tilegame

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tilegame.databinding.ActivityGameBinding
import java.lang.Double.min
import kotlin.math.min

class GameActivity : AppCompatActivity() {
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inside your Activity or Fragment
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        // Calculate the screen width and height
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Calculate the tile size
        val tileSize = min(screenWidth, screenHeight) / 6 // Assuming you want 6x6 grid

        // Calculate the margin size
        val marginSize = tileSize / 10 // Adjust as needed

        // Add tiles to the GridLayout with background and border
        for (i in 0 until 6) {
            for (j in 0 until 6) {
                val tile = View(this)
                val params = GridLayout.LayoutParams()
                params.width = tileSize
                params.height = tileSize
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                params.setMargins(marginSize, marginSize, marginSize, marginSize) // Set margins
                tile.layoutParams = params
                tile.setBackgroundResource(R.drawable.tile_background) // Set background drawable
                tile.setOnClickListener {
                    startAnimation(tile)
                }
                gridLayout.addView(tile)
            }
        }
    }

    private fun startAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f)
        animator.duration = 1000
        animator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            // Check if the tile is flipped (rotation angle > 90 degrees)
            if (value > 90) {
                view.setBackgroundResource(R.drawable.tile_background_flipped)
            } else {
                view.setBackgroundResource(R.drawable.tile_background)
            }
        }
        animator.start()
    }

}
