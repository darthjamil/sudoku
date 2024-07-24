package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * Represents a sudoku game that can be played by first creating it with
 * an initial board state, then attempting to solve it cell by cell. The
 * game will know when it's been solved. The game will never allow itself
 * to become in an inconsistent state.
 *
 * @param initialGrid: A 2d array that represents the initial state of the game.
 * The constructor is private to prevent creating the game in an invalid
 * state. Use the static builder to create the game, which can return an error.
 */
class Game private constructor(initialGrid: Array<IntArray>) {

    val rank = sqrt(initialGrid.size.toDouble()).toInt()
    private val board = SudokuGrid(initialGrid)
    private val allowedValues = (0..initialGrid.size)
    private val coordinatesOfGivens: MutableList<Pair<Int, Int>>

    init {
        coordinatesOfGivens = ArrayList()

        for (i in board.indices) {
            for (j in board.indices) {
                if (!board.isBlank(i, j)) {
                    coordinatesOfGivens.add(i to j)
                }
            }
        }
    }

    /**
     * Returns a 2-d array representing the game grid in its current state.
     */
    fun getGrid() = board.copyAsArray()

    /**
     * Returns the value at row i and column j, zero-indexed. A zero represents an empty cell.
     */
    fun valueAt(i: Int, j: Int) = board[i, j]

    /**
     * Returns whether the cell at row i and column j was a given or not.
     */
    fun isGiven(i: Int, j: Int) = isIndexGiven(i, j)

    /**
     * Returns whether the current game is in a solved state.
     */
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

        val oldValue = board[i, j]
        board[i, j] = value

        if (!board.rowSatisfiesOneRule(i)) {
            board[i, j] = oldValue
            return PlayResult.ROW_BREAKS_ONE_RULE
        }

        if (!board.columnSatisfiesOneRule(j)) {
            board[i, j] = oldValue
            return PlayResult.COLUMN_BREAKS_ONE_RULE
        }

        val (blockRow, blockCol) = board.blockContaining(i, j)
        if (!board.blockSatisfiesOneRule(blockRow, blockCol)) {
            board[i, j] = oldValue
            return PlayResult.BLOCK_BREAKS_ONE_RULE
        }

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

    private fun isIndexInGrid(i: Int, j: Int) = i >= 0 && i < board.size && j >= 0 && j < board.size

    private fun isIndexGiven(i: Int, j: Int) = coordinatesOfGivens.contains(i to j)

    private fun isValidValue(value: Int) = allowedValues.contains(value)

    private fun isValid(): Boolean {
        return hasValidValues()
                && board.gridSatisfiesOneRule()
    }

    private fun hasValidValues(): Boolean {
        return board.indices
            .all { i ->
                board.indices.all { j ->
                    isValidValue(board[i, j])
                }
            }
    }

    companion object {
        /**
         * Creates a new game with the provided initial state.
         *
         * @param grid: A 2d array representing the initial board. In order to be
         * valid, the board must be square, have side length that is a perfect square,
         * and the cells that are filled with numbers must represent a valid game.
         * All other cells are expected to contain Zeros.
         *
         * @return: A new game board and an error. One of the two will be null depending
         * on whether there was an error or not.
         */
        fun create(grid: Array<IntArray>): Pair<Game?, BoardSetupError> {
            if (isTooSmall(grid)) {
                return null to BoardSetupError.TOO_SMALL
            }

            if (!isSquare(grid)) {
                return null to BoardSetupError.IS_JAGGED_OR_RECTANGULAR
            }

            if (!isPerfectSquare(grid)) {
                return null to BoardSetupError.NOT_SQUARE
            }

            if (isBlank(grid)) {
                return null to BoardSetupError.NO_GIVENS
            }

            val board = Game(grid)

            if (!board.isValid()) {
                return null to BoardSetupError.NOT_VALID
            }

            if (board.isSolved()) {
                return null to BoardSetupError.ALREADY_SOLVED
            }

            return board to BoardSetupError.NONE
        }

        private fun isTooSmall(grid: Array<IntArray>) = grid.size < 4

        private fun isSquare(grid: Array<IntArray>) = grid.all { row -> row.size == grid.size }

        private fun isPerfectSquare(grid: Array<IntArray>): Boolean {
            val rank = sqrt(grid.size.toDouble())
            return rank.toInt().compareTo(rank) == 0
        }

        private fun isBlank(grid: Array<IntArray>): Boolean {
            return grid
                .all { row ->
                    row.all { cell -> SudokuGrid.isBlank(cell) }
                }
        }
    }
}