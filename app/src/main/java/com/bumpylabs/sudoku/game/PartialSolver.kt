package com.bumpylabs.sudoku.game

import java.util.ArrayList
import java.util.HashMap

/**
 * Solves a Sudoku puzzle if there is a unique solution. The provided grid is not modified.
 * If there are multiple solutions, no guesses are made, and the grid is brought as close
 * to a solution as possible without guessing. There can also be no solutions, in which case the
 * state of the returned grid is not guaranteed.
 *
 * @param initialBoard: The grid to solve. This reference is not modified.
 */
internal class PartialSolver(initialBoard: SudokuGrid) {
    private val board = initialBoard.copy()
    private val size = board.size
    private val allowedValues = 1..size
    private var numCellsSolved = 0

    // Maps values to the number of times a given value has been added to the board
    private val numAllocationsPerValue = HashMap<Int, Int>(size)

    // Maps blocks by their coordinates to whether the block has been filled or not
    private val isFullByBlock = HashMap<Pair<Int, Int>, Boolean>(size)

    init {
        primeNumValueAllocationsMap()
    }

    /**
     * Solves the puzzle, if there is a single solution.
     */
    fun solve(): Solution {
        do {
            val previousNumCellsSolved = numCellsSolved
            solveGrid()
        } while (numCellsSolved > previousNumCellsSolved)

        return if (board.isSolved()) {
            Solution(solutionType = SolutionType.SINGLE_SOLUTION, solution = board)
        } else {
            Solution(solutionType = SolutionType.MULTIPLE_OR_NO_SOLUTIONS, solution = board)
        }
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

        if (candidateCellsInBlock.size == 1) {
            val (i, j) = candidateCellsInBlock.single()
            board[i, j] = value

            numCellsSolved++
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
        return value in board.allowedValuesForCell(i, j)
    }
}