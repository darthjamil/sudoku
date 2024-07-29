package com.bumpylabs.sudoku.game

/**
 * Generates a random new Sudoku game.
 */
class GameGenerator(private val rank: Int) {
    private val size = rank * rank
    private var board = SudokuGrid(rank)

    /**
     * Generates a random Sudoku puzzle grid.
     * @return A 2-d array representing the game
     */
    fun generate(): SudokuGrid {
        generateFullGrid()
        removeCells()

        return board
    }

    private fun generateFullGrid() {
        guessCellsInDiagonalBlocks()
        solveRestOfGrid()
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

    private fun solveRestOfGrid() {
        val solver = Solver()
        val solution = solver.solve(board)
        board = solution.solution
    }

    private fun removeCells() {
        // TODO
    }
}