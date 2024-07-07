package com.bumpylabs.sudoku.game

/**
 * The types of errors that can occur when creating a new board
 * with a given initial state. Boards must be square, with side length
 * that is a perfect square (usually 9 or 16), and must provide some
 * initial state (numbers filled in some of the cells) that is
 * valid and solvable.
 */
enum class BoardSetupError {
    NONE,
    TOO_SMALL,
    IS_JAGGED_OR_RECTANGULAR,
    NOT_SQUARE,
    NO_GIVENS,
    NOT_VALID,
    ALREADY_SOLVED,
}