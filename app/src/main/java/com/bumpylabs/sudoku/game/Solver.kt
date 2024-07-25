package com.bumpylabs.sudoku.game

import java.util.ArrayList
import java.util.HashMap

/**
 * Solves a game of Sudoku.
 *
 * @param array: A 2-d array representing the Sudoku game to solve.
 */
class Solver(array: Array<IntArray>) {
    private val board = SudokuGrid(array)
    private val size = board.size

    // Maps values to the number of times a given value has been added to the board
    private val numAllocationsPerValue = HashMap<Int, Int>(size)

    // Maps blocks by their coordinates to whether the block has been filled or not
    private val isFullByBlock = HashMap<Pair<Int, Int>, Boolean>(size)

    /**
     * Solves the game.
     *
     * @return A 2-d array representing the game in a solved and valid state. If there is more
     * than one solution, or the game could not be solved (e.g. because the starting state was
     * invalid to begin with), then null is returned.
     */
    fun solve(): Array<IntArray>? {
        while (solveGrid() > 0) {}

        return if (board.isSolved()) {
            board.copyAsArray()
        } else {
            null
        }
    }

    private fun solveGrid(): Int {
        var numCellsSolved = 0

        for (value in 1..size) {
            if (!valueIsFullyAllocated(value)) {
                numCellsSolved += solveForValue(value)
            }
        }

        return numCellsSolved
    }

    private fun solveForValue(value: Int): Int {
        var numCellsSolved = 0

        for ((i, j) in board.blockCoordinates()) {
            if (!blockIsFull(i, j)) {
                numCellsSolved += solveForBlock(value, i, j)
            }
        }

        return numCellsSolved
    }

    private fun solveForBlock(value: Int, blockRow: Int, blockCol: Int): Int {
        if (blockContainsValue(value, blockRow, blockCol)) {
            return 0
        }

        val candidateCellsInBlock = getCandidateCellsInBlock(value, blockRow, blockCol)

        if (candidateCellsInBlock.size == 1) {
            val (i, j) = candidateCellsInBlock.single()
            board[i, j] = value
            numAllocationsPerValue[value] = (numAllocationsPerValue[value] ?: 0) + 1
            return 1
        }

        return 0
    }

    private fun getCandidateCellsInBlock(value: Int, blockRow: Int, blockCol: Int): List<Pair<Int, Int>> {
        val candidateCells = ArrayList<Pair<Int, Int>>(size)

        for ((i, j) in board.cellsInBlock(blockRow, blockCol)) {
            if (board.isBlank(i, j) && isValidPlacement(i, j, value)) {
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

        val isBlockFull = !blockContainsValue(0, blockRow, blockCol)

        if (isBlockFull) {
            isFullByBlock[blockRow to blockCol] = true
        }

        return isBlockFull
    }

    private fun blockContainsValue(value: Int, blockRow: Int, blockCol: Int): Boolean {
        for ((i, j) in board.cellsInBlock(blockRow, blockCol)) {
            if (board[i, j] == value) {
                return true
            }
        }

        return false
    }

    private fun isValidPlacement(i: Int, j: Int, value: Int): Boolean {
        val oldValue = board[i, j]

        board[i, j] = value
        val isValid = board.cellSatisfiesOneRule(i, j)
        board[i, j] = oldValue

        return isValid
    }
}