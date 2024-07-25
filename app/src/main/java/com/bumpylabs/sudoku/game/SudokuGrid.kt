package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * A data structure representing a Sudoku board.
 */
internal class SudokuGrid {
    val rank: Int
    val size: Int
    val indices: IntRange
        get() = grid.indices

    private val grid: Array<IntArray>

    constructor(array: Array<IntArray>) {
        grid = array.map { it.clone() }.toTypedArray()
        size = array.size
        rank = sqrt(size.toDouble()).toInt()
    }

    constructor(rank: Int): this(Array(rank * rank) { IntArray(rank * rank) })

    operator fun get(i: Int, j: Int) = grid[i][j]
    operator fun set(i: Int, j: Int, value: Int) {
        grid[i][j] = value
    }

    fun copyAsArray() = grid.map { it.clone() }.toTypedArray()

    fun isSolved() = isComplete() && gridSatisfiesOneRule()

    fun isComplete(): Boolean {
        return grid
            .all { row ->
                row.all { cell -> !isBlank(cell) }
            }
    }

    fun isBlank(row: Int, col: Int) = Companion.isBlank(grid[row][col])

    fun clearCell(row: Int, col: Int) {
        grid[row][col] = 0
    }

    fun gridSatisfiesOneRule(): Boolean {
        return rowsSatisfyOneRule()
                && columnsSatisfyOneRule()
                && blocksSatisfyOneRule()
    }

    fun cellSatisfiesOneRule(row: Int, col: Int): Boolean {
        val (blockRow, blockCol) = blockContaining(row, col)
        return rowSatisfiesOneRule(row)
                && columnSatisfiesOneRule(col)
                && blockSatisfiesOneRule(blockRow, blockCol)
    }

    fun rowSatisfiesOneRule(row: Int) = containsDistincts(grid[row])

    fun columnSatisfiesOneRule(col: Int): Boolean {
        val column = grid.map { row -> row[col] }
        return containsDistincts(column.toIntArray())
    }

    fun blockSatisfiesOneRule(blockRow: Int, blockCol: Int): Boolean {
        val blockCellCoordinates = cellsInBlock(blockRow, blockCol).iterator()
        val blockValues = IntArray(size) {
            val (i, j) = blockCellCoordinates.next()
            grid[i][j]
        }

        return containsDistincts(blockValues)
    }

    private fun rowsSatisfyOneRule(): Boolean {
        return grid.indices.all {
            rowSatisfiesOneRule(it)
        }
    }

    private fun columnsSatisfyOneRule(): Boolean {
        return grid.indices.all { j ->
            columnSatisfiesOneRule(j)
        }
    }

    private fun blocksSatisfyOneRule(): Boolean {
        for ((i, j) in blockCoordinates()) {
            if (!blockSatisfiesOneRule(i, j)) {
                return false
            }
        }

        return true
    }

    /**
     * Returns the coordinates of the block containing the given coordinates of the grid.
     */
    fun blockContaining(row: Int, col: Int): Pair<Int, Int> {
        var blockRowIndex = -1
        var blockColumnIndex = -1

        for (b in 0..<rank) {
            val gridStartIndex = b * rank
            val gridEndIndex = gridStartIndex + rank
            val blockRange = (gridStartIndex..<gridEndIndex)

            if (blockRange.contains(row)) {
                blockRowIndex = b
            }

            if (blockRange.contains(col)) {
                blockColumnIndex = b
            }
        }

        return blockRowIndex to blockColumnIndex
    }

    /**
     * Given a set of block coordinates, returns all the coordinates of the grid that fall
     * within that block.
     */
    fun cellsInBlock(blockRow: Int, blockCol: Int) = sequence {
        val blockSize = rank
        val blockRowStartIndex = blockRow * blockSize
        val blockRowEndIndex = blockRowStartIndex + blockSize - 1
        val blockColStartIndex = blockCol * blockSize
        val blockColEndIndex = blockColStartIndex + blockSize - 1

        for (i in blockRowStartIndex..blockRowEndIndex) {
            for (j in blockColStartIndex..blockColEndIndex) {
                yield(i to j)
            }
        }
    }

    /**
     * Returns a set of coordinates for the blocks in the grid.
     */
    fun blockCoordinates() = sequence {
        for (i in 0..<rank) {
            for (j in 0..<rank) {
                yield(i to j)
            }
        }
    }

    companion object {
        fun containsDistincts(array: IntArray): Boolean {
            val nonBlankEntries = array.filterNot { isBlank(it) }
            return nonBlankEntries.distinct().size == nonBlankEntries.size
        }

        fun isBlank(value: Int) = value == 0
    }
}