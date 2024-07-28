package com.bumpylabs.sudoku.game

/**
 * Represents the solution to a Sudoku game.
 */
data class Solution(
    val grid: SudokuGrid,
    val hasUniqueSolution: Boolean
)