package com.bumpylabs.sudoku.game

import kotlin.math.sqrt

/**
 * Generates a valid Sudoku grid with all cells filled with random values.
 *
 * Note: Any traditional backtracking algorithm for generating a new grid must be done
 * offline due to the computational complexity: O(n^n^2). Instead, to keep the entire experience
 * in-app, we use a cheat, where a known good 9x9 grid is randomized. This is fine, but
 * it limits the generator to rank 3.
 */
internal class RandomGridGenerator {
    private val grid = arrayOf(
        intArrayOf(6, 5, 3,   4, 1, 7,   9, 8, 2),
        intArrayOf(1, 8, 9,   5, 2, 3,   6, 4, 7),
        intArrayOf(2, 4, 7,   6, 8, 9,   1, 5, 3),

        intArrayOf(3, 7, 4,   2, 6, 1,   5, 9, 8),
        intArrayOf(9, 1, 5,   7, 4, 8,   3, 2, 6),
        intArrayOf(8, 2, 6,   3, 9, 5,   4, 7, 1),

        intArrayOf(4, 9, 2,   8, 3, 6,   7, 1, 5),
        intArrayOf(7, 6, 1,   9, 5, 2,   8, 3, 4),
        intArrayOf(5, 3, 8,   1, 7, 4,   2, 6, 9),
    )
    private val size = grid.size
    private val rank = sqrt(size.toDouble()).toInt()
    private val numCages = rank
    private val numRowsInCage = rank

    fun generate(): SudokuGrid {
        shuffleRows()
        shuffleColumns()

        return SudokuGrid(grid)
    }

    private fun shuffleRows() {
        val shuffledCages = (0..<numCages)
            .map { cageIndex -> cageIndexToGridRange(cageIndex) }
            .map { gridRange -> grid
                .sliceArray(gridRange)
                .toList()
                .shuffled()
            }
            .shuffled()

        var i = 0
        for (cage in shuffledCages) {
            for (row in cage) {
                grid[i] = row
                i++
            }
        }
    }

    private fun shuffleColumns() {
        val shuffledCages = (0..<numCages)
            .map { cageIndex -> cageIndexToGridRange(cageIndex) }
            .map { gridRange -> gridRange
                .map { col -> getColumn(col).toTypedArray() }
                .shuffled()
            }
            .shuffled()

        var col = 0

        for (cage in shuffledCages) {
            for (column in cage) {

                for ((row, cell) in column.withIndex()) {
                    grid[row][col] = cell
                }

                col++
            }
        }
    }

    private fun getColumn(col: Int) = grid.indices.map { i -> grid[i][col] }

    private fun cageIndexToGridRange(cageIndex: Int): IntRange {
        val first = cageIndex * numRowsInCage
        val last = first + numRowsInCage - 1
        return first..last
    }
}