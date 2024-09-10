package com.bumpylabs.sudoku.game

/**
 * Represents a sudoku game that can be played by first creating it with
 * an initial board state, then attempting to solve it cell by cell. The
 * game will know when it's been solved. The game will never allow itself
 * to become in an inconsistent state, unless the initial state provided
 * was itself inconsistent.
 *
 * @param board: A data structure that represents the initial state of the game.
 */
class Game private constructor(private val board: SudokuGrid) {
    private val allowedValues = (0..board.size)
    private val coordinatesOfGivens: MutableList<Pair<Int, Int>> = ArrayList()

    init {
        populateGivens()
    }

    val rank = board.rank
    val size = board.size

    fun getGrid() = board.copyAsArray()
    fun valueAt(i: Int, j: Int) = board[i, j]
    fun isGiven(i: Int, j: Int) = isIndexGiven(i, j)
    fun isSolved() = board.isSolved()

    /**
     * Adds a guess to the specified cell in the grid. An error result is returned if the specified
     * cell is invalid, the value provided is invalid, the cell is a given, or the guess results
     * in breaking the One Rule. The grid is never left in an inconsistent state.
     */
    fun play(i: Int, j: Int, value: Int): PlayResult {
        if (!isIndexInGrid(i, j)) {
            return PlayResult.INDEX_OUT_OF_BOUNDS
        }

        if (!isValidValue(value)) {
            return PlayResult.INVALID_INPUT
        }

        if (isIndexGiven(i, j)) {
            return PlayResult.CANNOT_OVERWRITE_GIVEN
        }

        if (value in board.valuesInRow(i)) {
            return PlayResult.ROW_BREAKS_ONE_RULE
        }

        if (value in board.valuesInColumn(j)) {
            return PlayResult.COLUMN_BREAKS_ONE_RULE
        }

        val (blockRow, blockCol) = board.blockContaining(i, j)
        if (value in board.valuesInBlock(blockRow, blockCol)) {
            return PlayResult.BLOCK_BREAKS_ONE_RULE
        }

        board[i, j] = value
        return PlayResult.VALID
    }

    /**
     * Clears the grid, leaving only the givens.
     */
    fun clear() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (!isIndexGiven(i, j)) {
                    board.clearCell(i, j)
                }
            }
        }
    }

    private fun populateGivens() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (!board.isBlank(i, j)) {
                    coordinatesOfGivens.add(i to j)
                }
            }
        }
    }

    private fun isIndexInGrid(i: Int, j: Int) = i >= 0 && i < board.size && j >= 0 && j < board.size
    private fun isIndexGiven(i: Int, j: Int) = coordinatesOfGivens.contains(i to j)
    private fun isValidValue(value: Int) = allowedValues.contains(value)

    companion object {
        fun create(board: SudokuGrid): Pair<Game?, GameCreationError> {
            if (hasInvalidValues(board)) {
                return null to GameCreationError.INVALID_VALUES
            }

            if (board.nonBlankCells().toList().isEmpty()) {
                return null to GameCreationError.NO_GIVENS
            }

            if (!board.gridSatisfiesOneRule()) {
                return null to GameCreationError.INVALID_STATE
            }

            if (board.isSolved()) {
                return null to GameCreationError.ALREADY_SOLVED
            }

            return Game(board) to GameCreationError.NONE
        }

        private fun hasInvalidValues(board: SudokuGrid) =
            board.nonBlankCells().any { (i, j) ->
                board[i, j] !in (0..board.size)
            }
    }
}