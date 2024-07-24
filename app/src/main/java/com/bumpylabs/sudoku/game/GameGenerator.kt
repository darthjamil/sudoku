package com.bumpylabs.sudoku.game

class GameGenerator(private val rank: Int) {
    private val size = rank * rank
    private val guessRange = (1..size)

    fun generate(): Array<IntArray> {
        val grid = Array(size) { IntArray(size) }

        TODO()
    }

    private fun guessCellsInDiagonalBlocks(grid: Array<IntArray>) {
        for (i in 0..<rank) {
            
        }
    }

    private fun guessCell(grid: Array<IntArray>, i: Int, j: Int, tries: Int = 0) {
        grid[i][j] = guessRange.random()
    }

    companion object {
        // maximum number of times to try guessing a random cell value
        private const val MAX_TRIES = 30
    }
}