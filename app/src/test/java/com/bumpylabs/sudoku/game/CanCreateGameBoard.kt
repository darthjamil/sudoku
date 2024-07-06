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
        val initialState = arrayOf(
            intArrayOf(0),
        )
        assertError(initialState, BoardSetupError.TOO_SMALL)
    }

    @Test
    fun board_cannot_be_two_cells() {
        val initialState = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(0, 0),
        )
        assertError(initialState, BoardSetupError.TOO_SMALL)
    }

    @Test
    fun board_cannot_be_rectangle() {
        val initialState = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(0, 0),
            intArrayOf(0, 0),
        )
        assertError(initialState, BoardSetupError.NOT_SQUARE)
    }

    @Test
    fun board_cannot_be_jagged() {
        val initialState = arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, 0),
            intArrayOf(0, 0, 0),
        )
        assertError(initialState, BoardSetupError.NOT_SQUARE)
    }

    @Test
    fun board_cannot_have_no_hints() {
        val initialState = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
        )
        assertError(initialState, BoardSetupError.BLANK_BOARD)
    }

    private fun assertError(initialState: Array<IntArray>, expectedError: BoardSetupError) {
        val (board, error) = GameBoard.build(initialState)

        assertEquals(expectedError, error)
        assertNull(board)
    }
}