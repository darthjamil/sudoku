package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanCreateSudokuDataStructure {
    @Test
    fun grid_cannot_be_empty() {
        assertError(emptyArray(), SudokuCreationError.TOO_SMALL)
    }

    @Test
    fun grid_cannot_be_one_cell() {
        val grid = arrayOf(
            intArrayOf(1),
        )
        assertError(grid, SudokuCreationError.TOO_SMALL)
    }

    @Test
    fun grid_cannot_be_binary() {
        val grid = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(0, 0),
        )
        assertError(grid, SudokuCreationError.TOO_SMALL)
    }

    @Test
    fun grid_cannot_be_trinary() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
        )
        assertError(grid, SudokuCreationError.TOO_SMALL)
    }

    @Test
    fun grid_cannot_be_long_rectangle() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, SudokuCreationError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun grid_cannot_be_wide_rectangle() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, SudokuCreationError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun grid_cannot_be_jagged() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, SudokuCreationError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun grid_size_must_be_perfect_square() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, SudokuCreationError.NOT_SQUARE)
    }

    @Test
    fun data_structure_can_be_created() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        val (board, error) = SudokuGrid.create(grid)

        assertEquals(SudokuCreationError.NONE, error)
        assertNotNull(board)
        assertEquals(2, board?.rank)
        assertEquals(4, board?.size)
    }

    private fun assertError(grid: Array<IntArray>, expectedError: SudokuCreationError) {
        val (board, error) = SudokuGrid.create(grid)

        assertEquals(expectedError, error)
        assertNull(board)
    }
}