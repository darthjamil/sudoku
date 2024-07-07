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
        val (board, _) = GameBoard.create(grid)
        assertNotNull(board)

        val result = board?.play(-1, 0, 2)
        assertEquals(PlayResult.INDEX_OUT_OF_BOUNDS, result)
    }
}