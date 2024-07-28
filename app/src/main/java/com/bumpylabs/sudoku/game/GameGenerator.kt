package com.bumpylabs.sudoku.game

/**
 * Generates a random new Sudoku game.
 */
class GameGenerator(rank: Int) {
    private val board = SudokuGrid(rank)

    /**
     * Generates a random Sudoku puzzle grid.
     * @param k: The number of cells to obfuscate
     * @return A 2-d array representing the game
     */
    fun generate(k: Int): SudokuGrid {
        generateFullGrid()
        removeCells(k)

        return board
    }

    private fun generateFullGrid() {
        val solver = Solver(board)
        solver.solve() ?: throw NullPointerException("Could not generate solved board.")
    }

    private fun removeCells(k: Int) {
        // TODO
    }
}