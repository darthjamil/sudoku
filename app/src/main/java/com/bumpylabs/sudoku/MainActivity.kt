package com.bumpylabs.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumpylabs.sudoku.game.GameBoard
import com.bumpylabs.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {

    private val boardSize = 400.dp
    private val thickDivider = 1.dp
    private val thinDivider = 0.25.dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Game(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Preview
    @Composable
    fun Game(modifier: Modifier = Modifier) {
        val gameBoard by remember {
            val g = getGameBoard()
            mutableStateOf(g)
        }

        Column(
            modifier = modifier
        ) {
            Board(
                gameBoard = gameBoard,
                onCellClick = { row, column ->

                },
                boardSize = boardSize
            )
        }
    }

    @Composable
    fun Board(
        gameBoard: GameBoard,
        onCellClick: (row: Int, column: Int) -> Unit,
        boardSize: Dp,
        modifier: Modifier = Modifier
    ) {
        val rank = gameBoard.rank
        val gameSize = rank * rank
        val cellSize = (boardSize / gameSize) - 2.dp

        Column(
            modifier = modifier
                .size(boardSize)
        ) {
            repeat(gameSize) { row ->
                HorizontalDivider(
                    color = Color.Black,
                    thickness = if (isDivisible(row, rank)) thickDivider else thinDivider
                )

                Row {
                    repeat(gameSize) { column ->
                        VerticalDivider(
                            color = Color.Black,
                            thickness = if (isDivisible(column, rank)) thickDivider else thinDivider,
                            modifier = Modifier.height(cellSize)
                        )

                        Cell(
                            value = gameBoard.valueAt(row, column),
                            isGiven = gameBoard.isGiven(row, column),
                            onCellClick = { onCellClick(row, column) },
                            modifier = Modifier.size(cellSize)
                        )
                    }

                    VerticalDivider(color = Color.Black, thickness = thickDivider, modifier = Modifier.height(cellSize))
                }
            }

            HorizontalDivider(color = Color.Black, thickness = thickDivider)
        }
    }

    @Composable
    fun Cell(
        value: Int,
        isGiven: Boolean,
        onCellClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val text = if (value == 0) "" else value.toString()

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = modifier
                .wrapContentHeight(Alignment.CenterVertically)
                .clickable(
                    enabled = !isGiven,
                    onClick = onCellClick,
                )
        )
    }

    private fun getGameBoard(): GameBoard {
        // TODO
        val grid = arrayOf(
            intArrayOf(6, 7, 0,   0, 3, 0,   1, 9, 4),
            intArrayOf(0, 0, 9,   0, 0, 0,   7, 0, 8),
            intArrayOf(0, 8, 0,   9, 0, 4,   0, 3, 0),

            intArrayOf(0, 1, 0,   5, 0, 0,   0, 4, 9),
            intArrayOf(0, 0, 3,   0, 0, 0,   2, 0, 0),
            intArrayOf(5, 2, 0,   0, 0, 9,   0, 1, 0),

            intArrayOf(0, 6, 0,   3, 0, 1,   0, 7, 0),
            intArrayOf(4, 0, 1,   0, 0, 0,   9, 0, 0),
            intArrayOf(9, 5, 7,   0, 6, 0,   0, 8, 1),
        )
        val (game, _) = GameBoard.create(grid)
        return game!!
    }

    private fun isDivisible(dividend: Int, divisor: Int): Boolean {
        val quotient = dividend.toDouble() / divisor
        return quotient.toInt().compareTo(quotient) == 0
    }

}