package com.bumpylabs.sudoku.game

/**
 * Solves a Sudoku puzzle if there is a unique solution. The provided grid is not modified.
 * If there are multiple solutions, no guesses are made, and the grid is brought as close
 * to a solution as possible without guessing. There can also be no solutions, in which case the
 * state of the returned grid is not guaranteed.
 *
 * @param initialBoard: The grid to solve. This reference is not modified.
 */
internal class PartialSolver(initialBoard: SudokuGrid) {
    private val board = initialBoard.copy()
    private var numCellsSolved = 0

    /**
     * Solves the puzzle, if there is a single solution.
     */
    fun solve(): Solution {
        do {
            val previousNumCellsSolved = numCellsSolved
            solveGrid()
        } while (numCellsSolved > previousNumCellsSolved)

        return when {
            board.isComplete() ->
                Solution(solutionType = SolutionType.SINGLE_SOLUTION, solution = board)
            hasSolutions() ->
                Solution(solutionType = SolutionType.MULTIPLE_SOLUTIONS, solution = board)
            else ->
                Solution(solutionType = SolutionType.NO_SOLUTION, solution = board)
        }
    }

    private fun solveGrid() {
        for ((i, j) in board.blankCells()) {
            val allowedValues = board.allowedValuesForCell(i, j)

            if (allowedValues.size == 1) {
                board[i, j] = allowedValues.single()
                numCellsSolved++
            }
        }
    }

    private fun hasSolutions(): Boolean {
        return board.blankCells().any { (i, j) ->
            board.allowedValuesForCell(i, j).isNotEmpty()
        }
    }
}