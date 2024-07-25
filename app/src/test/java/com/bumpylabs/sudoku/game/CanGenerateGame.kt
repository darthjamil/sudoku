package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanGenerateGame {
    @Test
    fun can_generate_solved_game() {
        val generator = GameGenerator(3)
        val grid = generator.generate(0)

        print(grid.pretty())
        assertEquals(9, grid.size)
        grid.forEach { assertEquals(9, it.size) }
        assertTrue(grid.all { row -> row.all { cell -> cell != 0 } })
    }
}