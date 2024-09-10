package com.bumpylabs.sudoku.game

interface GameGenerator {
    fun generate(): SudokuGrid
}