package com.bumpylabs.sudoku.game

/**
 * The types of errors that can occur when creating a Sudoku game (such as no givens being
 * provided). Not to be confused with the types of errors that could occur when creating the
 * underling grid itself from a provided array (such as the array size being invalid).
 */
enum class GameCreationError {
    NONE,
    NO_GIVENS,
    INVALID_VALUES,
    INVALID_STATE,
    ALREADY_SOLVED,
}