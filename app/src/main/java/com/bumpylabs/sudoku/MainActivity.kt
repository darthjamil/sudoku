package com.bumpylabs.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumpylabs.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {

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
        Column(
            modifier = modifier
        ) {
            Board(gameRank = 3, boardSize = 400.dp)
        }
    }

    @Composable
    fun Board(gameRank: Int, boardSize: Dp, modifier: Modifier = Modifier) {
        val thickDivider = 1.dp
        val thinDivider = 0.25.dp
        val gameSize = gameRank * gameRank
        val cellSize = (boardSize / gameSize)

        Column(
            modifier = modifier
                .size(boardSize, boardSize)
        ) {
            repeat(gameSize) { row ->
                HorizontalDivider(
                    color = Color.Black,
                    thickness = if (isDivisible(row, gameRank)) thickDivider else thinDivider
                )

                Row {
                    repeat(gameSize) { column ->
                        VerticalDivider(
                            color = Color.Black,
                            thickness = if (isDivisible(column, gameRank)) thickDivider else thinDivider,
                            modifier = Modifier.height(cellSize)
                        )

                        Cell(cellSize, row + column) // TODO just for testing fo now
                    }

                    VerticalDivider(color = Color.Black, thickness = thickDivider, modifier = Modifier.height(cellSize))
                }
            }

            HorizontalDivider(color = Color.Black, thickness = thickDivider)
        }
    }

    @Composable
    fun Cell(cellSize: Dp, value: Int, modifier: Modifier = Modifier) {
        val interactionSource = remember { MutableInteractionSource() }

        Text(
            text = value.toString(),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = modifier
                .size(cellSize, cellSize)
                .wrapContentHeight(Alignment.CenterVertically)
                .clickable(
                    onClick = {},
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                )
        )
    }

    private fun isDivisible(dividend: Int, divisor: Int): Boolean {
        val quotient = dividend.toDouble() / divisor
        return quotient.toInt().compareTo(quotient) == 0
    }

}