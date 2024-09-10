package com.bumpylabs.sudoku

import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat.performHapticFeedback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumpylabs.sudoku.ui.theme.SudokuTheme
import kotlin.math.sqrt


class MainActivity : ComponentActivity() {

    private val thickDivider = 1.dp
    private val thinDivider = 0.25.dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SudokuTheme {
                val viewModel = viewModel<GameViewModel>(factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GameViewModel(rank = 3) as T
                    }
                })

                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() },
                    content = { innerPadding ->
                        Game(
                            uiState,
                            checkIsGiven = { row, col -> viewModel.isGiven(row, col) },
                            onCellClick = { row, col -> viewModel.clickCell(row, col) },
                            onValueClick = { viewModel.clickValue(it) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    },
                    floatingActionButton = {
                        FAB(
                            onNewGameClick = { viewModel.newGame() }
                        )
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(
        modifier: Modifier = Modifier
    ) {
        TopAppBar(
            title = { stringResource(R.string.top_bar_title) },
            modifier = modifier,
        )
    }

    @Composable
    fun FAB(
        onNewGameClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        FloatingActionButton(
            onClick = onNewGameClick,
            modifier = modifier,
        )
        {
            Row {
                Icon(
                    painter = painterResource(R.drawable.outline_refresh_24),
                    contentDescription = stringResource(R.string.new_game_button_description),
                )
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = stringResource(R.string.new_game_button_text))
            }
        }
    }

    @Composable
    fun Game(
        uiState: GameState,
        checkIsGiven: (row: Int, column: Int) -> Boolean,
        onCellClick: (row: Int, column: Int) -> Unit,
        onValueClick: (value: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Board(
                rank = uiState.rank,
                grid = uiState.grid,
                selectedRow = uiState.selectedCellRow,
                selectedColumn = uiState.selectedCellCol,
                checkIsGiven = checkIsGiven,
                onCellClick = onCellClick,
                modifier = Modifier
                    .size(400.dp)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            NumberSelection(
                enabled = uiState.enableValueSelection,
                valueOptions = uiState.valueOptions,
                onNumberClick = onValueClick
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
            modifier = modifier
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
                        val isValueHighlighted = grid[row][column] != 0
                                && selectedRow != null
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
            isValueHighlighted -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.surface
        }
        val textColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            isCellHighlighted -> MaterialTheme.colorScheme.onSecondaryContainer
            isValueHighlighted -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(color = textColor),
            fontSize = 30.sp,
            modifier = modifier
                .fillMaxHeight()
                .background(color = backgroundColor)
                .wrapContentSize()
                .clickable(onClick = onCellClick)
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