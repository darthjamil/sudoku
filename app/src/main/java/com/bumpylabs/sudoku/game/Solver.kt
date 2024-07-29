package com.bumpylabs.sudoku.game

/**
 * Solves a game of Sudoku. If there are multiple solutions, a valid solution will be selected
 * at random. If there are no solutions, the response may contain a reference to the original
 * Sudoku board.
 */
class Solver {
    private var resortedToGuessing = false

    /**
     * Solves the given Sudoku grid. [board] is not modified.
     *
     * @return An object representing the solution, including the solved grid and whether
     * there was one or multiple solutions.
     */
    fun solve(board: SudokuGrid): Solution {
        if (!board.gridSatisfiesOneRule()) {
            return Solution(solutionType = SolutionType.NO_SOLUTION, solution = board)
        }

        val solution = solveRecursive(board)
        val solutionType = when {
            solution.solutionType == SolutionType.NO_SOLUTION -> SolutionType.NO_SOLUTION
            resortedToGuessing -> SolutionType.MULTIPLE_SOLUTIONS
            else -> SolutionType.SINGLE_SOLUTION
        }

        return Solution(solutionType = solutionType, solution = solution.solution)
    }

    private fun solveRecursive(board: SudokuGrid): Solution {
        val uniqueSolutionFinder = PartialSolver(board)
        val uniqueSolution = uniqueSolutionFinder.solve()

        if (uniqueSolution.solutionType != SolutionType.MULTIPLE_SOLUTIONS) {
            return uniqueSolution
        }

        resortedToGuessing = true
        val partiallySolvedBoard = uniqueSolution.solution
        val blankCells = partiallySolvedBoard.blankCells()

        for ((i, j) in blankCells) {
            val allowedValues = partiallySolvedBoard.allowedValuesForCell(i, j).shuffled()

            for (value in allowedValues) {
                partiallySolvedBoard[i, j] = value

                val solution = solveRecursive(partiallySolvedBoard)

                if (solution.solutionType == SolutionType.NO_SOLUTION) {
                    partiallySolvedBoard.clearCell(i, j)
                } else if (solution.solutionType == SolutionType.SINGLE_SOLUTION) {
                    return solution
                } // It never returns multiple solutions; it recurses if that's the case.
            }
        }

        return Solution(solutionType = SolutionType.NO_SOLUTION, solution = board)
    }
}