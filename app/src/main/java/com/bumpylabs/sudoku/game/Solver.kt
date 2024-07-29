package com.bumpylabs.sudoku.game

/**
 * Solves a game of Sudoku. If there are multiple solutions, a valid solution will be selected
 * at random. If there are no solutions, the response will contain a reference to the original
 * Sudoku board.
 */
class Solver {
    /**
     * Solves the given Sudoku grid. [board] is not modified.
     *
     * @return An object representing the solution, including the solved grid and whether
     * there was one or multiple solutions.
     */
    fun solve(board: SudokuGrid): Solution {
        val uniqueSolutionFinder = PartialSolver(board)
        val uniqueSolution = uniqueSolutionFinder.solve()

        if (uniqueSolution.solutionType == SolutionType.SINGLE_SOLUTION) {
            return uniqueSolution
        }

        val partiallySolvedBoard = uniqueSolution.solution
        val blankCells = blankCells(partiallySolvedBoard).toList().shuffled()

        for ((i, j) in blankCells) {
            val allowedValues = partiallySolvedBoard.allowedValuesForCell(i, j).shuffled()

            for (value in allowedValues) {
                partiallySolvedBoard[i, j] = value
                println("Trying $value at $i, $j.")

                val solution = solve(partiallySolvedBoard)

                if (solution.solutionType != SolutionType.NO_SOLUTION) {
                    return Solution(
                        solutionType = SolutionType.MULTIPLE_SOLUTIONS,
                        solution = solution.solution
                    )
                }

                partiallySolvedBoard.clearCell(i, j)
            }
        }

        return Solution(solutionType = SolutionType.NO_SOLUTION, solution = board)
    }

    private fun blankCells(board: SudokuGrid) = sequence {
        for (i in board.indices) {
            for (j in board.indices) {
                if (board.isBlank(i, j)) {
                    yield(i to j)
                }
            }
        }
    }
}