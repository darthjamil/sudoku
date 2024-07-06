package com.bumpylabs.sudoku.game

/**
 * The types of errors that can occur when creating a new board
 * with a given initial state. Boards must be square, usually at least
 * 9x9, and must provide some initial state (numbers filled in some of
 * the cells) that is valid and solvable.
 */
enum class BoardSetupError {
    NONE,
    TOO_SMALL,
    NOT_SQUARE,
    BLANK_BOARD,
    NOT_VALID,
    NOT_SOLVABLE,
}