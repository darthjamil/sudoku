package com.bumpylabs.sudoku

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumpylabs.sudoku.game.Game
import com.bumpylabs.sudoku.game.GameGenerator
import com.bumpylabs.sudoku.game.PlayResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class GameViewModel(
    private val rank: Int
) : ViewModel() {
    private lateinit var gameBoard: Game

    private val _uiState = MutableStateFlow(GameState())
    val uiState = _uiState.asStateFlow()

    init {
        newGame()
    }

    fun isGiven(row: Int, column: Int) = gameBoard.isGiven(row, column)

    fun clickCell(row: Int, column: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (selectedSameCell(currentState.selectedCellRow, currentState.selectedCellCol, row, column)) {
                    currentState.withCellDeselected()
                } else if (gameBoard.isGiven(row, column)) {
                    currentState.withCellSelected(row, column, allowChange = false)
                } else { // selected a different cell
                    currentState.withCellSelected(row, column, allowChange = true)
                }
            }
        }
    }

    fun clickValue(value: Int) {
        viewModelScope.launch {
            val i = _uiState.value.selectedCellRow
            val j = _uiState.value.selectedCellCol

            if (i == null || j == null) {
                return@launch
            }

            val playResult = gameBoard.play(i, j, value)

            if (playResult != PlayResult.VALID) {
                return@launch
            }

            _uiState.update { currentState ->
                val newState = currentState.withCellDeselected()
                newState.grid[i][j] = value

                newState
            }
        }
    }

    fun newGame() {
        viewModelScope.launch {
            gameBoard = getGameBoard(rank)

            _uiState.update {
                val valueOptions = (1..(gameBoard.rank * gameBoard.rank)).toList().toTypedArray()
                GameState(
                    rank = rank,
                    grid = gameBoard.getGrid(),
                    valueOptions = valueOptions
                )
            }
        }
    }

    private fun selectedSameCell(oldRow: Int?, oldCol: Int?, newRow: Int, newCol: Int) =
        oldRow == newRow && oldCol == newCol

    private fun getGameBoard(rank: Int): Game {
        val board = GameGenerator().generate()
        val (game, _) = Game.create(board)
        return game!!
    }

    private fun GameState.withCellSelected(row: Int, column: Int, allowChange: Boolean = true) = this.copy(
            enableValueSelection = allowChange,
            selectedCellRow = row,
            selectedCellCol = column
    )

    private fun GameState.withCellDeselected() = this.copy(
            enableValueSelection = false,
            selectedCellRow = null,
            selectedCellCol = null
    )
}

@Stable
@Parcelize
data class GameState(
    val rank: Int = 0,
    val grid: Array<IntArray> = emptyArray(),
    val enableValueSelection: Boolean = false,
    val valueOptions: Array<Int> = emptyArray(),
    val selectedCellRow: Int? = null,
    val selectedCellCol: Int? = null,
) : Parcelable
