package com.bumpylabs.sudoku.game

/**
 * Generates a random new Sudoku game.
 */
class RandomGameGenerator: GameGenerator {
    override fun generate(): SudokuGrid {
        val randomizedGrid = RandomGridGenerator().generate()
        removeCells(randomizedGrid)

        return randomizedGrid
    }

    private fun removeCells(board: SudokuGrid) {
        while (removeCellsIteration(board) > 0) {}
    }

    private fun removeCellsIteration(board: SudokuGrid): Int {
        var numCellsRemoved = 0
        val randomizedCells = board.indices
            .shuffled()
            .zip(board.indices.shuffled())
            .filterNot { (i, j) -> board.isBlank(i, j) }

        for ((row, col) in randomizedCells) {
            val oldValue = board[row, col]
            board.clearCell(row, col)

            val solution = Solver(board).solve()

            if (solution.solutionType == SolutionType.SINGLE_SOLUTION) {
                numCellsRemoved++
            } else {
                board[row, col] = oldValue
            }
        }

        return numCellsRemoved
    }
}