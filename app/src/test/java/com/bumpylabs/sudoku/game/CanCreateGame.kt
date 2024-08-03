package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanCreateGame {
    @Test
    fun board_must_have_givens() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, GameCreationError.NO_GIVENS)
    }

    @Test
    fun cells_must_have_positive_numbers() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, -1, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, GameCreationError.INVALID_VALUES)
    }

    @Test
    fun cells_must_have_numbers_less_than_or_equal_to_the_rank() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 5, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, GameCreationError.INVALID_VALUES)
    }

    @Test
    fun rows_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 2, 3, 3),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertError(grid, GameCreationError.INVALID_STATE)
    }

    @Test
    fun columns_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 3, 0),
            intArrayOf(0, 0, 4, 0),
        )
        assertError(grid, GameCreationError.INVALID_STATE)
    }

    @Test
    fun blocks_must_have_unique_cells() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 3, 2, 4),
            intArrayOf(0, 0, 4, 3),
            intArrayOf(0, 0, 3, 1),
        )
        assertError(grid, GameCreationError.INVALID_STATE)
    }

    @Test
    fun board_must_not_already_be_solved() {
        val grid = arrayOf(
            intArrayOf(4, 2, 1, 3),
            intArrayOf(1, 3, 2, 4),
            intArrayOf(3, 1, 4, 2),
            intArrayOf(2, 4, 3, 1),
        )
        assertError(grid, GameCreationError.ALREADY_SOLVED)
    }

    @Test
    fun board_can_be_created() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        val (board, error) = SudokuGrid.create(grid)
        assertEquals(SudokuCreationError.NONE, error)
        assertNotNull(board)
        board!!

        val (game, gameError) = Game.create(board!!)
        assertEquals(GameCreationError.NONE, gameError)
        assertNotNull(game)

        assertEquals(2, board.rank)
    }

    private fun assertError(grid: Array<IntArray>, expectedError: GameCreationError) {
        val (board, error) = SudokuGrid.create(grid)
        assertEquals(SudokuCreationError.NONE, error)
        assertNotNull(board)
        board!!

        val (game, gameError) = Game.create(board)
        assertEquals(expectedError, gameError)
        assertNull(game)
    }
}