package com.bumpylabs.sudoku.game

/**
 * Represents a sudoku game that can be played by first creating it with
 * an initial board state, then attempting to solve it cell by cell. The
 * game will know when it's been solved or is unsolvable.
 *
 * @param initialState: A 2d array that represents the initial
 * state of the game. The constructor is private to prevent creating the game
 * in an invalid state. Use the builder to create the game, which can return
 * an error.
 */
class GameBoard private constructor(val initialState: Array<IntArray>) {

    companion object Initializer {
        private const val MIN_BOARD_SIZE = 3

        /**
         * Creates a new game with the provided initial state.
         *
         * @param initialState: A 2d array representing the initial board. In order to be
         * valid, the board must be square, meet certain size criteria, and the cells
         * that are filled with numbers must represent a valid and solvable game. All other
         * cells are expected to contain Zeros.
         *
         * @return: A new game board and an error. One of the two will be null depending
         * on whether there was an error or not.
         */
        fun build(initialState: Array<IntArray>): Pair<GameBoard?, BoardSetupError> {
            if (initialState.size < MIN_BOARD_SIZE) {
                return null to BoardSetupError.TOO_SMALL
            }

            if (initialState.any { it.size != initialState.size }) {
                return null to BoardSetupError.NOT_SQUARE
            }

            val allCellsAreEmpty = initialState
                .flatMap { row -> row.asIterable() }
                .all { cell -> cell == 0 }

            if (allCellsAreEmpty) {
                return null to BoardSetupError.BLANK_BOARD
            }

            return GameBoard(initialState) to BoardSetupError.NONE
        }
    }

}
