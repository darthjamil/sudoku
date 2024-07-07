package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanCreateGameBoard {
    @Test
    fun board_cannot_be_empty() {
        assertError(emptyArray(), BoardSetupError.TOO_SMALL)
    }

    @Test
    fun board_cannot_be_one_cell() {
        val grid = arrayOf(
            intArrayOf(0),
        )
        assertError(grid, BoardSetupError.TOO_SMALL)
    }

    @Test
    fun board_cannot_be_two_cells() {
        val grid = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(0, 0),
        )
        assertError(grid, BoardSetupError.TOO_SMALL)
    }

    @Test
    fun board_cannot_be_long_rectangle() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun board_cannot_be_wide_rectangle() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun board_cannot_be_jagged() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.IS_JAGGED_OR_RECTANGULAR)
    }

    @Test
    fun board_must_have_givens() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.NO_GIVENS)
    }

    @Test
    fun board_must_be_perfect_square() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.NOT_SQUARE)
    }

    @Test
    fun cells_must_have_positive_numbers() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, -1, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.NOT_VALID)
    }

    @Test
    fun cells_must_have_numbers_less_than_or_equal_to_the_rank() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 5, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.NOT_VALID)
    }

    @Test
    fun rows_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 2, 3, 3),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, BoardSetupError.NOT_VALID)
    }

    @Test
    fun columns_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 3, 0),
            intArrayOf(0, 0, 4, 0),
        )
        assertError(grid, BoardSetupError.NOT_VALID)
    }

    @Test
    fun blocks_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 3, 2, 4),
            intArrayOf(0, 0, 4, 3),
            intArrayOf(0, 0, 3, 1),
        )
        assertError(grid, BoardSetupError.NOT_VALID)
    }

    @Test
    fun board_must_not_already_be_solved() {
        val grid = arrayOf(
            intArrayOf(4, 2, 1, 3),
            intArrayOf(1, 3, 2, 4),
            intArrayOf(3, 1, 4, 2),
            intArrayOf(2, 4, 3, 1),
        )
        assertError(grid, BoardSetupError.ALREADY_SOLVED)
    }

    @Test
    fun board_can_be_created() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        val (board, error) = GameBoard.create(grid)

        assertEquals(BoardSetupError.NONE, error)
        assertNotNull(board)
        assertEquals(2, board?.rank)
    }

    private fun assertError(grid: Array<IntArray>, expectedError: BoardSetupError) {
        val (board, error) = GameBoard.create(grid)

        assertEquals(expectedError, error)
        assertNull(board)
    }
}