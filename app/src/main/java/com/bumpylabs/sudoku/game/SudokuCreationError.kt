package com.bumpylabs.sudoku.game

/**
 * The types of errors that can occur when creating a new board
 * with a given initial state. Boards must be square, with side length
 * that is a perfect square (usually 9).
 */
enum class SudokuCreationError {
    NONE,
    TOO_SMALL,
    IS_JAGGED_OR_RECTANGULAR,
    NOT_SQUARE,
}