package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanGenerateGame {
    @Test
    fun can_generate_solved_game() {
        val generator = GameGenerator(3)
        val board = generator.generate()
        val grid = board.copyAsArray()

        print(grid.pretty())

        assertTrue(board.isSolved())
        assertEquals(9, board.size)
        grid.forEach { assertEquals(9, it.size) }
    }
}