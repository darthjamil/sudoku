package com.bumpylabs.sudoku.game

/**
 * The result of playing a cell in the grid.
 */
enum class PlayResult {
    VALID,
    INDEX_OUT_OF_BOUNDS,
    INVALID_INPUT,
    CANNOT_OVERWRITE_GIVEN,
    ROW_BREAKS_ONE_RULE,
    COLUMN_BREAKS_ONE_RULE,
    BLOCK_BREAKS_ONE_RULE,
}