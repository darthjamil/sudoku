package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * Represents a sudoku game that can be played by first creating it with
 * an initial board state, then attempting to solve it cell by cell. The
 * game will know when it's been solved.
 *
 * @param grid: A 2d array that represents the initial state of the game.
 * The constructor is private to prevent creating the game in an invalid
 * state. Use the static builder to create the game, which can return an error.
 */
class GameBoard private constructor(private val grid: Array<IntArray>) {

    val rank: Int = sqrt(grid.size.toDouble()).toInt()

    fun isValid(): Boolean {
        if (cachedIsValidValue == null) {
            cachedIsValidValue = containsValidValues()
                    && rowsSatisfyOneRule()
                    && columnsSatisfyOneRule()
                    && blocksSatisfyOneRule()
        }

        return cachedIsValidValue!!
    }

    fun isSolved(): Boolean {
        return isComplete()
                && isValid()
    }

    fun play(i: Int, j: Int, value: Int): PlayResult {
        if (!isIndexInGrid(i, j)) {
            return PlayResult.INDEX_OUT_OF_BOUNDS
        }

        if (!isValidValue(value)) {
            return PlayResult.INVALID_INPUT
        }

        if (!rowSatisfiesOneRule(i)) {
            return PlayResult.ROW_BREAKS_ONE_RULE
        }

        if (!columnSatisfiesOneRule(j)) {
            return PlayResult.COLUMN_BREAKS_ONE_RULE
        }

        val (bi, bj) = blockContaining(i, j)
        if (!blockSatisfiesOneRule(bi, bj)) {
            return PlayResult.BLOCK_BREAKS_ONE_RULE
        }

        grid[i][j] = value
        return PlayResult.VALID
    }

    // Cached value for is-valid check. Reset to null as the game progresses.
    private var cachedIsValidValue: Boolean? = null
    private val allowedValues: IntRange = (0..grid.size)

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

    private fun isIndexInGrid(i: Int, j: Int) = i >= 0 && i < grid.size && j >= 0 && j < grid.size

    private fun containsValidValues(): Boolean {
        return grid
            .flatMap { row -> row.asIterable() }
            .all { cell -> isValidValue(cell) }
    }

    private fun isValidValue(cell: Int) = allowedValues.contains(cell)

    private fun rowsSatisfyOneRule() = grid.indices.all { rowSatisfiesOneRule(it) }

    private fun rowSatisfiesOneRule(i: Int) = containsDistincts(grid[i])

    private fun columnsSatisfyOneRule(): Boolean {
        return (grid.indices).all { j ->
            columnSatisfiesOneRule(j)
        }
    }

    private fun columnSatisfiesOneRule(j: Int): Boolean {
        val column = grid.map { row -> row[j] }
        return containsDistincts(column.toIntArray())
    }

    private fun blocksSatisfyOneRule(): Boolean {
        return blocks().all { block ->
            val flattened = block
                .flatMap { blockRow -> blockRow.asIterable() }
                .toIntArray()

            containsDistincts(flattened)
        }
    }

    private fun blockSatisfiesOneRule(bi: Int, bj: Int): Boolean {
        val block = blockAt(bi, bj)
        val flattened = block
            .flatMap { blockRow -> blockRow.asIterable() }
            .toIntArray()

        return containsDistincts(flattened)
    }

    private fun isComplete(): Boolean {
        return grid
            .flatMap { row -> row.asIterable() }
            .none { cell -> isBlank(cell) }
    }

    private fun isBlank(cell: Int) = cell == 0

    private fun containsDistincts(array: IntArray): Boolean {
        val nonZeroEntries = array.filterNot { isBlank(it) }
        return nonZeroEntries.distinct().size == nonZeroEntries.size
    }

    private fun blocks() = sequence {
        for (i in 0..<rank) {
            for (j in 0..<rank) {
                val block = blockAt(i, j)
                yield(block)
            }
        }
    }

    /**
     * Returns the index of the block containing row i and column j of the grid. i and j can be any
     * row and column in the game grid. This will return the row and column index of the block
     * containing i and j.
     */
    private fun blockContaining(i: Int, j: Int): Pair<Int, Int> {
        var blockRowIndex = -1
        var blockColumnIndex = -1

        for (b in 0..<rank) {
            val gridStartIndex = b * rank
            val gridEndIndex = gridStartIndex + rank
            val blockRange = (gridStartIndex..<gridEndIndex)

            if (blockRange.contains(i)) {
                blockRowIndex = b
            }

            if (blockRange.contains(j)) {
                blockColumnIndex = b
            }
        }

        return blockRowIndex to blockColumnIndex
    }

    /**
     * Returns the block in the bi-th row and bj-th column, where bi and bj
     * are indexes of the blocks (from 0 to rank), not of the grid.
     */
    private fun blockAt(bi: Int, bj: Int): Array<IntArray> {
        val blockSize = rank
        val rowIndexInGrid = bi * blockSize
        val columnIndexInGrid = bj * blockSize

        return grid
            .slice(rowIndexInGrid..< rowIndexInGrid + blockSize)
            .map { row -> row.slice(columnIndexInGrid..< columnIndexInGrid + blockSize).toIntArray() }
            .toTypedArray()
    }

}
