package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanSolveGame {
    @Test
    fun can_solve_game_with_unique_solution() {
        val grid = arrayOf(
            intArrayOf(6, 7, 5,   2, 3, 8,   1, 0, 4),
            intArrayOf(3, 0, 9,   0, 1, 5,   7, 2, 8),
            intArrayOf(1, 8, 2,   9, 7, 0,   5, 3, 6),

            intArrayOf(7, 1, 6,   5, 0, 3,   8, 4, 9),
            intArrayOf(0, 9, 3,   1, 4, 6,   2, 5, 7),
            intArrayOf(5, 2, 0,   7, 8, 9,   0, 1, 0),

            intArrayOf(2, 6, 8,   3, 9, 1,   4, 0, 5),
            intArrayOf(4, 0, 1,   8, 0, 7,   9, 6, 2),
            intArrayOf(9, 5, 7,   4, 6, 2,   3, 8, 0),
        )

        val board = SudokuGrid(grid)
        val solver = Solver(board)
        val solution = solver.solve()
        val expectedSolution = arrayOf(
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

        assertEquals(SolutionType.SINGLE_SOLUTION, solution.solutionType)

        val actualGrid = solution.solution.copyAsArray()
        print(actualGrid.pretty())

        assertEquals(expectedSolution.size, actualGrid.size)
        expectedSolution.indices.forEach {
            assertArrayEquals(
                "Started with:\n${grid.pretty()}" +
                        "Expected:\n${expectedSolution.pretty()} " +
                        "Got:\n${actualGrid.pretty()}\n",
                expectedSolution[it],
                actualGrid[it])
        }
    }

    @Test
    fun return_no_solution_on_invalid_starting_grid() {
        val grid = arrayOf(
            intArrayOf(6, 6, 0,   0, 3, 0,   1, 9, 4),
            intArrayOf(0, 0, 9,   0, 0, 0,   7, 0, 8),
            intArrayOf(0, 0, 9,   9, 0, 4,   0, 3, 0),

            intArrayOf(0, 1, 0,   5, 0, 0,   0, 4, 9),
            intArrayOf(0, 0, 3,   0, 0, 0,   2, 0, 0),
            intArrayOf(5, 2, 0,   0, 0, 9,   0, 1, 0),

            intArrayOf(0, 6, 0,   3, 0, 1,   0, 7, 0),
            intArrayOf(4, 0, 1,   0, 0, 0,   9, 0, 0),
            intArrayOf(9, 5, 7,   0, 6, 0,   0, 8, 1),
        )

        val board = SudokuGrid(grid)
        val solver = Solver(board)
        val solution = solver.solve()

        assertEquals(SolutionType.NO_SOLUTION, solution.solutionType)
    }

    @Test
    fun return_random_solution_on_multiple_solutions_input() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3,   0, 0, 0,   0, 0, 0),
            intArrayOf(4, 5, 6,   0, 0, 0,   0, 0, 0),
            intArrayOf(7, 8, 9,   0, 0, 0,   0, 0, 0),

            intArrayOf(0, 0, 0,   9, 8, 7,   0, 0, 0),
            intArrayOf(0, 0, 0,   6, 5, 4,   0, 0, 0),
            intArrayOf(0, 0, 0,   3, 2, 1,   0, 0, 0),

            intArrayOf(0, 0, 0,   0, 0, 0,   1, 4, 7),
            intArrayOf(0, 0, 0,   0, 0, 0,   2, 5, 8),
            intArrayOf(0, 0, 0,   0, 0, 0,   3, 6, 9),
        )

        val board = SudokuGrid(grid)
        val solver = Solver(board)
        val solution = solver.solve()

        assertEquals(SolutionType.MULTIPLE_SOLUTIONS, solution.solutionType)
    }
}