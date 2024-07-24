package com.bumpylabs.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                    val viewModel: GameViewModel by viewModels()
                    viewModel.createGame(3)

                    Game(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun Game(
        viewModel: GameViewModel,
        modifier: Modifier = Modifier
    ) {
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = modifier
        ) {
            Board(
                rank = uiState.rank,
                grid = uiState.grid,
                checkIsGiven = { row, column -> viewModel.isGiven(row, column) },
                onCellClick = { row, column -> viewModel.clickCell(row, column) },
                modifier = Modifier
                    .size(boardSize)
            )
            NumberSelection(
                enabled = uiState.enableValueSelection,
                valueOptions = uiState.valueOptions,
                onNumberClick = { viewModel.clickValue(it) }
            )
        }
    }

    @Composable
    fun Board(
        rank: Int,
        grid: Array<IntArray>,
        checkIsGiven: (row: Int, column: Int) -> Boolean,
        onCellClick: (row: Int, column: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val gameSize = grid.size

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            repeat(gameSize) { row ->
                HorizontalDivider(
                    color = Color.Black,
                    thickness = if (isDivisible(row, rank)) thickDivider else thinDivider
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    repeat(gameSize) { column ->
                        VerticalDivider(
                            color = Color.Black,
                            thickness = if (isDivisible(column, rank)) thickDivider else thinDivider
                        )

                        Cell(
                            value = grid[row][column],
                            isGiven = checkIsGiven(row, column),
                            onCellClick = { onCellClick(row, column) },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    VerticalDivider(color = Color.Black, thickness = thickDivider)
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

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun NumberSelection(
        enabled: Boolean,
        valueOptions: IntRange,
        onNumberClick: (value: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        FlowRow(
            modifier = modifier
        ) {
            valueOptions.forEach {
                ElevatedButton(
                    enabled = enabled,
                    onClick = { onNumberClick(it) }
                ) {
                    Text(it.toString())
                }
            }
        }
    }

    private fun isDivisible(dividend: Int, divisor: Int): Boolean {
        val quotient = dividend.toDouble() / divisor
        return quotient.toInt().compareTo(quotient) == 0
    }

}