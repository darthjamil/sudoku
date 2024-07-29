package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * A data structure representing a Sudoku board.
 */
class SudokuGrid {
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

    fun copy() = SudokuGrid(copyAsArray())
    fun copyAsArray() = grid.map { it.clone() }.toTypedArray()

    fun clearCell(row: Int, col: Int) {
        grid[row][col] = 0
    }

    fun isSolved() = isComplete() && gridSatisfiesOneRule()

    fun isComplete(): Boolean {
        return grid
            .all { row ->
                row.all { cell -> !isBlank(cell) }
            }
    }

    fun isBlank(row: Int, col: Int) = Companion.isBlank(grid[row][col])

    fun valuesInRow(row: Int) = grid[row].map { it }.filterNot { isBlank(it) }
    fun rowContainsValue(value: Int, row: Int) = grid[row].any { value == it }
    fun rowSatisfiesOneRule(row: Int) = containsDistincts(grid[row])
    private fun rowsSatisfyOneRule() = grid.indices.all { rowSatisfiesOneRule(it) }

    fun valuesInColumn(col: Int) = grid.indices.map { i -> grid[i][col] }.filterNot { isBlank(it) }
    fun columnContainsValue(value: Int, col: Int) = grid.indices.any { i -> grid[i][col] == value }
    fun columnSatisfiesOneRule(col: Int): Boolean {
        val column = grid.map { row -> row[col] }
        return containsDistincts(column.toIntArray())
    }
    private fun columnsSatisfyOneRule() = grid.indices.all { j -> columnSatisfiesOneRule(j) }

    fun valuesInBlock(blockRow: Int, blockCol: Int): List<Int> {
        val cells = cellsInBlock(blockRow, blockCol)
        val values = cells.map { (i, j) -> grid[i][j] }.filterNot { isBlank(it) }
        return values.toList()
    }

    fun blockContainsValue(value: Int, blockRow: Int, blockCol: Int): Boolean {
        return cellsInBlock(blockRow, blockCol).any { (i, j) ->
            grid[i][j] == value
        }
    }

    fun blockSatisfiesOneRule(blockRow: Int, blockCol: Int): Boolean {
        val valuesInbLock = valuesInBlock(blockRow, blockCol).toIntArray()
        return valuesInbLock.size == size && containsDistincts(valuesInbLock)
    }

    private fun blocksSatisfyOneRule() = blockCoordinates().all { (i, j) -> blockSatisfiesOneRule(i, j) }

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

    fun allowedValuesForCell(row: Int, col: Int): List<Int> {
        val rowValues = valuesInRow(row)
        val columnValues = valuesInColumn(col)
        val (blockRow, blockCol) = blockContaining(row, col)
        val blockValues = valuesInBlock(blockRow, blockCol)

        val takenValues = rowValues.union(columnValues).union(blockValues)
        return (1..size).subtract(takenValues).toList()
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