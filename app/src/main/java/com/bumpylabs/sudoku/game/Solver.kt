package com.bumpylabs.sudoku.game

import java.util.ArrayList
import java.util.HashMap

/**
 * Solves a game of Sudoku.
 *
 * @param board: Represents the Sudoku game to solve. The game can be in any state
 * of completion, including having multiple possible solutions. The input board will
 * be changed in place to solve the grid.
 */
class Solver(private val board: SudokuGrid) {
    private val size = board.size
    private val allowedValues = 1..size
    private var numCellsSolved = 0
    private var numCellsSolvedByGuessing = 0
    private var allowGuessing = false

    // Maps values to the number of times a given value has been added to the board
    private val numAllocationsPerValue = HashMap<Int, Int>(size)

    // Maps blocks by their coordinates to whether the block has been filled or not
    private val isFullByBlock = HashMap<Pair<Int, Int>, Boolean>(size)

    init {
        primeNumValueAllocationsMap()
    }

    /**
     * Solves the game.
     *
     * @return A solution record containing the grid representing the game in a solved and valid
     * state. If there is more than one solution, one of the multiple possible solutions is returned
     * at random. Thus, this can be used for generating a new game grid. Null is returned if the
     * game could not be solved (ex. if the original grid was in an invalid state). The game is
     * solved in place; therefore, the grid that is returned from [solve] is the same reference as
     * the input to the constructor.
     */
    fun solve(): Solution? {
        do {
            val previousNumCellsSolvedByGuessing = numCellsSolvedByGuessing

            do {
                val previousNumCellsSolved = numCellsSolved
                solveGrid()
            } while (numCellsSolved > previousNumCellsSolved)

            allowGuessing = true
            solveGrid()
        } while (numCellsSolvedByGuessing > previousNumCellsSolvedByGuessing)

        return if (board.isSolved()) Solution(board, numCellsSolvedByGuessing == 0) else null
    }

    private fun primeNumValueAllocationsMap() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (!board.isBlank(i, j)) {
                    val value = board[i, j]
                    numAllocationsPerValue[value] = (numAllocationsPerValue[value] ?: 0) + 1
                }
            }
        }
    }

    private fun solveGrid() {
        for (value in allowedValues) {
            if (!valueIsFullyAllocated(value)) {
                solveForValue(value)
            }
        }
    }

    private fun solveForValue(value: Int) {
        for ((i, j) in board.blockCoordinates()) {
            if (!blockIsFull(i, j) && !board.blockContainsValue(value, i, j)) {
                solveForBlock(value, i, j)
            }
        }
    }

    private fun solveForBlock(value: Int, blockRow: Int, blockCol: Int) {
        val candidateCellsInBlock = getCandidateCellsInBlock(value, blockRow, blockCol)

        if (candidateCellsInBlock.isEmpty()) {
            return
        }

        if (candidateCellsInBlock.size == 1) {
            val (i, j) = candidateCellsInBlock.single()
            board[i, j] = value

            numCellsSolved++

            numAllocationsPerValue[value] = (numAllocationsPerValue[value] ?: 0) + 1
        }

        if (candidateCellsInBlock.size > 1 && allowGuessing) {
            val (i, j) = candidateCellsInBlock.random()
            board[i, j] = value

            numCellsSolved++
            numCellsSolvedByGuessing++
            allowGuessing = false

            numAllocationsPerValue[value] = (numAllocationsPerValue[value] ?: 0) + 1
        }
    }

    private fun getCandidateCellsInBlock(value: Int, blockRow: Int, blockCol: Int): List<Pair<Int, Int>> {
        val candidateCells = ArrayList<Pair<Int, Int>>(size)

        for ((i, j) in board.cellsInBlock(blockRow, blockCol)) {
            if (board.isBlank(i, j) && isValidPlay(i, j, value)) {
                candidateCells.add(i to j)
            }
        }

        return candidateCells
    }

    private fun valueIsFullyAllocated(value: Int) = numAllocationsPerValue[value] == size

    private fun blockIsFull(blockRow: Int, blockCol: Int): Boolean {
        if (isFullByBlock[blockRow to blockCol] == true) {
            return true
        }

        val isBlockFull = !board.blockContainsValue(0, blockRow, blockCol)

        if (isBlockFull) {
            isFullByBlock[blockRow to blockCol] = true
        }

        return isBlockFull
    }

    private fun isValidPlay(i: Int, j: Int, value: Int): Boolean {
        val oldValue = board[i, j]

        board[i, j] = value
        val isValid = board.cellSatisfiesOneRule(i, j)
        board[i, j] = oldValue

        return isValid
    }
}