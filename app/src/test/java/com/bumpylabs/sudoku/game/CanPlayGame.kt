package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanPlayGame {
    @Test
    fun row_index_can_not_be_negative() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, -1, 0, 2, PlayResult.INDEX_OUT_OF_BOUNDS)
    }

    @Test
    fun row_index_can_not_be_out_of_bounds() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 4, 0, 2, PlayResult.INDEX_OUT_OF_BOUNDS)
    }

    @Test
    fun column_index_can_not_be_negative() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 0, -1, 2, PlayResult.INDEX_OUT_OF_BOUNDS)
    }

    @Test
    fun column_index_can_not_be_out_of_bounds() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 0, 4, 2, PlayResult.INDEX_OUT_OF_BOUNDS)
    }

    @Test
    fun number_can_not_be_negative() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 0, 0, -1, PlayResult.INVALID_INPUT)
    }

    @Test
    fun number_must_be_in_range() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 0, 0, 5, PlayResult.INVALID_INPUT)
    }

    @Test
    fun row_can_not_break_one_rule() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 2, 3, 3, PlayResult.ROW_BREAKS_ONE_RULE)
    }

    @Test
    fun column_can_not_break_one_rule() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        assertPlayError(grid, 2, 3, 4, PlayResult.COLUMN_BREAKS_ONE_RULE)
    }

    @Test
    fun block_can_not_break_one_rule() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertPlayError(grid, 1, 1, 1, PlayResult.BLOCK_BREAKS_ONE_RULE)
    }

    @Test
    fun can_not_replace_given() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        assertPlayError(grid, 0, 0, 1, PlayResult.CANNOT_OVERWRITE_GIVEN)
    }

    @Test
    fun can_not_mutate_grid() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        grid[0][0] = 2

        val refreshedGrid = board.getGrid()
        assertEquals(1, refreshedGrid[0][0])
    }

    @Test
    fun can_play_cell() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        assertPlaySuccess(board, 1, 0, 2)
    }

    @Test
    fun can_reset_cell() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        assertPlaySuccess(board, 1, 0, 2)
        assertPlaySuccess(board, 1, 0, 0)
    }

    @Test
    fun can_change_cell() {
        val grid = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        assertPlaySuccess(board, 1, 0, 2)
        assertPlaySuccess(board, 1, 0, 3)
    }

    @Test
    fun can_get_value_of_cell() {
        val value = 4
        val grid = arrayOf(
            intArrayOf(value, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        assertEquals(value, board.valueAt(0, 0))
        assertEquals(0, board.valueAt(1, 0))
    }

    @Test
    fun can_determine_if_cell_was_given() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )
        val board = createGame(grid)

        assertTrue(board.isGiven(1, 1))
        assertFalse(board.isGiven(2, 2))
    }

    private fun assertPlayError(grid: Array<IntArray>, i: Int, j: Int, value: Int, expectedResult: PlayResult) {
        val board = createGame(grid)
        val result = board.play(i, j, value)

        assertEquals(expectedResult, result)
    }

    private fun assertPlaySuccess(board: GameBoard, i: Int, j: Int, value: Int) {
        val result = board.play(i, j, value)
        assertEquals(PlayResult.VALID, result)

        val newBoardGrid = board.getGrid()
        assertEquals(value, newBoardGrid[i][j])
    }

    private fun createGame(grid: Array<IntArray>): GameBoard {
        val (board, _) = GameBoard.create(grid)
        return board!!
    }
}