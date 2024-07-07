package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * Represents a sudoku game that can be played by first creating it with
 * an initial board state, then attempting to solve it cell by cell. The
 * game will know when it's been solved.
 *
 * @param grid: A 2d array that represents the initial
 * state of the game. The constructor is private to prevent creating the game
 * in an invalid state. Use the builder to create the game, which can return
 * an error.
 */
class GameBoard private constructor(private val grid: Array<IntArray>) {

    val rank: Int = sqrt(grid.size.toDouble()).toInt()

    companion object Initializer {
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
        fun create(grid: Array<IntArray>): Pair<GameBoard?, BoardSetupError> {
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

            val board = GameBoard(grid)

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
                .flatMap { row -> row.asIterable() }
                .all { cell -> cell == 0 }
        }
    }

    fun isValid(): Boolean {
        return containsValidValues()
                && rowsSatisfyOneRule()
                && columnsSatisfyOneRule()
                && blocksSatisfyOneRule()
    }

    fun isSolved(): Boolean {
        return isComplete()
                && isValid()
    }

    private fun containsValidValues(): Boolean {
        return grid
            .flatMap { row -> row.asIterable() }
            .all { cell -> (0..grid.size).contains(cell) }
    }

    private fun rowsSatisfyOneRule() = grid.all { row -> containsDistincts(row) }

    private fun columnsSatisfyOneRule(): Boolean {
        return (grid.indices).all { j ->
            val column = grid.map { row -> row[j] }
            containsDistincts(column.toIntArray())
        }
    }

    private fun blocksSatisfyOneRule(): Boolean {
        return blocks().all { block ->
            val flattened = block
                .flatMap { blockRow -> blockRow.asIterable() }
                .toIntArray()

            containsDistincts(flattened)
        }
    }

    private fun isComplete(): Boolean {
        return grid
            .flatMap { row -> row.asIterable() }
            .all { cell -> cell != 0 }
    }

    private fun containsDistincts(array: IntArray): Boolean {
        val nonZeroEntries = array.filter { it != 0 }
        return nonZeroEntries.distinct().size == nonZeroEntries.size
    }

    private fun blocks() = sequence {
        for (i in grid.indices step rank) { // for each block row
            for (j in grid.indices step rank) { // for each block column
                val block = grid
                    .slice(i..< i + rank) // get the full grid rows in that block
                    .map { row -> row.slice(j..< j + rank).toIntArray() } // chop the rows to only those columns in the block
                    .toTypedArray()

                yield(block)
            }
        }
    }

}
