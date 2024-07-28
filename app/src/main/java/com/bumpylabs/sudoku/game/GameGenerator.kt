package com.bumpylabs.sudoku.game

/**
 * Generates a random new Sudoku game.
 */
class GameGenerator(private val rank: Int) {
    private val size = rank * rank
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
        guessCellsInDiagonalBlocks()

        val solver = Solver(board)
        solver.solve() ?: throw NullPointerException("Could not generate solved board.")
    }

    private fun guessCellsInDiagonalBlocks() {
        for ((blockRow, blockCol) in diagonalBlocks()) {
            val valuesToGuessFrom = (1..size).shuffled()
            var i = 0

            for ((row, col) in board.cellsInBlock(blockRow, blockCol)) {
                val guess = valuesToGuessFrom[i]
                board[row, col] = guess
                i++
            }
        }
    }

    private fun diagonalBlocks() = sequence {
        for (i in 0..<rank) {
            yield(i to i)
        }
    }

    private fun removeCells(k: Int) {
        // TODO
    }
}