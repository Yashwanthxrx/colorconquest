package com.example.colorconquest2

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
data class Tile(var color: Int = 0, var points: Int = 0)

class GameViewModel : ViewModel() {
    val grid = Array(5) { Array(5) { Tile() } }
    var currentPlayer = 1
    var player1Points = 0
    var player2Points = 0
    var moveCounter = 0

    fun onButtonClick(row: Int, col: Int, updateUI: (Int, Int, Tile) -> Unit, onWin: (Int) -> Unit) {
        val cell = grid[row][col]

        if (cell.color == currentPlayer) {
            cell.points++
            if (cell.points >= 4) {
                expand(row, col, updateUI)
            }
        } else if (cell.color == 0) {
            cell.color = currentPlayer
            cell.points = 3
        }

        // Update UI for the clicked tile
        updateUI(row, col, cell)

        // Update player points
        updatePlayerPoints()

        // Increment move counter
        moveCounter++

        // Check if both players have completed their first move
        if (moveCounter >= 2) {
            if (player1Points == 0) {
                onWin(2)
                return
            } else if (player2Points == 0) {
                onWin(1)
                return
            }
        }

        // Switch player
        currentPlayer = if (currentPlayer == 1) 2 else 1
    }

    fun expand(row: Int, col: Int, updateUI: (Int, Int, Tile) -> Unit) {
        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        // Reset the current tile
        grid[row][col].apply {
            color = 0
            points = 0
        }
        updateUI(row, col, grid[row][col])

        // Expand to neighboring tiles
        directions.forEach { (dx, dy) ->
            val newRow = row + dx
            val newCol = col + dy

            if (newRow in 0..4 && newCol in 0..4) {
                val neighbor = grid[newRow][newCol]
                if (neighbor.color != currentPlayer) {
                    neighbor.color = currentPlayer
                    neighbor.points = 1
                } else {
                    neighbor.points++
                    if (neighbor.points >= 4) {
                        expand(newRow, newCol, updateUI)
                    }
                }
                updateUI(newRow, newCol, neighbor)
            }
        }

        updatePlayerPoints()
    }

    fun updatePlayerPoints() {
        player1Points = grid.flatten().filter { it.color == 1 }.sumOf { it.points }
        player2Points = grid.flatten().filter { it.color == 2 }.sumOf { it.points }
    }
}

class MainActivity3 : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var player1PointsTextView: TextView
    private lateinit var player2PointsTextView: TextView
    private val viewModel by lazy { ViewModelProvider(this).get(GameViewModel::class.java) }
    private var isPlayer1Turn = true
    private lateinit var rootView: ConstraintLayout
    lateinit var p1: TextView
    lateinit var p2: TextView
    lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page3)
        resetButton = findViewById(R.id.reset)
        resetButton.setOnClickListener {
            resetGame()
        }

        p1 = findViewById(R.id.textView2)
        p2 = findViewById(R.id.textView)
        val intent = intent
        val str = intent.getStringExtra("message_key")
        val str2 = intent.getStringExtra("message_key2")
        p1.text = str
        p2.text = str2

        rootView = findViewById(R.id.relativeLayout3)
        gridLayout = findViewById(R.id.gridlayout)
        player1PointsTextView = findViewById(R.id.player1Points)
        player2PointsTextView = findViewById(R.id.player2Points)

        createGridButtons()

        findViewById<Button>(R.id.closeButton).setOnClickListener {
            finish()
        }

        updatePointsDisplay()
    }

    private fun createGridButtons() {
        val buttonSize = resources.displayMetrics.widthPixels / 5 - 70
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                val context = gridLayout.context
                val button = Button(context).apply {
                    tag = intArrayOf(row, col)
                    setOnClickListener { handleTileClick(it.tag as IntArray) }
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = buttonSize
                        height = buttonSize
                        setMargins(8, 8, 8, 8)
                    }
                    background = ContextCompat.getDrawable(context, R.drawable.gridl)
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun handleTileClick(position: IntArray) {
        val (row, col) = position
        val tile = viewModel.grid[row][col]

        if (isValidMove(tile)) {
            viewModel.onButtonClick(row, col, ::updateTileUI) { winner ->
                showWinnerDialog(winner)
            }
            checkForExpansion(row, col)
            isPlayer1Turn = !isPlayer1Turn
            updateBackgroundColor()
            updatePointsDisplay()
        }
    }

    private fun isValidMove(tile: Tile): Boolean {
        return tile.color == 0 || tile.color == if (isPlayer1Turn) 1 else 2
    }

    private fun checkForExpansion(row: Int, col: Int) {
        val tile = viewModel.grid[row][col]
        if (tile.points >= 4) {
            viewModel.expand(row, col, ::updateTileUI)
        }
    }

    private fun updateTileUI(row: Int, col: Int, tile: Tile) {
        val button = gridLayout.getChildAt(row * 5 + col) as Button
        button.apply {
            text = if (tile.points > 0) tile.points.toString() else ""
            textSize = 24f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            gravity = android.view.Gravity.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            background = ContextCompat.getDrawable(
                context,
                when (tile.color) {
                    1 -> R.drawable.cbut
                    2 -> R.drawable.cbut2
                    else -> R.drawable.gridl
                }
            )
        }
    }

    private fun updateBackgroundColor() {
        val playerColor = if (isPlayer1Turn) {
            ContextCompat.getColor(this, R.color.redb)
        } else {
            ContextCompat.getColor(this, R.color.play)
        }
        rootView.setBackgroundColor(playerColor)
    }

    private fun updatePointsDisplay() {
        player1PointsTextView.text = viewModel.player1Points.toString()
        player2PointsTextView.text = viewModel.player2Points.toString()
    }

    private fun showWinnerDialog(winner: Int) {
        val dialogView = layoutInflater.inflate(R.layout.winner_dialogue, null)
        val winnerNameTextView = dialogView.findViewById<TextView>(R.id.winnerName)
        val playAgainButton = dialogView.findViewById<Button>(R.id.playAgainButton)
        val homeButton = dialogView.findViewById<Button>(R.id.homeButton)

        val winnerName = if (winner == 1) p1.text.toString() else p2.text.toString()
        winnerNameTextView.text = winnerName

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.show()

        playAgainButton.setOnClickListener {
            dialog.dismiss()
            // Reset the game state and start a new game
            resetGame()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun resetGame() {
        viewModel.grid.forEach { row ->
            row.forEach { tile ->
                tile.color = 0
                tile.points = 0
            }
        }
        isPlayer1Turn = true
        viewModel.currentPlayer = 1
        viewModel.player1Points = 0
        viewModel.player2Points = 0
        viewModel.moveCounter = 0
        updateBackgroundColor()
        updatePointsDisplay()
        // Update UI for all tiles
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                updateTileUI(row, col, viewModel.grid[row][col])
            }
        }
    }
}