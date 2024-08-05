package com.bumpylabs.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumpylabs.sudoku.ui.theme.SudokuTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private val boardSize = 400.dp
    private val thickDivider = 1.dp
    private val thinDivider = 0.25.dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SudokuTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val viewModel = viewModel<GameViewModel>(factory = object: ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return GameViewModel(3) as T
                        }
                    })

                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Game(viewModel, modifier = Modifier.padding(innerPadding))
                    }
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
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Board(
                rank = uiState.rank,
                grid = uiState.grid,
                selectedRow = uiState.selectedCellRow,
                selectedColumn = uiState.selectedCellRow,
                checkIsGiven = { row, column -> viewModel.isGiven(row, column) },
                onCellClick = { row, column -> viewModel.clickCell(row, column) },
                modifier = Modifier
                    .size(boardSize)
            )
            Spacer(modifier = Modifier.padding(16.dp))
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
        selectedRow: Int?,
        selectedColumn: Int?,
        checkIsGiven: (row: Int, column: Int) -> Boolean,
        onCellClick: (row: Int, column: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val gameSize = grid.size

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            repeat(gameSize) { row ->
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = if (isDivisible(row, rank)) thickDivider else thinDivider
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    repeat(gameSize) { column ->
                        val isCellSelected = row == selectedRow && column == selectedColumn
                        val isCellHighlighted = !isCellSelected && (row == selectedRow || column == selectedColumn)
                        val isValueHighlighted = selectedRow != null
                                && selectedColumn != null
                                && grid[selectedRow][selectedColumn] == grid[row][column]

                        VerticalDivider(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            thickness = if (isDivisible(column, rank)) thickDivider else thinDivider
                        )

                        Cell(
                            value = grid[row][column],
                            isGiven = checkIsGiven(row, column),
                            isSelected = isCellSelected,
                            isCellHighlighted = isCellHighlighted,
                            isValueHighlighted = isValueHighlighted,
                            onCellClick = { onCellClick(row, column) },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = thickDivider)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = thickDivider)
        }
    }

    @Composable
    fun Cell(
        value: Int,
        isGiven: Boolean,
        isSelected: Boolean,
        isCellHighlighted: Boolean,
        isValueHighlighted: Boolean,
        onCellClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val text = if (value == 0) "" else value.toString()
        val backgroundColor = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            isCellHighlighted -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
        val textColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            isValueHighlighted -> MaterialTheme.colorScheme.onPrimaryContainer
            isCellHighlighted -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = textColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = modifier
                .background(color = backgroundColor)
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
        valueOptions: Array<Int>,
        onNumberClick: (value: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val maxValuesPerRow = sqrt(valueOptions.size.toDouble()).toInt()

        FlowRow(
            modifier = modifier,
            maxItemsInEachRow = maxValuesPerRow,
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