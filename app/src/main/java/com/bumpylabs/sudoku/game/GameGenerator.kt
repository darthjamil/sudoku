package com.bumpylabs.sudoku.game

/**
 * Generates a random new Sudoku game.
 */
class GameGenerator(private val rank: Int) {
    private val size = rank * rank
    private val board = SudokuGrid(rank)
    private val validValues = (1..size)

    /**
     * Generates a random Sudoku puzzle grid.
     * @param k: The number of cells to obfuscate
     * @return A 2-d array representing the game
     */
    fun generate(k: Int): Array<IntArray> {
        generateFullGrid()
        removeCells(k)

        return board.copyAsArray()
    }

    private fun generateFullGrid() {
        guessCellsInDiagonalBlocks()
        guessCellsInNonDiagonalBlocks()
    }

    private fun removeCells(k: Int) {
        // TODO
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
            guessCellsInBlock(blockRow, blockCol)
        }
    }

    private fun guessCellsInBlock(blockRow: Int, blockCol: Int) {
        val valueToCandidateCells = buildCandidateCellsMap(blockRow, blockCol)
        val sortedByLeastCandidates = valueToCandidateCells.entries.sortedBy { it.value.size }
        val takenCells = mutableSetOf<Pair<Int, Int>>()

        for ((value, candidateCells) in sortedByLeastCandidates) {
            val (i, j) = candidateCells.subtract(takenCells).random()
            board[i, j] = value
            takenCells.add(i to j)
        }
    }

    /*
     * For a given block, builds a map that maps cell-values to all the candidate cells
     * in which that value can be placed
     */
    private fun buildCandidateCellsMap(blockRow: Int, blockCol: Int):
            HashMap<Int, ArrayList<Pair<Int, Int>>> {
        val map = HashMap<Int, ArrayList<Pair<Int, Int>>>(size)

        for ((row, col) in board.cellsInBlock(blockRow, blockCol)) {
            val valuesInRow = board.indices.map { board[row, it] }.filterNot { it == 0 }
            val valuesInCol = board.indices.map { board[it, col] }.filterNot { it == 0 }
            val takenValues = valuesInRow.union(valuesInCol)
            val allowedValues = validValues.subtract(takenValues)

            for (allowedValue in allowedValues) {
                if (map.containsKey(allowedValue)) {
                    map[allowedValue]?.add(row to col)
                } else {
                    map[allowedValue] = arrayListOf(row to col)
                }
            }
        }

        return map
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