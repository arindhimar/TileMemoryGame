package com.example.tilegame

import android.animation.ObjectAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tilegame.databinding.ActivityGameBinding
import java.util.Random
import kotlin.math.min

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val tilesSelected = mutableListOf<String>()
    private var tilesClickable = false
    private val flippedTiles = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val tileSize = min(displayMetrics.widthPixels, displayMetrics.heightPixels) / 6
        val marginSize = tileSize / 10

        for (i in 0 until 6) {
            for (j in 0 until 6) {
                val tile = View(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = tileSize
                        height = tileSize
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                        setMargins(marginSize, marginSize, marginSize, marginSize)
                    }
                    setBackgroundResource(R.drawable.tile_background)
                    tag = "Tile_$i$j"
                    setOnClickListener {
                        startAnimation(this)
                        isClickable = false
                    }
                }
                gridLayout.addView(tile)
            }
        }
        selectAndHighlightTiles()
//        val delayInMillis = 6000L
//        binding.gridLayout.postDelayed({
//            Log.e("TAG", "Tile with tag ")
//
//        }, delayInMillis)


    }


    private fun startAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f)
        animator.duration = 1000
        animator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val isFlipped = value > 90
            val currentDrawableId =
                (view.background as? ColorDrawable)?.color ?: R.drawable.tile_background
            val newDrawableId = if (currentDrawableId == R.drawable.tile_background) {
                if (isFlipped) R.drawable.tile_background_flipped else R.drawable.tile_background
            } else {
                if (isFlipped) R.drawable.tile_background else R.drawable.tile_background_flipped
            }
            view.setBackgroundResource(newDrawableId)
        }
        animator.start()
    }

    private fun selectAndHighlightTiles() {

        flippedTiles.clear()

        tilesSelected.clear()

        Log.e("TAG", "check fxn call $tilesSelected ")

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val totalTiles = gridLayout.childCount
        val random = Random()
        val selectedTags = mutableSetOf<String>()

        repeat(4) {
            var randomTag: String
            do {
                val i = random.nextInt(6)
                val j = random.nextInt(6)
                randomTag = "Tile_$i$j"
            } while (selectedTags.contains(randomTag))
            selectedTags.add(randomTag)
            tilesSelected.add(randomTag)
        }

        tilesSelected.forEach { tag ->
            val tile = gridLayout.findViewWithTag<View>(tag)
            if (tile == null) {
                Log.e("TAG", "Tile with tag $tag not found!")
            } else {
                Log.d("TAG", "Tile with tag $tag found: $tile")
            }
        }

        val highlightDelay = 1000L
        gridLayout.postDelayed({
            tilesSelected.forEachIndexed { index, tag ->
                val flipBackDelay = (index + 1) * 1000L
                gridLayout.postDelayed({
                    flipTile(tag, true)
                    if (index == tilesSelected.size - 1) {
                        gridLayout.postDelayed({
                            tilesSelected.forEach { flipTile(it, false) }
                            makeAllTilesClickable()
                        }, 1000L)
                    }
                }, flipBackDelay)
            }
        }, highlightDelay)

        Log.d("TAG", "selectAndHighlightTiles: $tilesSelected")
    }

    private fun makeAllTilesClickable() {
        tilesClickable = true
    }

    private fun makeAllTilesUnClickable() {
        tilesClickable = false
    }

    private fun flipTile(tag: String, isFlipped: Boolean) {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val tile = gridLayout.findViewWithTag<View>(tag)
        val currentDrawableId =
            if (isFlipped) R.drawable.tile_background_flipped else R.drawable.tile_background
        tile?.setBackgroundResource(currentDrawableId)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return tilesClickable || super.onTouchEvent(event)
    }
}

