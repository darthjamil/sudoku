package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanGenerateGame {
    @Test
    fun can_generate_solved_game() {
        val generator = RandomGameGenerator()
        val board = generator.generate()
        val grid = board.copyAsArray()

        println("Game:")
        println(grid.pretty())

        assertFalse(board.isSolved())
        assertEquals(9, board.size)
        grid.forEach { assertEquals(9, it.size) }

        val solver = Solver(board)
        val solution = solver.solve()
        assertEquals(SolutionType.SINGLE_SOLUTION, solution.solutionType)

        println("Solution:")
        print(solution.solution.copyAsArray().pretty())
    }
}