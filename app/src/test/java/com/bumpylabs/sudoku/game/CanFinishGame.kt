package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanFinishGame {
    @Test
    fun can_play_rank_three_game() {
        val grid = arrayOf(
            intArrayOf(6, 7, 0,   0, 3, 0,   1, 9, 4),
            intArrayOf(0, 0, 9,   0, 0, 0,   7, 0, 8),
            intArrayOf(0, 8, 0,   9, 0, 4,   0, 3, 0),

            intArrayOf(0, 1, 0,   5, 0, 0,   0, 4, 9),
            intArrayOf(0, 0, 3,   0, 0, 0,   2, 0, 0),
            intArrayOf(5, 2, 0,   0, 0, 9,   0, 1, 0),

            intArrayOf(0, 6, 0,   3, 0, 1,   0, 7, 0),
            intArrayOf(4, 0, 1,   0, 0, 0,   9, 0, 0),
            intArrayOf(9, 5, 7,   0, 6, 0,   0, 8, 1),
        )
        val board = createGame(grid)

        assertPlaySuccess(board, 4, 1, 9)
        assertPlaySuccess(board, 5, 2, 4)
        assertPlaySuccess(board, 3, 2, 6)
        assertPlaySuccess(board, 2, 4, 7)
        assertPlaySuccess(board, 6, 4, 9)
        assertPlaySuccess(board, 1, 1, 4)
        assertPlaySuccess(board, 1, 0, 3)
        assertPlaySuccess(board, 7, 1, 3)
        assertPlaySuccess(board, 6, 2, 8)
        assertPlaySuccess(board, 2, 0, 1)
        assertPlaySuccess(board, 6, 0, 2)
        assertPlaySuccess(board, 4, 4, 4)
        assertPlaySuccess(board, 8, 3, 4)
        assertPlaySuccess(board, 6, 6, 4)
        assertPlaySuccess(board, 8, 6, 3)
        assertPlaySuccess(board, 8, 5, 2)
        assertPlaySuccess(board, 2, 6, 5)
        assertPlaySuccess(board, 2, 8, 6)
        assertPlaySuccess(board, 2, 2, 2)
        assertPlaySuccess(board, 0, 2, 5)
        assertPlaySuccess(board, 3, 4, 2)
        assertPlaySuccess(board, 0, 3, 2)
        assertPlaySuccess(board, 0, 5, 8)
        assertPlaySuccess(board, 1, 7, 2)
        assertPlaySuccess(board, 5, 8, 3)
        assertPlaySuccess(board, 7, 8, 2)
        assertPlaySuccess(board, 7, 7, 6)
        assertPlaySuccess(board, 6, 8, 5)
        assertPlaySuccess(board, 4, 8, 7)
        assertPlaySuccess(board, 4, 7, 5)
        assertPlaySuccess(board, 5, 6, 6)
        assertPlaySuccess(board, 3, 6, 8)
        assertPlaySuccess(board, 3, 5, 3)
        assertPlaySuccess(board, 3, 0, 7)
        assertPlaySuccess(board, 4, 0, 8)
        assertPlaySuccess(board, 4, 3, 1)
        assertPlaySuccess(board, 4, 5, 6)
        assertPlaySuccess(board, 5, 3, 7)
        assertPlaySuccess(board, 5, 4, 8)
        assertPlaySuccess(board, 7, 3, 8)
        assertPlaySuccess(board, 1, 3, 6)
        assertPlaySuccess(board, 7, 5, 7)
        assertPlaySuccess(board, 1, 5, 5)
        assertPlaySuccess(board, 1, 4, 1)
        assertPlaySuccess(board, 7, 4, 5)

        assertTrue(board.isSolved())

        val actualGrid = board.getGrid()
        val expectedGrid = arrayOf(
            intArrayOf(6, 7, 5,   2, 3, 8,   1, 9, 4),
            intArrayOf(3, 4, 9,   6, 1, 5,   7, 2, 8),
            intArrayOf(1, 8, 2,   9, 7, 4,   5, 3, 6),

            intArrayOf(7, 1, 6,   5, 2, 3,   8, 4, 9),
            intArrayOf(8, 9, 3,   1, 4, 6,   2, 5, 7),
            intArrayOf(5, 2, 4,   7, 8, 9,   6, 1, 3),

            intArrayOf(2, 6, 8,   3, 9, 1,   4, 7, 5),
            intArrayOf(4, 3, 1,   8, 5, 7,   9, 6, 2),
            intArrayOf(9, 5, 7,   4, 6, 2,   3, 8, 1),
        )
        assertGridsAreEqual(expectedGrid, actualGrid)
    }

    @Test
    fun partial_solve_is_not_recognized_as_solved() {
        val grid = arrayOf(
            intArrayOf(6, 7, 5,   2, 3, 8,   1, 9, 4),
            intArrayOf(3, 4, 9,   6, 1, 5,   7, 2, 8),
            intArrayOf(1, 8, 2,   9, 7, 4,   5, 3, 6),

            intArrayOf(7, 1, 6,   5, 2, 3,   8, 4, 9),
            intArrayOf(8, 9, 3,   1, 4, 6,   2, 5, 7),
            intArrayOf(5, 2, 4,   7, 8, 9,   6, 1, 3),

            intArrayOf(2, 6, 8,   3, 9, 1,   4, 7, 5),
            intArrayOf(4, 3, 1,   8, 5, 7,   9, 6, 2),
            intArrayOf(9, 5, 7,   4, 6, 2,   0, 0, 0),
        )
        val board = createGame(grid)

        board.play(8, 6, 3)
        assertFalse(board.isSolved())

        board.play(8, 7, 8)
        assertFalse(board.isSolved())

        board.play(8, 8, 1)
        assertTrue(board.isSolved())
    }

    @Test
    fun can_clear_board() {
        val grid = arrayOf(
            intArrayOf(6, 7, 0,   0, 3, 0,   1, 9, 4),
            intArrayOf(0, 0, 9,   0, 0, 0,   7, 0, 8),
            intArrayOf(0, 8, 0,   9, 0, 4,   0, 3, 0),

            intArrayOf(0, 1, 0,   5, 0, 0,   0, 4, 9),
            intArrayOf(0, 0, 3,   0, 0, 0,   2, 0, 0),
            intArrayOf(5, 2, 0,   0, 0, 9,   0, 1, 0),

            intArrayOf(0, 6, 0,   3, 0, 1,   0, 7, 0),
            intArrayOf(4, 0, 1,   0, 0, 0,   9, 0, 0),
            intArrayOf(9, 5, 7,   0, 6, 0,   0, 8, 1),
        )
        val board = createGame(grid)

        assertPlaySuccess(board, 4, 1, 9)
        assertPlaySuccess(board, 5, 2, 4)
        assertPlaySuccess(board, 3, 2, 6)
        assertPlaySuccess(board, 2, 4, 7)
        assertPlaySuccess(board, 6, 4, 9)

        board.clear()

        val updatedGrid = board.getGrid()
        assertGridsAreEqual(grid, updatedGrid)
    }

    private fun createGame(grid: Array<IntArray>): GameBoard {
        val (board, error) = GameBoard.create(grid)
        assertEquals(BoardSetupError.NONE, error)
        assertNotNull(board)

        return board!!
    }

    private fun assertPlaySuccess(board: GameBoard, i: Int, j: Int, value: Int) {
        val result = board.play(i, j, value)
        assertEquals(PlayResult.VALID, result)
    }

    private fun assertGridsAreEqual(g1: Array<IntArray>, g2: Array<IntArray>) {
        assertEquals(g1.size, g2.size)

        for (i in g1.indices) {
            assertArrayEquals(g1[i], g2[i])
        }
    }
}