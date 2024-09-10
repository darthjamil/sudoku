package com.bumpylabs.sudoku.game

import org.junit.Test
import org.junit.Assert.*

internal class CanInterrogateSudokuDataStructure {
    private fun sampleBoard() = SudokuGrid(arrayOf(
        intArrayOf(0, 0, 1, 0),
        intArrayOf(0, 0, 0, 4),
        intArrayOf(3, 0, 0, 0),
        intArrayOf(0, 4, 0, 0),
    ))

    @Test
    fun dimensions_match_underlying_grid() {
        val board = sampleBoard()

        assertEquals(4, board.size)
        assertEquals(2, board.rank)
        assertEquals((0..3), board.indices)
    }

    @Test
    fun changing_data_structure_does_not_change_input_array() {
        val grid = arrayOf(
            intArrayOf(0, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )
        val board = sampleBoard()

        board[0, 0] = 1

        assertEquals(1, board[0, 0])
        assertEquals(0, grid[0][0])
    }

    @Test
    fun copy_matches_original() {
        val board = sampleBoard()
        val otherBoard = board.copy()

        assertEquals(board.size, otherBoard.size)
        for (i in board.indices) {
            for (j in board.indices) {
                assertEquals(board[i, j], otherBoard[i, j])
            }
        }
    }

    @Test
    fun changing_copy_does_not_change_original() {
        val board = sampleBoard()
        val otherBoard = board.copy()

        otherBoard[0, 0] = 1

        assertEquals(1, otherBoard[0, 0])
        assertEquals(0, board[0, 0])
    }

    @Test
    fun array_copy_matches_underlying_grid() {
        val board = sampleBoard()

        board[0, 0] = 1
        val underlyingGrid = board.copyAsArray()

        val expectedGrid = arrayOf(
            intArrayOf(1, 0, 1, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        )

        assertEquals(expectedGrid.size, underlyingGrid.size)
        for (i in expectedGrid.indices) {
            for (j in expectedGrid.indices) {
                assertEquals(expectedGrid[i][j], underlyingGrid[i][j])
            }
        }
    }

    @Test
    fun identifies_blank_cell_as_blank() {
        val board = sampleBoard()
        assertTrue(board.isBlank(0, 0))
    }

    @Test
    fun identifies_non_blank_cell_as_non_blank() {
        val board = sampleBoard()
        assertFalse(board.isBlank(0, 2))
    }

    @Test
    fun can_clear_cell() {
        val board = sampleBoard()

        board.clearCell(0, 2)

        assertEquals(0, board[0, 2])
    }

    @Test
    fun identifies_empty_board_as_incomplete() {
        val grid = emptyArray(4)
        val board = SudokuGrid(grid)
        assertFalse(board.isComplete())
    }

    @Test
    fun identifies_incomplete_board() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 0, 1, 2),
        )
        val board = SudokuGrid(grid)
        assertFalse(board.isComplete())
    }

    @Test
    fun identifies_complete_board() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
        )
        val board = SudokuGrid(grid)
        assertTrue(board.isComplete())
    }

    @Test
    fun identifies_incomplete_board_as_unsolved() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 0, 1, 2),
        )
        val board = SudokuGrid(grid)
        assertFalse(board.isSolved())
    }

    @Test
    fun identifies_complete_board_that_breaks_rules_as_unsolved() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
        )
        val board = SudokuGrid(grid)
        assertFalse(board.isSolved())
    }

    @Test
    fun identifies_solved_board() {
        val grid = arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(3, 4, 1, 2),
            intArrayOf(2, 3, 4, 1),
            intArrayOf(4, 1, 2, 3),
        )
        val board = SudokuGrid(grid)
        assertTrue(board.isSolved())
    }

    @Test
    fun get_values_for_empty_row_returns_empty() {
        val board = SudokuGrid(emptyArray(4))
        val valuesInFirstRow = board.valuesInRow(0)
        assertTrue(valuesInFirstRow.isEmpty())
    }

    @Test
    fun get_values_for_row_returns_non_zero_values_in_row() {
        val board = sampleBoard()
        val valuesInFirstRow = board.valuesInRow(0).toTypedArray()
        assertArrayEquals(arrayOf(1), valuesInFirstRow)
    }

    @Test
    fun get_values_for_empty_column_returns_empty() {
        val board = SudokuGrid(emptyArray(4))
        val valuesInFirstColumn = board.valuesInColumn(0)
        assertTrue(valuesInFirstColumn.isEmpty())
    }

    @Test
    fun get_values_for_column_returns_non_zero_values_in_column() {
        val board = sampleBoard()
        val valuesInFirstColumn = board.valuesInColumn(0).toTypedArray()
        assertArrayEquals(arrayOf(3), valuesInFirstColumn)
    }

    @Test
    fun row_satisfies_one_rule_if_all_blank() {
        val board = SudokuGrid(emptyArray(4))
        assertTrue(board.rowSatisfiesOneRule(0))
    }

    @Test
    fun row_satisfies_one_rule_if_incomplete_but_no_duplicates() {
        val board = sampleBoard()
        assertTrue(board.rowSatisfiesOneRule(0))
    }

    @Test
    fun row_satisfies_one_rule_if_complete_and_no_duplicates() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        ))
        assertTrue(board.rowSatisfiesOneRule(0))
    }

    @Test
    fun row_does_not_satisfy_one_rule_if_duplicates() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 0, 0, 1),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(0, 4, 0, 0),
        ))
        assertFalse(board.rowSatisfiesOneRule(0))
    }

    @Test
    fun column_satisfies_one_rule_if_all_blank() {
        val board = SudokuGrid(emptyArray(4))
        assertTrue(board.columnSatisfiesOneRule(0))
    }

    @Test
    fun column_satisfies_one_rule_if_incomplete_but_no_duplicates() {
        val board = sampleBoard()
        assertTrue(board.columnSatisfiesOneRule(0))
    }

    @Test
    fun column_satisfies_one_rule_if_complete_and_no_duplicates() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(2, 0, 0, 4),
            intArrayOf(3, 0, 0, 0),
            intArrayOf(4, 4, 0, 0),
        ))
        assertTrue(board.columnSatisfiesOneRule(0))
    }

    @Test
    fun column_does_not_satisfy_one_rule_if_duplicates() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 0, 0, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 4, 0, 0),
        ))
        assertFalse(board.columnSatisfiesOneRule(0))
    }

    @Test
    fun get_values_for_empty_block_returns_empty() {
        val board = SudokuGrid(emptyArray(4))
        assertTrue(board.valuesInBlock(0, 0).isEmpty())
    }

    @Test
    fun get_values_for_block_returns_values() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 4, 0, 0),
        ))
        assertArrayEquals(arrayOf(1, 4), board.valuesInBlock(1, 0).toTypedArray())
    }

    @Test
    fun empty_block_satisfies_one_rule() {
        val board = SudokuGrid(emptyArray(4))
        assertTrue(board.blockSatisfiesOneRule(0, 0))
    }

    @Test
    fun valid_block_satisfies_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 3, 0, 0),
            intArrayOf(1, 4, 0, 0),
        ))
        assertTrue(board.blockSatisfiesOneRule(1, 0))
    }

    @Test
    fun block_with_duplicates_does_not_satisfy_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 3, 0, 0),
            intArrayOf(1, 2, 0, 0),
        ))
        assertFalse(board.blockSatisfiesOneRule(1, 0))
    }

    @Test
    fun partially_filled_block_with_no_duplicates_satisfies_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 3, 0, 0),
            intArrayOf(1, 0, 0, 0),
        ))
        assertTrue(board.blockSatisfiesOneRule(1, 0))
    }

    @Test
    fun empty_grid_satisfies_one_rule() {
        val board = SudokuGrid(emptyArray(4))
        assertTrue(board.gridSatisfiesOneRule())
    }

    @Test
    fun valid_grid_satisfies_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 4, 1, 0),
            intArrayOf(3, 0, 0, 2),
        ))
        assertTrue(board.gridSatisfiesOneRule())
    }

    @Test
    fun grid_with_row_violation_does_not_satisfy_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 3, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 4, 1, 0),
            intArrayOf(3, 0, 0, 2),
        ))
        assertFalse(board.gridSatisfiesOneRule())
    }

    @Test
    fun grid_with_column_violation_does_not_satisfy_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 4, 3, 0),
            intArrayOf(3, 0, 0, 2),
        ))
        assertFalse(board.gridSatisfiesOneRule())
    }

    @Test
    fun grid_with_block_violation_does_not_satisfy_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 4, 1, 0),
            intArrayOf(3, 0, 0, 1),
        ))
        assertFalse(board.gridSatisfiesOneRule())
    }

    @Test
    fun grid_with_multiple_violations_does_not_satisfy_one_rule() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 2, 1, 0),
            intArrayOf(3, 0, 0, 0),
        ))
        assertFalse(board.gridSatisfiesOneRule())
    }

    @Test
    fun allowed_values_for_empty_grid_returns_all_values() {
        val board = SudokuGrid(emptyArray(4))
        val allowedValues = board.allowedValuesForCell(1, 1).toTypedArray()
        assertArrayEquals(arrayOf(1, 2, 3, 4), allowedValues)
    }

    @Test
    fun allowed_values_checks_row_and_column_and_block() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 0, 1, 0),
            intArrayOf(3, 0, 0, 0),
        ))
        val allowedValues = board.allowedValuesForCell(2, 1).toTypedArray()
        assertArrayEquals(arrayOf(4), allowedValues)
    }

    @Test
    fun blank_cells_returns_coordinates_of_all_blank_cells() {
        val board = sampleBoard()
        val actualCells = board.blankCells().toList().toTypedArray()
        val expectedCells = arrayOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 3),
            Pair(1, 0),
            Pair(1, 1),
            Pair(1, 2),
            Pair(2, 1),
            Pair(2, 2),
            Pair(2, 3),
            Pair(3, 0),
            Pair(3, 2),
            Pair(3, 3),
        )
        assertArrayEquals(expectedCells, actualCells)
    }

    @Test
    fun blank_cells_for_full_grid_returns_empty() {
        val board = SudokuGrid(arrayOf(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(1, 2, 3, 4),
            intArrayOf(1, 2, 3, 4),
        ))
        val actualCells = board.blankCells().toList()
        assertTrue(actualCells.isEmpty())
    }

    @Test
    fun non_blank_cells_returns_coordinates_of_all_non_blank_cells() {
        val board = sampleBoard()
        val actualCells = board.nonBlankCells().toList().toTypedArray()
        val expectedCells = arrayOf(
            Pair(0, 2),
            Pair(1, 3),
            Pair(2, 0),
            Pair(3, 1),
        )
        assertArrayEquals(expectedCells, actualCells)
    }

    @Test
    fun non_blank_cells_for_empty_grid_returns_empty() {
        val board = SudokuGrid(emptyArray(4))
        val actualCells = board.nonBlankCells().toList()
        assertTrue(actualCells.isEmpty())
    }

    @Test
    fun return_block_coordinates_given_grid_coordinates() {
        val board = SudokuGrid(emptyArray(4))
        val check: (Int, Int, Int, Int) -> Unit = { i, j, expectedBlockRow, expectedBlockCol ->
            val actual = board.blockContaining(i, j)
            val expected = Pair(expectedBlockRow, expectedBlockCol)
            assertEquals(expected, actual)
        }

        // top left block
        check(0, 0, 0, 0) // top left
        check(0, 1, 0, 0) // top right
        check(1, 0, 0, 0) // bottom left
        check(1, 1, 0, 0) // bottom right

        // top right block
        check(0, 2, 0, 1) // top left
        check(0, 3, 0, 1) // top right
        check(1, 2, 0, 1) // bottom left
        check(1, 3, 0, 1) // bottom right

        // bottom left block
        check(2, 0, 1, 0) // top left
        check(2, 1, 1, 0) // top right
        check(3, 0, 1, 0) // bottom left
        check(3, 1, 1, 0) // bottom right

        // bottom right block
        check(2, 2, 1, 1) // top left
        check(2, 3, 1, 1) // top right
        check(3, 2, 1, 1) // bottom left
        check(3, 3, 1, 1) // bottom right
    }

    @Test
    fun cells_in_block_returns_grid_cells_in_given_block() {
        val board = sampleBoard()
        val actualCells = board.cellsInBlock(1, 1).toList().toTypedArray()
        val expectedCells = arrayOf(
            Pair(2, 2),
            Pair(2, 3),
            Pair(3, 2),
            Pair(3, 3),
        )
        assertArrayEquals(expectedCells, actualCells)
    }

    @Test
    fun block_coordinates_returns_coordinates_of_all_blocks() {
        val board = sampleBoard()
        val actualCells = board.blockCoordinates().toList().toTypedArray()
        val expectedCells = arrayOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(1, 0),
            Pair(1, 1),
        )
        assertArrayEquals(expectedCells, actualCells)
    }
}