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
    fun generate(k: Int): Array<IntArray> {
        generateFullGrid()
        redact(k)

        return board.copyAsArray()
    }

    private fun generateFullGrid() {
        guessCellsInDiagonalBlocks()
        guessCellsInNonDiagonalBlocks()
    }

    private fun redact(k: Int) {
        TODO()
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

    private fun guessCellsInNonDiagonalBlocks() {
        for ((blockRow, blockCol) in nonDiagonalBlocks()) {
            val valuesToGuessFrom = ArrayDeque((1..size).shuffled())

            for ((row, col) in board.cellsInBlock(blockRow, blockCol)) {
                for (guess in valuesToGuessFrom) {
                    board[row, col] = guess

                    if (board.rowSatisfiesOneRule(row) && board.columnSatisfiesOneRule(col)) {
                        valuesToGuessFrom.remove(guess)
                        break
                    } else {
                        board[row, col] = 0
                    }
                }
            }
        }
    }

    private fun diagonalBlocks() = sequence {
        for (i in 0..<rank) {
            yield(i to i)
        }
    }

    private fun nonDiagonalBlocks() = sequence {
        for (i in 0..<rank) {
            for (j in 0..<rank) {
                if (i != j) {
                    yield(i to j)
                }
            }
        }
    }
}