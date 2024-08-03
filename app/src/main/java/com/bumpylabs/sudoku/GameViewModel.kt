package com.bumpylabs.sudoku

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.bumpylabs.sudoku.game.Game
import com.bumpylabs.sudoku.game.GameGenerator
import com.bumpylabs.sudoku.game.PlayResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private lateinit var gameBoard: Game
    private val _uiState = MutableStateFlow(GameState())
    val uiState = _uiState.asStateFlow()

    fun createGame(rank: Int = 3) {
        _uiState.update {
            gameBoard = getGameBoard(rank)
            val valueOptions = 1..(gameBoard.rank * gameBoard.rank)

            GameState(rank = rank, grid = gameBoard.getGrid(), valueOptions = valueOptions)
        }
    }

    fun valueAt(row: Int, column: Int) = gameBoard.valueAt(row, column)

    fun isGiven(row: Int, column: Int) = gameBoard.isGiven(row, column)

    fun clickCell(row: Int, column: Int) {
        _uiState.update { currentState ->
            if (gameBoard.isGiven(row, column)) {
                return@update currentState.withCellDeselected()
            }

            if (currentState.isDeselected()) {
                currentState.withCellSelected(row, column)
            } else if (selectedSameCell(currentState.selectedCellRow, currentState.selectedCellCol, row, column)) {
                currentState.withCellDeselected()
            } else { // selected a different cell
                currentState.withCellSelected(row, column)
            }
        }
    }

    fun clickValue(value: Int) {
        val i = _uiState.value.selectedCellRow
        val j = _uiState.value.selectedCellCol

        val playResult = gameBoard.play(i, j, value)

        if (playResult == PlayResult.VALID) {
            _uiState.update { currentState ->
                val newState = currentState.withCellDeselected()
                newState.grid[i][j] = value

                newState
            }
        }
    }

    private fun selectedSameCell(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int) =
        oldRow == newRow && oldCol == newCol

    private fun getGameBoard(rank: Int): Game {
        val board = GameGenerator().generate()
        val (game, _) = Game.create(board)
        return game!!
    }

    fun GameState.isDeselected() = this.selectedCellRow == -1 && this.selectedCellCol == -1

    fun GameState.withCellSelected(row: Int, column: Int) = this.copy(
            enableValueSelection = true,
            selectedCellRow = row,
            selectedCellCol = column
    )

    fun GameState.withCellDeselected() = this.copy(
            enableValueSelection = false,
            selectedCellRow = -1,
            selectedCellCol = -1
    )
}

@Stable
data class GameState(
    val rank: Int = 0,
    val grid: Array<IntArray> = emptyArray(),
    val enableValueSelection: Boolean = false,
    val valueOptions: IntRange = IntRange.EMPTY,
    val selectedCellRow: Int = -1,
    val selectedCellCol: Int = -1,
)
