package com.bumpylabs.sudoku.game

/**
 * Represents the solution to a Sudoku game.
 */
data class Solution(
    val solutionType: SolutionType,
    val solution: SudokuGrid
)

enum class SolutionType {
    NO_SOLUTION,
    SINGLE_SOLUTION,
    MULTIPLE_SOLUTIONS,
    MULTIPLE_OR_NO_SOLUTIONS,
}